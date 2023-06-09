package io.lassomarketing.ei2.twitter.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Ei2TwitterErrorCode {

    UNKNOWN_SCHEMA("TWITTER-0001"),
    MISSED_STATISTICS_RECORD("TWITTER-0003"),
    NO_AUDIENCE_DATA("TWITTER-0004"),
    CANNOT_BUILD_AUDIENCE_NAME("TWITTER-0006");

    final String code;

    Ei2TwitterErrorCode(String code) {
        this.code = code;
    }
}
