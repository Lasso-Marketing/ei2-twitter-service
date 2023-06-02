package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.model.TwitterAudienceDto;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
import io.lassomarketing.ei2.twitter.api.oauth.HttpParameter;
import io.lassomarketing.ei2.twitter.api.oauth.OAuthAuthorization;
import io.lassomarketing.ei2.twitter.dto.AudienceDto;
import io.lassomarketing.ei2.twitter.utils.AudienceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwitterAudienceApiClient extends TwitterApiClient {

    private static final String CREATE_URI = "/accounts/%s/custom_audiences";
    private static final String UPDATE_URI = "/accounts/%s/custom_audiences/%s";

    private final TwitterAudienceRestOperations twitterAudienceRestOperations;

    public TwitterAudienceApiClient(
            ObjectMapper objectMapper,
            TwitterAudienceRestOperations twitterAudienceRestOperations,
            AuthorizationService authorizationService
    ) {
        super(objectMapper, authorizationService);
        this.twitterAudienceRestOperations = twitterAudienceRestOperations;
    }

    public String create(AudienceDto dto) {
        String uri = String.format(CREATE_URI, dto.getSocialAccountId());
        HttpParameter nameParameter = buildAudienceNameParameter(dto);

        HttpHeaders headers = createHeaders(HttpMethod.POST, uri, nameParameter);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = OAuthAuthorization.encodeParameters(nameParameter);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<TwitterApiResponse> responseEntity =
                twitterAudienceRestOperations.exchange(uri, HttpMethod.POST, httpEntity, TwitterApiResponse.class);

        TwitterAudienceDto twitterAudienceDto = parseResponseData(responseEntity, TwitterAudienceDto.class);
        return twitterAudienceDto.getId();
    }

    public String update(String externalId, AudienceDto dto) {
        String uri = String.format(UPDATE_URI, dto.getSocialAccountId(), externalId);
        HttpParameter nameParameter = buildAudienceNameParameter(dto);

        HttpHeaders headers = createHeaders(HttpMethod.PUT, uri, nameParameter);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = OAuthAuthorization.encodeParameters(nameParameter);
        HttpEntity<?> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<TwitterApiResponse> responseEntity =
                twitterAudienceRestOperations.exchange(uri, HttpMethod.PUT, httpEntity, TwitterApiResponse.class);

        TwitterAudienceDto twitterAudienceDto = parseResponseData(responseEntity, TwitterAudienceDto.class);
        return twitterAudienceDto.getId();
    }

    private static HttpParameter buildAudienceNameParameter(AudienceDto dto) {
        String audienceName = AudienceUtils.buildAudienceName(dto.getName(), dto.getCustomName(), dto.getAudienceId(),
                                                              Integer.MAX_VALUE);
        return new HttpParameter("name", audienceName);
    }
}
