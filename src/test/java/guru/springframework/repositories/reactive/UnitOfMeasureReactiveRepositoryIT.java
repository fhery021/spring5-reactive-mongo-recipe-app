package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryIT {

    @Autowired
    UnitOfMeasureReactiveRepository repository;

    private static final String LITER = "liter";

    @Before
    public void setUp() throws Exception {
        repository.deleteAll().block();
    }

    @Test
    public void saveUOM() throws Exception {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription(LITER);

        repository.save(unitOfMeasure).block();

        Long count = repository.count().block();

        assertEquals(Long.valueOf(1L), count);
    }

    @Test
    public void findByDescription() throws Exception {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription(LITER);

        repository.save(unitOfMeasure).block();

        UnitOfMeasure fetchedUOM = repository.findByDescription(LITER).block();
        assertEquals(LITER, fetchedUOM.getDescription());
    }

//        assertEquals(LITER,repository.findByDescription(LITER).block().getDescription());
}
