package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersDto;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersOperation;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
import io.lassomarketing.ei2.twitter.config.AppConfig;
import io.lassomarketing.ei2.twitter.dto.AudienceDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.AUDIENCE_USERS_PAYLOAD_EXCEEDED;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.SERIALIZATION_ERROR;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.WRONG_UPLOADED_AMOUNT;

@Slf4j
@Service
public class TwitterAudienceUsersApiClient extends TwitterApiClient {

    private static final String URI = "/accounts/%s/custom_audiences/%s/users";

    private final TwitterAudienceUsersRestOperations twitterAudienceUsersRestOperations;

    private final AppConfig appConfig;

    protected TwitterAudienceUsersApiClient(
            ObjectMapper objectMapper,
            TwitterAudienceUsersRestOperations twitterAudienceUsersRestOperations,
            AuthorizationService authorizationService,
            AppConfig appConfig) {
        super(objectMapper, authorizationService);
        this.twitterAudienceUsersRestOperations = twitterAudienceUsersRestOperations;
        this.appConfig = appConfig;
    }

    /**
     * <a href="https://developer.twitter.com/en/docs/twitter-ads-api/audiences/api-reference/custom-audience-users">
     *      Twitter upload users documentation </a>
     */
    public int uploadUsers(String twitterAccountId, String audienceExternalId, Long expireMinutes,
                           AudienceDataType dataType, List<String> usersData) {
        String uri = String.format(URI, twitterAccountId, audienceExternalId);
        HttpHeaders headers = createHeaders(HttpMethod.POST, uri);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<TwitterAudienceUsersOperation> usersOperations = getUserOperations(usersData, dataType, expireMinutes);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(usersOperations);
        } catch (JsonProcessingException e) {
            throw new EI2Exception(SERIALIZATION_ERROR.getCode(), e.getMessage());
        }
        if (requestBody.length() >= appConfig.getUploadAudiencePayloadLimit()) {
            throw new EI2Exception(AUDIENCE_USERS_PAYLOAD_EXCEEDED.getCode(), requestBody.length(),
                                   appConfig.getUploadAudiencePayloadLimit());
        }

        HttpEntity<?> httpEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<TwitterApiResponse> responseEntity =
                twitterAudienceUsersRestOperations.exchange(uri, HttpMethod.POST, httpEntity, TwitterApiResponse.class);

        TwitterAudienceUsersDto usersDto = parseResponseData(responseEntity, TwitterAudienceUsersDto.class);
        Integer successCount = usersDto.getSuccessCount();
        Integer totalCount = usersDto.getTotalCount();
        Integer usersAmount = usersData.size();
        if (!Objects.equals(usersAmount, successCount) || !Objects.equals(usersAmount, totalCount)) {
            throw new EI2Exception(WRONG_UPLOADED_AMOUNT.getCode(), successCount, totalCount, usersAmount);
        }

        log.info("There are {} users successfully uploaded for the {} audience of Twitter account {}",
                usersAmount, audienceExternalId, twitterAccountId);
        return successCount;
    }

    private List<TwitterAudienceUsersOperation> getUserOperations(List<String> usersData, AudienceDataType dataType,
                                                                  Long expireMinutes) {

        List<TwitterAudienceUsersOperation.User> users = usersData.stream()
                .map(userData -> mapUserDataToTwitterUsers(userData, dataType)).collect(Collectors.toList());

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime usersExpirationDate = expireMinutes == null ? null: now.plusMinutes(expireMinutes);

        TwitterAudienceUsersOperation.Params params =
                TwitterAudienceUsersOperation.Params.of(now, usersExpirationDate, users);
        TwitterAudienceUsersOperation usersOperation =
                TwitterAudienceUsersOperation.of(TwitterAudienceUsersOperation.OperationType.Update, params);
        return List.of(usersOperation);
    }

    private TwitterAudienceUsersOperation.User mapUserDataToTwitterUsers(String userData, AudienceDataType dataType) {
        return switch (dataType) {
            case EMAIL -> TwitterAudienceUsersOperation.User.ofEmail(userData);
            case MADID -> TwitterAudienceUsersOperation.User.ofMadid(userData);
        };
    }

}
