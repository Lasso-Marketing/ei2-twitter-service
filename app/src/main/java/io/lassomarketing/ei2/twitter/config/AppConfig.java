package io.lassomarketing.ei2.twitter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@ConfigurationProperties("app")
public class AppConfig {

    @NotNull
    private Long usersBatchPayloadSize;

}
