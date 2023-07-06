package io.lassomarketing.ei2.twitter.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecretsConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public JsonNode twitterDbSecret(@Value("${sm://ei2-twitter-service-db-secret}") String secretValue) throws Exception {
        return readSecretValue(secretValue, "ei2-twitter-service-db-secret");
    }

    @Bean
    public JsonNode twitterApiConfigSecret(@Value("${sm://api-twitter-config}") String secretValue) throws Exception {
        return readSecretValue(secretValue, "api-twitter-config");
    }

    private JsonNode readSecretValue(String secretValue, String secretName) throws Exception {
        try {
            return objectMapper.readTree(secretValue);
        } catch (JsonProcessingException e) {
            throw new Exception("Secret %s contains invalid json".formatted(secretName), e);
        }
    }
}
