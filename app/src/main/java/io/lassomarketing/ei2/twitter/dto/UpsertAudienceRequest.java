package io.lassomarketing.ei2.twitter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpsertAudienceRequest {

    AudienceDto existingAudience;

    @NotNull
    AudienceDto requestAudience;
}
