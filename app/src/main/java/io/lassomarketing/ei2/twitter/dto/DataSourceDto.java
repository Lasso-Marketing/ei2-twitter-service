package io.lassomarketing.ei2.twitter.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceDto {

    private String temporaryTableName;

    private String dataSet;

    private AudienceDataType dataType;

}
