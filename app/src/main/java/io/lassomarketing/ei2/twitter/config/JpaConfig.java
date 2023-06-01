package io.lassomarketing.ei2.twitter.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import io.lassomarketing.ei2.config.AppDBConfig;
import io.lassomarketing.ei2.config.DatasourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public JsonNode twitterDbSecret(@Value("${sm://ei2-twitter-service-db-secret}") String secretValue) throws Exception {
        try {
            return objectMapper.readTree(secretValue);
        } catch (JsonProcessingException e) {
            throw new Exception("Secret ei2-twitter-service-db-secret contains invalid json", e);
        }
    }

    @Bean
    @ConfigurationProperties("app.db.twitter")
    public AppDBConfig dbConfig(JsonNode camundaDbSecret) {
        return new AppDBConfig(camundaDbSecret);
    }

    @Bean
    @Primary
    public DataSource camundaDataSource(HikariConfig appPostgresHikariConfig, AppDBConfig dbConfig) {
        DatasourceBuilder dsBuilder = new DatasourceBuilder(dbConfig, appPostgresHikariConfig);

        return dsBuilder.build();
    }

}
