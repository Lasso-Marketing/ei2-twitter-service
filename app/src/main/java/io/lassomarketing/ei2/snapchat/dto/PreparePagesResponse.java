package io.lassomarketing.ei2.snapchat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PreparePagesResponse {

    private DataSourceDto dataSource;

    private Integer pageNumber;

}
