package io.lassomarketing.ei2.snapchat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class AudienceIdDto {

    @JsonProperty("audience_id")
    private Integer audienceId;

    @JsonProperty("channel")
    private Integer exchangeId;

    @JsonProperty("account_id")
    private Integer accountId;

}
