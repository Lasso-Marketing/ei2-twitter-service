package io.lassomarketing.ei2.twitter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadResultsResponse {

    private Integer totalRecords;

    private Integer matchedRecords;

}
