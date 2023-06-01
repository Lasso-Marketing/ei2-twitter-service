package io.lassomarketing.ei2.twitter.api.oauth;

import io.lassomarketing.common.twitter.config.TwitterApiProperties;
import io.lassomarketing.common.twitter.config.TwitterConfigProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    private final TwitterApiProperties twitterApiProperties;
    private final TwitterConfigProperties twitterConfigProperties;

    public AuthorizationService(TwitterApiProperties twitterApiProperties, TwitterConfigProperties twitterConfigProperties) {
        this.twitterApiProperties = twitterApiProperties;
        this.twitterConfigProperties = twitterConfigProperties;
    }

    public String buildAuthorizationHeader(HttpMethod method, String uri, List<HttpParameter> httpParameters) {
        OAuthAuthorization authorization = new OAuthAuthorization(
                twitterConfigProperties.getOauthConsumerKey(),
                twitterConfigProperties.getOauthConsumerSecret(),
                twitterConfigProperties.getOauthToken(),
                twitterConfigProperties.getOauthSecret()
        );
        String url = twitterApiProperties.getRootUri() + uri;
        return authorization.generateAuthorizationHeader(method.name(), url, httpParameters);
    }
}
