package io.lassomarketing.ei2.snapchat.service;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.google.common.base.Preconditions.checkArgument;

public class AudienceUtils {

    private AudienceUtils() {
    }

    public static String buildAudienceName(String name, boolean isCustomName, int audienceId, int maxLength) {
        if (isCustomName) {
            return StringUtils.truncate(name, maxLength);
        }
        LocalDate estDate = LocalDate.now(ZoneId.of("America/New_York"));
        String nameSuffix = String.format("_%d_%s", audienceId, estDate);
        checkArgument(maxLength >= nameSuffix.length());
        String truncatedName = StringUtils.truncate(name, maxLength - nameSuffix.length());
        return truncatedName + nameSuffix;
    }
}
