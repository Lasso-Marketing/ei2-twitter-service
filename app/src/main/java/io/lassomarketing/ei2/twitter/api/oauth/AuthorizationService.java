package io.lassomarketing.ei2.twitter.api.oauth;

import io.lassomarketing.ei2.twitter.config.TwitterApiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final TwitterApiProperties twitterApiProperties;

    public String buildAuthorizationHeader(HttpMethod method, String uri, List<HttpParameter> httpParameters) {
        OAuthAuthorization authorization = new OAuthAuthorization(
                twitterApiProperties.getOauthConsumerKey(),
                twitterApiProperties.getOauthConsumerSecret(),
                twitterApiProperties.getOauthToken(),
                twitterApiProperties.getOauthSecret()
        );
        String url = twitterApiProperties.getRootUri() + uri;
        return authorization.generateAuthorizationHeader(method.name(), url, httpParameters);
    }
}
