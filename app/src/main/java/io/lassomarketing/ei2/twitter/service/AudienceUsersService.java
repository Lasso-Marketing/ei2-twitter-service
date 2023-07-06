package io.lassomarketing.ei2.twitter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.twitter.api.TwitterAudienceUsersApiClient;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersOperation;
import io.lassomarketing.ei2.twitter.dto.AudienceDataType;
import io.lassomarketing.ei2.twitter.dto.DataSourceDto;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.SERIALIZATION_ERROR;

@Slf4j
@Service
@AllArgsConstructor
public class AudienceUsersService {

    private final BigQueryService bigQueryService;

    private final TwitterAudienceUsersApiClient twitterAudienceUsersApiClient;
    private final ObjectMapper objectMapper;

    public int uploadUsers(String socialAccountId, String externalId, Long expireMinutes, DataSourceDto dataSourceDto,
                       int pageNumber, Integer pageSize) {
        List<String> usersData = getUsersData(dataSourceDto, pageNumber, pageSize);
        String uploadUsersRequestBody = prepareUsersUploadRequest(usersData, dataSourceDto.dataType(),
                                                                  expireMinutes);
        return twitterAudienceUsersApiClient.uploadUsers(socialAccountId, externalId, uploadUsersRequestBody,
                                                         usersData.size());
    }

    public List<String> getUsersData(DataSourceDto dataSourceDto, int pageNumber, Integer pageSize) {
        List<String> usersData = bigQueryService.loadStringValuesPage(dataSourceDto.dataSet(),
                                                                      dataSourceDto.temporaryTableName(), pageNumber,
                                                                      pageSize);
        //not hash emails as they are stored already hashed in BigQuery
        if (dataSourceDto.dataType() != AudienceDataType.EMAIL) {
            usersData = usersData.stream().map(AudienceUsersService::sha256).collect(Collectors.toList());
        }
        return usersData;
    }

    public String prepareUsersUploadRequest(List<String> usersData, AudienceDataType dataType, Long expireMinutes) {
        List<TwitterAudienceUsersOperation> usersOperations =
                twitterAudienceUsersApiClient.getUserOperations(usersData, dataType, expireMinutes);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(usersOperations);
        } catch (JsonProcessingException e) {
            throw new EI2Exception(SERIALIZATION_ERROR.getCode(), e.getMessage());
        }

        return requestBody;
    }

    @SneakyThrows
    private static String sha256(String message) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(message.toLowerCase().getBytes(StandardCharsets.UTF_8));
        return toHex(hash);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            sb.append(String.format("%1$02x", b));
        }
        return sb.toString();
    }

}
