package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.snapchat.service.AudienceUploadFieldNames;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersDto;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersOperation;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
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

import static io.lassomarketing.ei2.snapchat.error.TwitterErrorCode.UNKNOWN_SCHEMA;

@Slf4j
@Service
public class TwitterAudienceUsersApiClient extends TwitterApiClient {

    private static final String URI = "/accounts/%s/custom_audiences/%s/users";

    private final TwitterAudienceUsersRestOperations twitterAudienceUsersRestOperations;

    protected TwitterAudienceUsersApiClient(
            ObjectMapper objectMapper,
            TwitterAudienceUsersRestOperations twitterAudienceUsersRestOperations,
            AuthorizationService authorizationService
    ) {
        super(objectMapper, authorizationService);
        this.twitterAudienceUsersRestOperations = twitterAudienceUsersRestOperations;
    }

    /**
     * <a href="https://developer.twitter.com/en/docs/twitter-ads-api/audiences/api-reference/custom-audience-users">
     *      Twitter upload users documentation </a>
     */
    public int uploadUsers(String twitterAccountId, String audienceExternalId, Long expireMinutes, String dataType,
                           List<String> usersData) {
        String uri = String.format(URI, twitterAccountId, audienceExternalId);
        HttpHeaders headers = createHeaders(HttpMethod.POST, uri);
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<TwitterAudienceUsersOperation> usersOperations = getUserOperations(usersData, dataType, expireMinutes);

        //TODO check size

        HttpEntity<?> httpEntity = new HttpEntity<>(usersOperations, headers);
        ResponseEntity<TwitterApiResponse> responseEntity =
                twitterAudienceUsersRestOperations.exchange(uri, HttpMethod.POST, httpEntity, TwitterApiResponse.class);

        TwitterAudienceUsersDto usersDto = parseResponseData(responseEntity, TwitterAudienceUsersDto.class);
        Integer successCount = usersDto.getSuccessCount();
        Integer totalCount = usersDto.getTotalCount();
        Integer usersAmount = usersData.size();
        if (!Objects.equals(usersAmount, successCount) || !Objects.equals(usersAmount, totalCount)) {
            throw new TwitterApiException(String.format(
                    "Success (%d) or total (%d) uploaded users not equals to batch size: %d",
                    successCount, totalCount, usersAmount
            ));
        }

        log.info("There are {} users successfully uploaded for the {} audience of Twitter account {}",
                usersAmount, audienceExternalId, twitterAccountId);
        return successCount;
    }

    private List<TwitterAudienceUsersOperation> getUserOperations(List<String> usersData, String dataType,
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

    private TwitterAudienceUsersOperation.User mapUserDataToTwitterUsers(String userData, String dataType) {
        return switch (dataType) {
            case AudienceUploadFieldNames.EMAIL -> TwitterAudienceUsersOperation.User.ofEmail(userData);
            case AudienceUploadFieldNames.MADID -> TwitterAudienceUsersOperation.User.ofMadid(userData);
            default -> throw new EI2Exception(UNKNOWN_SCHEMA.getCode(), dataType);
        };
    }

}
