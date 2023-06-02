package io.lassomarketing.ei2.snapchat.jpa.model;

import io.lassomarketing.ei2.snapchat.dto.AudienceDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Audiences")
public class Audience extends CommonEntity<AudienceId> {

    @EmbeddedId
    private AudienceId id;

    @Column
    private String name;

    @Column(nullable = false)
    private String externalId;

    @Column
    private Long expireMinutes;

    @Column(nullable = false)
    private String socialAccountId;

    private Audience(
            Integer audienceId,
            Integer accountId,
            String name,
            String externalId,
            String socialAccountId,
            Long expireMinutes
    ) {
        id = AudienceId.of(audienceId, accountId);
        this.name = name;
        this.externalId = externalId;
        this.expireMinutes = expireMinutes;
        this.socialAccountId = socialAccountId;
    }

    public static Audience create(AudienceDto dto, String externalId) {
        return new Audience(
                dto.getAudienceId(),
                dto.getAccountId(),
                dto.getName(),
                externalId,
                dto.getSocialAccountId(),
                dto.getExpireMinutes()
        );
    }

}
