package test;

import java.util.List;
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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

@EnableWebMvc
@EnableTransactionManagement
@ComponentScan
@Configuration
public class SpringConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        ObjectMapper mapper = new ObjectMapper();
        //Registering Hibernate4Module to support lazy objects
        mapper.registerModule(new Hibernate4Module());

        messageConverter.setObjectMapper(mapper);
        converters.add(messageConverter);
        super.configureMessageConverters(converters);
    }

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
        return new HibernateTemplate(sessionFactory);
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