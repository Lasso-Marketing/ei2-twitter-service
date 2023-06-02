package io.lassomarketing.ei2.twitter.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariConfig;
import io.lassomarketing.ei2.config.AppDBConfig;
import io.lassomarketing.ei2.config.DatasourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "io.lassomarketing.ei2.snapchat.jpa.repository",
        entityManagerFactoryRef = "ei2EntityManagerFactoryBean",
        transactionManagerRef = "ei2PlatformTransactionManager"
)
public class JpaConfig {

    public static final String EI2 = "EI2";

    @Bean
    @Primary
    @Qualifier(EI2)
    @ConfigurationProperties(prefix = "spring.jpa")
    public JpaProperties ei2JpaProperties() {
        return new JpaProperties();
    }

    @Bean
    @Primary
    @Qualifier(EI2)
    @ConfigurationProperties(prefix = "spring.jpa.hibernate")
    public HibernateProperties ei2HibernateProperties() {
        return new HibernateProperties();
    }

    @Bean
    @Primary
    @Qualifier(EI2)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties ei2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(EI2)
    @Primary
    public DataSource snapchatDataSource(HikariConfig appPostgresHikariConfig, AppDBConfig snapchatDbConfig) {
        DatasourceBuilder dsBuilder = new DatasourceBuilder(snapchatDbConfig, appPostgresHikariConfig);
        
       return dsBuilder.build(); 
    }
    
    @Bean
    @Primary
    @Qualifier(EI2)
    public JpaVendorAdapter ei2JpaVendorAdapter(
            @Qualifier(EI2) JpaProperties ei2JpaProperties
    ) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(ei2JpaProperties.isShowSql());
        if (ei2JpaProperties.getDatabase() != null) {
            adapter.setDatabase(ei2JpaProperties.getDatabase());
        }
        if (ei2JpaProperties.getDatabasePlatform() != null) {
            adapter.setDatabasePlatform(ei2JpaProperties.getDatabasePlatform());
        }
        adapter.setGenerateDdl(ei2JpaProperties.isGenerateDdl());
        return adapter;
    }

    @Bean
    @Primary
    @Qualifier(EI2)
    public LocalContainerEntityManagerFactoryBean ei2EntityManagerFactoryBean(
            @Qualifier(EI2) DataSource snapchatDataSource,
            @Qualifier(EI2) JpaVendorAdapter ei2JpaVendorAdapter,
            @Qualifier(EI2) JpaProperties ei2JpaProperties,
            @Qualifier(EI2) HibernateProperties ei2HibernateProperties
    ) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(snapchatDataSource);
        em.setPackagesToScan("io.lassomarketing.ei2.snapchat.jpa.model");
        em.setJpaVendorAdapter(ei2JpaVendorAdapter);
        em.setJpaPropertyMap(ei2HibernateProperties.determineHibernateProperties(
                ei2JpaProperties.getProperties(),
                new HibernateSettings()
        ));
        return em;
    }

    @Bean
    @Primary
    @Qualifier(EI2)
    public PlatformTransactionManager ei2PlatformTransactionManager(
            @Qualifier(EI2) LocalContainerEntityManagerFactoryBean ei2EntityManagerFactoryBean
    ) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(ei2EntityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Bean
    @Primary
    @Qualifier(EI2)
    public TransactionTemplate ei2TransactionTemplate(
            @Qualifier(EI2) PlatformTransactionManager ei2PlatformTransactionManager
    ) {
        return new TransactionTemplate(ei2PlatformTransactionManager);
    }
    
    @Bean("snapchatDbConfig")
    @ConfigurationProperties("app.db.pg-snapchat")
    public AppDBConfig snapchatDbConfig(JsonNode snapchatDbSecret) {
        return new AppDBConfig(snapchatDbSecret);
    }
    

}
