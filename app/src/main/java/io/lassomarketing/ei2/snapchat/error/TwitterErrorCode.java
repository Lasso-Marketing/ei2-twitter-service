package io.lassomarketing.ei2.snapchat.error;

import lombok.Getter;

@Getter
public enum TwitterErrorCode {
    NO_AUDIENCE_DATA("TWITTER-0004");

    final String code;

    TwitterErrorCode(String code) {
        this.code = code;
    }
}
