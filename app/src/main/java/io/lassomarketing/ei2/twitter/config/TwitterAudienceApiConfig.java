package io.lassomarketing.ei2.twitter.config;

import io.lassomarketing.ei2.twitter.api.TwitterAudienceRestOperations;
import io.lassomarketing.ei2.twitter.interceptor.ApiLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.List;

@Slf4j
@Configuration
public class TwitterAudienceApiConfig {

    private static final String REQUEST_FACTORY_BEAN_NAME = "twitterAudienceApiHttpRequestFactory";

    @Bean
    public ApiLoggingInterceptor twitterAudienceApiLoggingInterceptor() {
        return ApiLoggingInterceptor.builder()
                .prefix("Twitter Audience")
                .logRequestBody(true)
                .logResponseBody(true)
                .debug(false)
                .sensitiveHeaders(List.of(HttpHeaders.AUTHORIZATION))
                .build();
    }

    @Bean
    public TwitterAudienceRestOperations twitterAudienceRestOperations(
            RestTemplateBuilder restTemplateBuilder,
            TwitterApiProperties properties,
            @Qualifier(REQUEST_FACTORY_BEAN_NAME) ClientHttpRequestFactory twitterAudienceApiHttpRequestFactory
    ) {
        return new TwitterAudienceRestOperations(restTemplateBuilder
                .requestFactory(() -> twitterAudienceApiHttpRequestFactory)
                .rootUri(properties.getRootUri())
                .additionalInterceptors(twitterAudienceApiLoggingInterceptor())
                .build()
        );
    }

    @Bean(REQUEST_FACTORY_BEAN_NAME)
    public ClientHttpRequestFactory twitterAudienceApiHttpRequestFactory(TwitterAudienceApiProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeoutMillis());
        requestFactory.setReadTimeout(properties.getReadTimeoutMillis());
        return new BufferingClientHttpRequestFactory(requestFactory);
    }
}
