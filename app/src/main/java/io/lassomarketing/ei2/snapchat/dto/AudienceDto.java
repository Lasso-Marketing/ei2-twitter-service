package io.lassomarketing.ei2.snapchat.dto;

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

    @JsonProperty("filename")
    private String filename;

    @JsonProperty("social_account_id")
    private String socialAccountId;

    @JsonProperty("expire_minutes")
    private Long expireMinutes;

    @JsonProperty("external_id")
    private String externalId;

    public String getGroupName(){
        return Optional.ofNullable(filename).orElse(name + "_" + type);
    }

}
