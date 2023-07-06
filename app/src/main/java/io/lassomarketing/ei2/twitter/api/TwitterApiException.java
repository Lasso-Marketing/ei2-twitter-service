package io.lassomarketing.ei2.twitter.api;

public class TwitterApiException extends RuntimeException {

    public TwitterApiException(String message) {
        super(message);
    }

    public TwitterApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterApiException(Throwable cause) {
        super(cause);
    }
}
