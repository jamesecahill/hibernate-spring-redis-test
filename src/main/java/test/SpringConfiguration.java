package test;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cache.redis.SingletonRedisRegionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan
@Configuration
public class SpringConfiguration {
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setSessionFactory(sessionFactory);
        return htm;
    }

    @Bean
    @Autowired
    public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory) {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
        return hibernateTemplate;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
        sfb.setDataSource(getDataSource());
        sfb.setHibernateProperties(getHibernateProperties());
        sfb.setPackagesToScan(new String[] { "test" });
        return sfb;
    }

    private DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        dataSource.setMaxTotal(100);
        dataSource.setMaxWaitMillis(10000);
        dataSource.setMaxIdle(10);
        return dataSource;
    }

    private Properties getHibernateProperties() {
        Properties properties = new Properties();

        // Standard/General
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.SHOW_SQL, true);
        properties.put(Environment.HBM2DDL_AUTO, "update");

        // Secondary Cache
        properties.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        properties.put(Environment.USE_QUERY_CACHE, true);
        properties.put(Environment.CACHE_REGION_FACTORY, SingletonRedisRegionFactory.class.getName());
        properties.put(Environment.CACHE_REGION_PREFIX, "hibernate");

        // optional setting for second level cache statistics
        properties.put(Environment.GENERATE_STATISTICS, false);
        properties.put(Environment.USE_STRUCTURED_CACHE, true);

        properties.put(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());

        // configuration for Redis that used by hibernate
        properties.put(Environment.CACHE_PROVIDER_CONFIG, "hibernate-redis.properties");

        return properties;
    }
}