package guru.springframework.services;

import guru.springframework.commands.RecipeCommand;
import guru.springframework.converters.RecipeCommandToRecipe;
import guru.springframework.converters.RecipeToRecipeCommand;
import guru.springframework.domain.Recipe;
import guru.springframework.exceptions.NotFoundException;
import guru.springframework.repositories.RecipeRepository;
import guru.springframework.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeReactiveRepository recipeRepository;

    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {

        Mono<Recipe> recipeOptional = recipeRepository.findById(id);

        if (recipeOptional.equals(Mono.empty())){
            throw new NotFoundException("Recipe Not Found. For ID value: " + id );
        }

        return recipeOptional;
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {

        RecipeCommand recipeCommand = recipeToRecipeCommand.convert(findById(id).block());

        //enhance command object with id value
        if(recipeCommand.getIngredients() != null && recipeCommand.getIngredients().size() > 0){
            recipeCommand.getIngredients().forEach(rc -> {
                rc.setRecipeId(recipeCommand.getId());
            });
        }

        return Mono.just(recipeCommand);
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        Recipe savedRecipe = recipeRepository.save(detachedRecipe).block();

        log.debug("Saved RecipeId:" + savedRecipe.getId());
        RecipeCommand recipeCommand = recipeToRecipeCommand.convert(savedRecipe);

        return Mono.just(recipeCommand);
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
        return recipeRepository.deleteById(idToDelete);
    }
}
