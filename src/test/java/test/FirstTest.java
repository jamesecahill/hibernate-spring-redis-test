package test;

import java.util.ArrayList;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// http://lkrnac.net/blog/2014/01/mock-autowired-fields/
public class FirstTest {
    @InjectMocks private TestEntityService svc;
    @Mock private TestEntityRepository repo;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testEntityService() {
        Mockito.when(repo.getAllTestEntities()).thenReturn(new ArrayList<TestEntity>(0));

        Assert.assertEquals(svc.getEntityCount(), 0);

        boolean threw = false;
        try {
            svc.getTestEntity(0);
        } catch (ResourceNotFoundException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }
}
