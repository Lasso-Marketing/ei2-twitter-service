package io.lassomarketing.ei2.snapchat.jpa.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class AudienceUploadStatistics extends CommonEntity<String> {

    @Id
    private String traceId;

    private Integer totalRecords;

    private Integer matchedRecords;

    @Override
    public String getId() {
        return traceId;
    }

    public AudienceUploadStatistics(String traceId, Integer totalRecords) {
        this.traceId = traceId;
        this.totalRecords = totalRecords;
        this.matchedRecords = 0;
    }
}
