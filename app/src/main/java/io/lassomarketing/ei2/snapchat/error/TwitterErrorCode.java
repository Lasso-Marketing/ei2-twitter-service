package io.lassomarketing.ei2.snapchat.error;

import lombok.Getter;

@Getter
public enum TwitterErrorCode {

    UNKNOWN_SCHEMA("TWITTER-0001"),
    MISSED_STATISTICS_RECORD("TWITTER-0003"),
    NO_AUDIENCE_DATA("TWITTER-0004");

    final String code;

    TwitterErrorCode(String code) {
        this.code = code;
    }
}
