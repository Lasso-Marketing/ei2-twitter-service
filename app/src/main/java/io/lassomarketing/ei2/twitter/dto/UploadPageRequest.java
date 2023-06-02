package io.lassomarketing.ei2.twitter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadPageRequest {

    private String externalId;

    private DataSourceDto dataSource;

    private Integer pageNumber;

}
