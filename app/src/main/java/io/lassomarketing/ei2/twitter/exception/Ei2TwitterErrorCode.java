package io.lassomarketing.ei2.twitter.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Ei2TwitterErrorCode {

    UNKNOWN_SCHEMA("TWITTER-0001"),
    WRONG_UPLOADED_AMOUNT("TWITTER-0002"),
    MISSED_STATISTICS_RECORD("TWITTER-0003"),
    NO_AUDIENCE_DATA("TWITTER-0004"),
    SERIALIZATION_ERROR("TWITTER-0005"),
    CANNOT_BUILD_AUDIENCE_NAME("TWITTER-0006"),
    AUDIENCE_USERS_PAYLOAD_EXCEEDED("TWITTER-0007"),
    PARSING_ERROR("TWITTER-0008"),
    EXCHANGE_ERROR_CODE("TWITTER-0051"),
    EXCHANGE_NO_BODY("TWITTER-0052"),
    EXCHANGE_ERROR_RESPONSE("TWITTER-0053"),
    EXCHANGE_OPERATION_ERROR("TWITTER-0054"),
    EXCHANGE_NO_DATA("TWITTER-0055");

    final String code;

    Ei2TwitterErrorCode(String code) {
        this.code = code;
    }
}
