package io.lassomarketing.ei2.twitter.service;

import io.lassomarketing.ei2.twitter.api.TwitterAudienceUsersApiClient;
import io.lassomarketing.ei2.twitter.dto.DataSourceDto;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AudienceUsersService {

    private final BigQueryService bigQueryService;

    private final TwitterAudienceUsersApiClient twitterAudienceUsersApiClient;

    public int uploadUsers(String socialAccountId, String externalId, Long expireMinutes, DataSourceDto dataSourceDto,
                       int pageNumber, Integer pageSize) {

        List<String> usersData = bigQueryService.loadStringValuesPage(dataSourceDto.getDataSet(),
                                                                      dataSourceDto.getTemporaryTableName(), pageNumber,
                                                                      pageSize);

        //not hash emails as they are stored already hashed in BigQuery
        boolean needSha = !dataSourceDto.getDataType().equalsIgnoreCase(AudienceUploadFieldNames.EMAIL);
        usersData = usersData.stream().map(id -> needSha ? sha256(id) : id).collect(Collectors.toList());

        return twitterAudienceUsersApiClient.uploadUsers(socialAccountId, externalId, expireMinutes,
                                                     dataSourceDto.getDataType(), usersData);
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
