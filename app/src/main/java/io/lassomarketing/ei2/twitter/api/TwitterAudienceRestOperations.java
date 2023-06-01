package io.lassomarketing.ei2.twitter.api;

import lombok.experimental.Delegate;
import org.springframework.web.client.RestOperations;

/**
 * RestOperations implementation for Twitter Audience.
 */
public class TwitterAudienceRestOperations implements RestOperations {

    @Delegate
    private final RestOperations delegate;

    public TwitterAudienceRestOperations(RestOperations delegate) {
        this.delegate = delegate;
    }
}
