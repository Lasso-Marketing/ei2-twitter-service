package io.lassomarketing.ei2.snapchat.jpa.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Data
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AudienceId implements Serializable {

    @Column(nullable = false)
    private Integer audienceId;

    @Column(nullable = false)
    private Integer accountId;

    public static AudienceId of(int audienceId, int accountId) {
        return new AudienceId(audienceId, accountId);
    }

}
