package io.lassomarketing.ei2.snapchat.service;

import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.twitter.config.AppConfig;
import io.lassomarketing.ei2.twitter.api.TwitterAudienceApiClient;
import io.lassomarketing.ei2.twitter.jpa.model.AudienceUploadStatistics;
import io.lassomarketing.ei2.twitter.jpa.repository.AudienceUploadStatisticsRepository;
import io.lassomarketing.ei2.twitter.dto.AudienceDto;
import io.lassomarketing.ei2.twitter.dto.DataSourceDto;
import io.lassomarketing.ei2.twitter.dto.PreparePagesResponse;
import io.lassomarketing.ei2.twitter.dto.UpsertAudienceRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.lassomarketing.ei2.snapchat.error.TwitterErrorCode.NO_AUDIENCE_DATA;


@Slf4j
@Service
@AllArgsConstructor
public class TwitterService {

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

        if (!isAudienceNameEquals(existingAudience, newAudience)) {
            return twitterAudienceApiClient.update(existingAudience.getExternalId(), newAudience);
        }
        return existingAudience.getExternalId();
    }

    private static boolean isAudienceNameEquals(AudienceDto existingAudience, AudienceDto newAudience) {
        return Objects.equals(existingAudience.getName(), newAudience.getName());
    }
//
//    @Transactional
//    public void uploadAudiencePage(String externalId, DataSourceDto dataSourceDto, int pageNumber, String traceId) {
//        int matchedRecords = audienceUsersService.uploadUsers(externalId, dataSourceDto, pageNumber);
//        audienceUploadStatisticsRepository.findById(traceId)
//                .map(Functions.peek(s -> s.setMatchedRecords(s.getMatchedRecords() + matchedRecords)))
//                .orElseThrow(() -> new EI2Exception(MISSED_STATISTICS_RECORD.getCode(), traceId));
//    }
//
    public List<PreparePagesResponse> preparePages(List<DataSourceDto> dataSources, String traceId) {
        int totalRecords = 0;

        List<PreparePagesResponse> pagesResponse = new ArrayList<>();

        for (DataSourceDto dataSource : dataSources) {
            long tableBytes = bigQueryService.getTableBytes(dataSource.getDataSet(),
                                                            dataSource.getTemporaryTableName());
            long usersAmount = bigQueryService.getTableSize(dataSource.getDataSet(), dataSource.getTemporaryTableName())
                    .intValueExact();
            long payloadSizeLimit = appConfig.getUsersBatchPayloadSize();

            int usersPerBatch = (int) (payloadSizeLimit / (double) tableBytes * usersAmount);
            int batchesAmount = usersAmount / usersPerBatch + usersAmount % usersPerBatch > 0 ? 1 : 0;

            for (int i = 0; i < batchesAmount ; i++) {
                pagesResponse.add(new PreparePagesResponse(dataSource, i, usersPerBatch));
            }

            totalRecords += usersAmount;
        }

        if (pagesResponse.isEmpty()) {
            throw new EI2Exception(NO_AUDIENCE_DATA.getCode(),
                                   dataSources.stream().map(DataSourceDto::getTemporaryTableName)
                                           .collect(Collectors.joining(", ")));
        }

        log.info("prepared {} pages for tables: {}", pagesResponse.size(), dataSources.stream()
                .map(DataSourceDto::getTemporaryTableName).collect(Collectors.joining(",")));

        AudienceUploadStatistics statistics = new AudienceUploadStatistics(traceId, totalRecords);
        audienceUploadStatisticsRepository.save(statistics);

        return pagesResponse;
    }

//    public UploadResultsResponse getUploadResults(AudienceIdDto audienceIdDto, String traceId) {
//        AudienceUploadStatistics statistics = audienceUploadStatisticsRepository.findById(traceId)
//                .orElseThrow(() -> new EI2Exception(MISSED_STATISTICS_RECORD.getCode(), traceId));
//        log.info("Uploaded {} audience. TotalRecords: {}, MatchedRecords: {}", audienceIdDto.getAudienceId(),
//                 statistics.getTotalRecords(), statistics.getMatchedRecords());
//        return new UploadResultsResponse(statistics.getTotalRecords(), statistics.getMatchedRecords());
//    }
}
