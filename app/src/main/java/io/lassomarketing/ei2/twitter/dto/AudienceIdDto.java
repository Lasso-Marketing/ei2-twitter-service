package io.lassomarketing.ei2.twitter.dto;

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

    private Integer audienceId;

    @JsonProperty("channel")
    private Integer exchangeId;

    private Integer accountId;

}
