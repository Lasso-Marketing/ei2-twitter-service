package io.lassomarketing.ei2.twitter.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties("common.twitter.api.audience")
public class TwitterAudienceApiProperties {

    @NotNull
    private Duration connectTimeout;

    @NotNull
    private Duration readTimeout;

    public int getConnectTimeoutMillis() {
        return (int) getConnectTimeout().toMillis();
    }

    public int getReadTimeoutMillis() {
        return (int) getReadTimeout().toMillis();
    }

}
