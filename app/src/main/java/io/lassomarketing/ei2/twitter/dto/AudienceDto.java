package io.lassomarketing.ei2.twitter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class AudienceDto extends AudienceIdDto {

    @JsonProperty("audience_name")
    private String name;

    @JsonProperty("is_custom_name")
    private Boolean customName;

    @JsonProperty("audience_type")
    private String type;

    private String filename;

    private String socialAccountId;

    private Long expireMinutes;

    private String externalId;

}
