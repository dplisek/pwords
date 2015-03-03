package org.plech.pwords.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("org.plech.pwords.repositories")
public class PersistenceConfig {

    @Autowired
    private Environment env;

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(env.getRequiredProperty("persistence.driver.class"));
        dataSource.setJdbcUrl(env.getRequiredProperty("persistence.jdbc.url"));
        dataSource.setUser(env.getRequiredProperty("persistence.username"));
        dataSource.setPassword(env.getRequiredProperty("persistence.password"));
        dataSource.setAcquireRetryAttempts(10);
        dataSource.setPreferredTestQuery(env.getRequiredProperty("persistence.validation.query"));
        dataSource.setAcquireRetryDelay(500);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setInitialPoolSize(1);
        dataSource.setMaxPoolSize(10);
        dataSource.setAutoCommitOnClose(true);
        dataSource.setIdleConnectionTestPeriod(120);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("org.plech.pwords.domain");
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.connection.driver_class", env.getRequiredProperty("persistence.driver.class"));
        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("persistence.dialect"));
        jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("persistence.hbm2ddl"));
        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(env.getRequiredProperty("persistence.dialect"));
        return jpaVendorAdapter;
    }
}
