package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TestEntityService {
    @Autowired
    private TestEntityRepository testEntityRepository;

    public int getEntityCount() {
        return testEntityRepository.getAllTestEntities().size();
    }

    public TestEntity getTestEntity(int id) {
        TestEntity e = testEntityRepository.findTestEntityById(id);
        e.getName();
        return e;
    }
}
