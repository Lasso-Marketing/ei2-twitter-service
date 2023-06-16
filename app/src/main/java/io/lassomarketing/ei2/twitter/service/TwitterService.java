package io.lassomarketing.ei2.twitter.service;

import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.twitter.config.AppConfig;
import io.lassomarketing.ei2.twitter.api.TwitterAudienceApiClient;
import io.lassomarketing.ei2.twitter.dto.AudienceIdDto;
import io.lassomarketing.ei2.twitter.dto.UploadResultsResponse;
import io.lassomarketing.ei2.twitter.jpa.model.AudienceUploadStatistics;
import io.lassomarketing.ei2.twitter.jpa.repository.AudienceUploadStatisticsRepository;
import io.lassomarketing.ei2.twitter.dto.AudienceDto;
import io.lassomarketing.ei2.twitter.dto.DataSourceDto;
import io.lassomarketing.ei2.twitter.dto.PreparePagesResponse;
import io.lassomarketing.ei2.twitter.dto.UpsertAudienceRequest;
import io.lassomarketing.ei2.twitter.utils.Functions;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.MISSED_STATISTICS_RECORD;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.NO_AUDIENCE_DATA;


@Slf4j
@Service
@AllArgsConstructor
public class TwitterService {

    private final static int PAYLOAD_SIZE_TEST_USERS_AMOUNT = 1000;

    private final AudienceUsersService audienceUsersService;

    private final BigQueryService bigQueryService;

    private final AppConfig appConfig;

    private final AudienceUploadStatisticsRepository audienceUploadStatisticsRepository;

    private final TwitterAudienceApiClient twitterAudienceApiClient;

    public String upsertAudience(UpsertAudienceRequest request) {
        AudienceDto existingAudience = request.getExistingAudience();
        AudienceDto newAudience = request.getRequestAudience();
        if (existingAudience == null) {
            return twitterAudienceApiClient.create(newAudience);
        }

        if (!Objects.equals(existingAudience.getName(), newAudience.getName())) {
            return twitterAudienceApiClient.update(existingAudience.getExternalId(), newAudience);
        }
        return existingAudience.getExternalId();
    }


    @Transactional
    public void uploadAudiencePage(String socialAccountId, String externalId, Long expireMinutes,
                                   DataSourceDto dataSourceDto, int pageNumber, Integer pageSize, String traceId) {

        int matchedRecords = audienceUsersService.uploadUsers(socialAccountId, externalId, expireMinutes, dataSourceDto,
                                                              pageNumber, pageSize);
        audienceUploadStatisticsRepository.findById(traceId)
                .map(Functions.peek(s -> s.setMatchedRecords(s.getMatchedRecords() + matchedRecords)))
                .orElseThrow(() -> new EI2Exception(MISSED_STATISTICS_RECORD.getCode(), traceId));
    }

    public List<PreparePagesResponse> preparePages(List<DataSourceDto> dataSources, String traceId) {
        int totalRecords = 0;

        List<PreparePagesResponse> responsePages = new ArrayList<>();

        for (DataSourceDto dataSource : dataSources) {
            long dataSourceUsersAmount = addDataSourcePages(responsePages, dataSource);
            totalRecords += dataSourceUsersAmount;
        }

        if (responsePages.isEmpty()) {
            throw new EI2Exception(NO_AUDIENCE_DATA.getCode(),
                                   dataSources.stream().map(DataSourceDto::getTemporaryTableName)
                                           .collect(Collectors.joining(", ")));
        }

        log.info("prepared {} pages for tables: {}", responsePages.size(), dataSources.stream()
                .map(DataSourceDto::getTemporaryTableName).collect(Collectors.joining(",")));

        AudienceUploadStatistics statistics = new AudienceUploadStatistics(traceId, totalRecords);
        audienceUploadStatisticsRepository.save(statistics);

        return responsePages;
    }

    private long addDataSourcePages(List<PreparePagesResponse> pagesResponse, DataSourceDto dataSource) {
        //get first 1000 users to estimate actual payload size per user, use stub expireMinutes=0
        List<String> usersData = audienceUsersService.getUsersData(dataSource, 0, PAYLOAD_SIZE_TEST_USERS_AMOUNT);
        String uploadUsersRequest = audienceUsersService.prepareUsersUploadRequest(usersData, dataSource.getDataType(),
                                                                                   0L);
        float payloadSizeFactor = appConfig.getUploadAudiencePayloadFactor();
        long testLength = uploadUsersRequest.getBytes().length;
        long payloadSizeLimit = appConfig.getUploadAudiencePayloadLimit();

        int usersPerBatch = (int) (payloadSizeFactor * payloadSizeLimit / (double) testLength * usersData.size());

        long totalUsersAmount = bigQueryService
                .getTableSize(dataSource.getDataSet(), dataSource.getTemporaryTableName()).longValueExact();

        int batchesAmount = (int) (totalUsersAmount / usersPerBatch + (totalUsersAmount % usersPerBatch > 0 ? 1 : 0));

        for (int i = 0; i < batchesAmount ; i++) {
            pagesResponse.add(new PreparePagesResponse(dataSource, i, usersPerBatch));
        }

        return totalUsersAmount;
    }

    public UploadResultsResponse getUploadResults(AudienceIdDto audienceIdDto, String traceId) {
        AudienceUploadStatistics statistics = audienceUploadStatisticsRepository.findById(traceId)
                .orElseThrow(() -> new EI2Exception(MISSED_STATISTICS_RECORD.getCode(), traceId));
        log.info("Uploaded {} audience. TotalRecords: {}, MatchedRecords: {}", audienceIdDto.getAudienceId(),
                 statistics.getTotalRecords(), statistics.getMatchedRecords());
        return new UploadResultsResponse(statistics.getTotalRecords(), statistics.getMatchedRecords());
    }

}
