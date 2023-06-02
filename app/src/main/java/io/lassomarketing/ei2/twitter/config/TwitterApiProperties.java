package io.lassomarketing.ei2.twitter.config;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties("app.twitter")
public class TwitterApiProperties {


    @Getter(AccessLevel.NONE)
    private final JsonNode twitterApiConfigSecret;

    @NotNull
    private String rootUri;

    private String oauthConsumerKey;

    private String oauthConsumerSecret;

    private String oauthToken;

    private String oauthSecret;

    public TwitterApiProperties(JsonNode twitterApiConfigSecret) {
        this.twitterApiConfigSecret = twitterApiConfigSecret;
    }

    public String getOauthConsumerKey() {
        return oauthConsumerKey == null ? twitterApiConfigSecret.at("/oauthConsumerKey").asText() : oauthConsumerKey;
    }

    public String getOauthConsumerSecret() {
        return oauthConsumerSecret == null ? twitterApiConfigSecret.at("/oauthConsumerSecret").asText() : oauthConsumerSecret;
    }

    public String getOauthToken() {
        return oauthToken == null ? twitterApiConfigSecret.at("/oauthToken").asText() : oauthToken;
    }

    public String getOauthSecret() {
        return oauthSecret == null ? twitterApiConfigSecret.at("/oauthSecret").asText() : oauthSecret;
    }

}
