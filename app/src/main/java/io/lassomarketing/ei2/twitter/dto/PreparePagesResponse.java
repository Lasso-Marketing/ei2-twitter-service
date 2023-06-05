package io.lassomarketing.ei2.twitter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PreparePagesResponse {

    private DataSourceDto dataSource;

    private Integer pageNumber;

    private Integer pageSize;

}
