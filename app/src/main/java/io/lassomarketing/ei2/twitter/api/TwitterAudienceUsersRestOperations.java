package io.lassomarketing.ei2.twitter.api;

import lombok.experimental.Delegate;
import org.springframework.web.client.RestOperations;

/**
 * RestOperations implementation for Twitter Audience users.
 */
public class TwitterAudienceUsersRestOperations implements RestOperations {

    @Delegate
    private final RestOperations delegate;

    public TwitterAudienceUsersRestOperations(RestOperations delegate) {
        this.delegate = delegate;
    }
}
