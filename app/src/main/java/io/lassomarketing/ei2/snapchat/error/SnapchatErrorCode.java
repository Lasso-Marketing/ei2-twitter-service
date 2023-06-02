package io.lassomarketing.ei2.snapchat.error;

import lombok.Getter;

@Getter
public enum SnapchatErrorCode {
    UNKNOWN_SCHEMA("SNPCHAT-0001"),
    WRONG_UPLOADED_AMOUNT("SNPCHAT-0002"),
    MISSED_STATISTICS_RECORD("SNPCHAT-0003"),
    NO_AUDIENCE_DATA("SNPCHAT-0004"),
    EXCHANGE_NO_REFRESH_TOKEN("SNPCHAT-0050"),
    EXCHANGE_ERROR_CODE("SNPCHAT-0051"),
    EXCHANGE_NO_BODY("SNPCHAT-0052"),
    EXCHANGE_NOT_SUCCESS_STATUS("SNPCHAT-0053"),
    EXCHANGE_NO_SUB_RESPONSES("SNPCHAT-0054"),
    EXCHANGE_NOT_SUCCESS_SUB_STATUS("SNPCHAT-0055");

    final String code;

    SnapchatErrorCode(String code) {
        this.code = code;
    }
}
