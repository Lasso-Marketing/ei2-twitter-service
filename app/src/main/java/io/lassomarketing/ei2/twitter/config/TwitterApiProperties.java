package io.lassomarketing.ei2.twitter.config;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.twitter")
public class TwitterApiProperties {

    @NotNull
    private String rootUri;

    private final String oauthConsumerKey;

    private final String oauthConsumerSecret;

    private final String oauthToken;

    private final String oauthSecret;

    public TwitterApiProperties(JsonNode twitterApiConfigSecret) {
        oauthToken = twitterApiConfigSecret.at("/oauthToken").asText();
        oauthSecret = twitterApiConfigSecret.at("/oauthSecret").asText();
        oauthConsumerKey = twitterApiConfigSecret.at("/oauthConsumerKey").asText();
        oauthConsumerSecret = twitterApiConfigSecret.at("/oauthConsumerSecret").asText();
    }

}
