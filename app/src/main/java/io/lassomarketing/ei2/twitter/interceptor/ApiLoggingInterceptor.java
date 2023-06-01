package io.lassomarketing.ei2.twitter.interceptor;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class ApiLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final String STUB = "********";

    private final String prefix;
    private final boolean logRequestBody;
    private final boolean logResponseBody;
    private final boolean debug;
    private final List<String> sensitiveHeaders;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        HttpHeaders headers = new HttpHeaders(new LinkedMultiValueMap<>(request.getHeaders()
                .entrySet()
                .stream()
                .map(entry -> {
                    if (sensitiveHeaders != null) {
                        for (String sensitiveHeader : sensitiveHeaders) {
                            if (sensitiveHeader.equalsIgnoreCase(entry.getKey())) {
                                return Map.entry(entry.getKey(), List.of(STUB));
                            }
                        }
                    }
                    return entry;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
        String requestBody = null;
        if (body != null) {
            requestBody = logRequestBody ? new String(body, StandardCharsets.UTF_8) : STUB;
        }
        if (debug) {
            log.debug("[{}] Request: {} {}\nHeaders:\n{}\nBody:\n{}",
                    prefix, request.getMethod(), request.getURI(), headers, requestBody);
        } else {
            log.info("[{}] Request: {} {}\nHeaders:\n{}\nBody:\n{}",
                    prefix, request.getMethod(), request.getURI(), headers, requestBody);
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        String responseBody = logResponseBody ? IOUtils.toString(response.getBody(), StandardCharsets.UTF_8) : STUB;
        if (debug) {
            log.debug("[{}] Response: {}\nHeaders:\n{}\nBody:\n{}",
                    prefix, response.getStatusCode(), response.getHeaders(), responseBody);
        } else {
            log.info("[{}] Response: {}\nHeaders:\n{}\nBody:\n{}",
                    prefix, response.getStatusCode(), response.getHeaders(), responseBody);
        }
    }
}
