package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestEntityService {
    @Autowired
    private TestEntityRepository testEntityRepository;

    @Transactional
    public int getEntityCount() {
        return testEntityRepository.getAllTestEntities().size();
    }

    @Transactional
    public TestEntity getTestEntity(int id) {
        return testEntityRepository.findTestEntityById(id);
    }
}
