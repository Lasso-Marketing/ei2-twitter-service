package io.lassomarketing.ei2.twitter.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

@Data
public class TwitterApiResponse {

    private JsonNode data;

    private List<Error> errors;

    @JsonProperty("operation_errors")
    private List<JsonNode> operationErrors;

    @Data
    public static class Error {
        private String code;
        private String message;
    }
}
