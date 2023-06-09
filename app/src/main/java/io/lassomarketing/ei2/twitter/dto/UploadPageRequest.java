package io.lassomarketing.ei2.twitter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadPageRequest {

    private String socialAccountId;
    private String externalId;
    private Long expireMinutes;
    private DataSourceDto dataSource;
    private Integer pageNumber;
    private Integer pageSize;

}
