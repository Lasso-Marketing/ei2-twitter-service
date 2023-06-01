package io.lassomarketing.ei2.twitter.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TwitterAudienceUsersDto {

    @JsonProperty("success_count")
    private Integer successCount;

    @JsonProperty("total_count")
    private Integer totalCount;

}
