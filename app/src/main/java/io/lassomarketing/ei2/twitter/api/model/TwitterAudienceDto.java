package io.lassomarketing.ei2.twitter.api.model;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class TwitterAudienceDto {

    @NotNull
    private String id;
}
