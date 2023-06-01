package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
import io.lassomarketing.ei2.twitter.api.oauth.HttpParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class TwitterApiClient {

    private final ObjectMapper objectMapper;
    private final AuthorizationService authorizationService;

    protected TwitterApiClient(ObjectMapper objectMapper, AuthorizationService authorizationService) {
        this.objectMapper = objectMapper;
        this.authorizationService = authorizationService;
    }

    protected HttpHeaders createHeaders(HttpMethod method, String uri, HttpParameter... httpParameters) {
        HttpHeaders headers = new HttpHeaders();
        String authorizationHeader = authorizationService.buildAuthorizationHeader(method, uri, List.of(httpParameters));
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected <T> T parseResponseData(ResponseEntity<TwitterApiResponse> response, Class<T> type) {
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new TwitterApiException(response.getStatusCode().toString());
        }
        TwitterApiResponse body = response.getBody();
        if (body == null) {
            throw new TwitterApiException("Empty body");
        }

        List<TwitterApiResponse.Error> errors = body.getErrors();
        if (errors != null && !errors.isEmpty()) {
            throw new TwitterApiException("Error response: " + errors);
        }

        List<JsonNode> operationErrors = body.getOperationErrors();
        if (operationErrors != null && !operationErrors.isEmpty()) {
            throw new TwitterApiException("Error response: " + operationErrors);
        }

        JsonNode data = body.getData();
        if (data == null || data.isNull()) {
            throw new TwitterApiException("Empty data");
        }
        try {
            return objectMapper.treeToValue(data, type);
        } catch (JsonProcessingException e) {
            throw new TwitterApiException(e);
        }
    }
}
