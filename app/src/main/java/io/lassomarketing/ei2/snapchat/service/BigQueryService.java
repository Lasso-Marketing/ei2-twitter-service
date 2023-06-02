package io.lassomarketing.ei2.snapchat.service;

import com.google.cloud.bigquery.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AllArgsConstructor
public class BigQueryService {

    private final BigQuery bigQuery;

    public List<String> loadStringValuesPage(String dataSet, String tableName, int pageNumber, int pageSize) {
        TableResult tableResult = loadPage(dataSet, tableName, pageNumber, pageSize);
        return StreamSupport.stream(tableResult.getValues().spliterator(), false)
                .map(fieldValues -> fieldValues.get(0))
                .filter(Objects::nonNull)
                .map(FieldValue::getStringValue)
                .filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    private TableResult loadPage(String dataSet, String tableName, int pageNumber, int pageSize) {
        TableId tableId = TableId.of(dataSet, tableName);
        return bigQuery.listTableData(tableId, BigQuery.TableDataListOption.pageSize(pageSize),
                                                     BigQuery.TableDataListOption.startIndex(pageNumber * pageSize));
    }

    public BigInteger getTableSize(String dataSet, String tableName) {
        TableId tableId = TableId.of(dataSet, tableName);
        return bigQuery.getTable(tableId).getNumRows();
    }

}
