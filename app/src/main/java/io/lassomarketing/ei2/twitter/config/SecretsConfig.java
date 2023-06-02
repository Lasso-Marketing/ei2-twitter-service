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
        try {
            return objectMapper.readTree(secretValue);
        } catch (JsonProcessingException e) {
            throw new Exception("Secret ei2-twitter-service-db-secret contains invalid json", e);
        }
    }

    @Bean
    public JsonNode twitterApiConfigSecret(@Value("${sm://api-twitter-sandbox-config}") String secretValue) throws Exception {
        try {
            return objectMapper.readTree(secretValue);
        } catch (JsonProcessingException e) {
            throw new Exception("Secret api-twitter-config contains invalid json", e);
        }
    }
}
