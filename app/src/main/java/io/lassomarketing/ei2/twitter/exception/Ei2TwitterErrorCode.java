package io.lassomarketing.ei2.twitter.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Ei2TwitterErrorCode {

    YOUR_ERROR_NAME("TWITTER-0001");

    String code;

    Ei2TwitterErrorCode(String code) {
        this.code = code;
    }
}
