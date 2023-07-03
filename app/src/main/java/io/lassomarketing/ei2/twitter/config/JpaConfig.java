package io.lassomarketing.ei2.twitter.config;


import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariConfig;
import io.lassomarketing.ei2.config.AppDBConfig;
import io.lassomarketing.ei2.config.DatasourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    @ConfigurationProperties("app.db.twitter")
    public AppDBConfig dbConfig(JsonNode twitterDbSecret) {
        return new AppDBConfig(twitterDbSecret);
    }

    @Bean
    @Primary
    public DataSource twitterDataSource(HikariConfig appPostgresHikariConfig, AppDBConfig dbConfig) {
        DatasourceBuilder dsBuilder = new DatasourceBuilder(dbConfig, appPostgresHikariConfig);

        return dsBuilder.build();
    }

}
