package test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TestEntityRepository {
    @Autowired
    HibernateTemplate hibernateTemplate;

    public List<TestEntity> getAllTestEntities() {
        return hibernateTemplate.loadAll(TestEntity.class);
    }

    public TestEntity findTestEntityById(int id) {
        return hibernateTemplate.get(TestEntity.class, id);
    }
}
