package io.lassomarketing.ei2.twitter.utils;

import io.lassomarketing.ei2.common.exception.EI2Exception;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;

import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.CANNOT_BUILD_AUDIENCE_NAME;

/**
 * Copied from k8s-exchange-service
 */
public class AudienceUtils {

    private AudienceUtils() {
    }

    public static String buildAudienceName(String name, boolean isCustomName, int audienceId, int maxLength) {
        if (isCustomName) {
            return StringUtils.truncate(name, maxLength);
        }
        LocalDate estDate = LocalDate.now(ZoneId.of("America/New_York"));
        String nameSuffix = String.format("_%d_%s", audienceId, estDate);
        if (nameSuffix.length() >= maxLength) {
            throw new EI2Exception(CANNOT_BUILD_AUDIENCE_NAME.getCode(), maxLength);
        }
        String truncatedName = StringUtils.truncate(name, maxLength - nameSuffix.length());
        return truncatedName + nameSuffix;
    }
}
