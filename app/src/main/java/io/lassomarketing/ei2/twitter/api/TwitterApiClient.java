package io.lassomarketing.ei2.twitter.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lassomarketing.ei2.common.exception.EI2Exception;
import io.lassomarketing.ei2.twitter.api.model.TwitterApiResponse;
import io.lassomarketing.ei2.twitter.api.oauth.AuthorizationService;
import io.lassomarketing.ei2.twitter.api.oauth.HttpParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.EXCHANGE_ERROR_CODE;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.EXCHANGE_ERROR_RESPONSE;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.EXCHANGE_NO_BODY;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.EXCHANGE_NO_DATA;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.EXCHANGE_OPERATION_ERROR;
import static io.lassomarketing.ei2.twitter.exception.Ei2TwitterErrorCode.PARSING_ERROR;

@Slf4j
@Service
public abstract class TwitterApiClient {

    protected final ObjectMapper objectMapper;
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
            throw new EI2Exception(EXCHANGE_ERROR_CODE.getCode(), response.getStatusCode().toString());
        }
        TwitterApiResponse body = response.getBody();
        if (body == null) {
            throw new EI2Exception(EXCHANGE_NO_BODY.getCode());
        }

        List<TwitterApiResponse.Error> errors = body.getErrors();
        if (errors != null && !errors.isEmpty()) {
            throw new EI2Exception(EXCHANGE_ERROR_RESPONSE.getCode(), errors);
        }

        List<JsonNode> operationErrors = body.getOperationErrors();
        if (operationErrors != null && !operationErrors.isEmpty()) {
            throw new EI2Exception(EXCHANGE_OPERATION_ERROR.getCode(), operationErrors);
        }

        JsonNode data = body.getData();
        if (data == null || data.isNull()) {
            throw new EI2Exception(EXCHANGE_NO_DATA.getCode());
        }
        try {
            return objectMapper.treeToValue(data, type);
        } catch (JsonProcessingException e) {
            log.error("Error parsing body: " + data);
            log.error(e.getMessage(), e);
            throw new EI2Exception(PARSING_ERROR.getCode(), e.getMessage());
        }
    }
}
