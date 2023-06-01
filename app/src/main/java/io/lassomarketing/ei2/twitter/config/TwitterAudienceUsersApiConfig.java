package io.lassomarketing.ei2.twitter.config;

import io.lassomarketing.ei2.twitter.api.TwitterAudienceUsersRestOperations;
import io.lassomarketing.ei2.twitter.interceptor.ApiLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;

@Slf4j
@Configuration
public class TwitterAudienceUsersApiConfig {

    private static final String REQUEST_FACTORY_BEAN_NAME = "twitterAudienceFileApiHttpRequestFactory";

    @Bean
    public ApiLoggingInterceptor twitterAudienceFileApiLoggingInterceptor() {
        return ApiLoggingInterceptor.builder()
                .prefix("Twitter Audience Users")
                .logRequestBody(false)
                .logResponseBody(false)
                .debug(false)
                .sensitiveHeaders(List.of(HttpHeaders.AUTHORIZATION))
                .build();
    }

    @Bean
    public TwitterAudienceUsersRestOperations twitterAudienceFileRestOperations(
            RestTemplateBuilder restTemplateBuilder,
            TwitterApiProperties properties,
            @Qualifier(REQUEST_FACTORY_BEAN_NAME) ClientHttpRequestFactory twitterAudienceFileApiHttpRequestFactory
    ) {
        return new TwitterAudienceUsersRestOperations(restTemplateBuilder
                .requestFactory(() -> twitterAudienceFileApiHttpRequestFactory)
                .rootUri(properties.getRootUri())
                .additionalInterceptors(twitterAudienceFileApiLoggingInterceptor())
                .build()
        );
    }

    @Bean(REQUEST_FACTORY_BEAN_NAME)
    public ClientHttpRequestFactory twitterAudienceFileApiHttpRequestFactory(TwitterAudienceUsersApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        requestFactory.setConnectTimeout(properties.getConnectTimeoutMillis());
        requestFactory.setReadTimeout(properties.getReadTimeoutMillis());
        return requestFactory;
    }
}
