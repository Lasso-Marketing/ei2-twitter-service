package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceUsersDto;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

    public void uploadUsers(String twitterAccountId, String audienceExternalId, String usersData, int batchSize) {
        String uri = String.format(URI, twitterAccountId, audienceExternalId);
        HttpHeaders headers = createHeaders(HttpMethod.POST, uri);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> httpEntity = new HttpEntity<>(usersData, headers);
        ResponseEntity<TwitterApiResponse> responseEntity =
                twitterAudienceUsersRestOperations.exchange(uri, HttpMethod.POST, httpEntity, TwitterApiResponse.class);

        TwitterAudienceUsersDto usersDto = parseResponseData(responseEntity, TwitterAudienceUsersDto.class);
        Integer successCount = usersDto.getSuccessCount();
        Integer totalCount = usersDto.getTotalCount();
        if (!Objects.equals(batchSize, successCount) || !Objects.equals(batchSize, totalCount)) {
            throw new TwitterApiException(String.format(
                    "Success (%d) or total (%d) uploaded users not equals to batch size: %d",
                    successCount, totalCount, batchSize
            ));
        }

        log.debug("There are {} users successfully uploaded for the {} audience of Twitter account {}",
                batchSize, audienceExternalId, twitterAccountId);
    }
}
