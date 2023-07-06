package io.lassomarketing.ei2.twitter.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Streams requests and provides buffered responses ({@link BufferingClientHttpResponse})
 */
class ResponseBufferingClientHttpRequest extends AbstractClientHttpRequest {

    private final HttpURLConnection connection;
    private final int chunkSize;

    private OutputStream body;

    ResponseBufferingClientHttpRequest(HttpURLConnection connection, int chunkSize) {
        this.connection = connection;
        this.chunkSize = chunkSize;
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        if (this.body == null) {
            long contentLength = headers.getContentLength();
            if (contentLength >= 0) {
                this.connection.setFixedLengthStreamingMode(contentLength);
            }
            else {
                this.connection.setChunkedStreamingMode(this.chunkSize);
            }

            addHeaders(this.connection, headers);
            this.connection.connect();
            this.body = this.connection.getOutputStream();
        }

        return StreamUtils.nonClosing(this.body);
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        try {
            if (this.body != null) {
                this.body.close();
            }
            else {
                addHeaders(this.connection, headers);
                this.connection.connect();
                // Immediately trigger the request in a no-output scenario as well
                this.connection.getResponseCode();
            }
        }
        catch (IOException ex) {
            // ignore
        }

        return new BufferingClientHttpResponse(connection);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(this.connection.getRequestMethod());
    }

    @Override
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }


    /**
     * Add the given headers to the given HTTP connection.
     * @param connection the connection to add the headers to
     * @param headers the headers to add
     */
    static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
        String method = connection.getRequestMethod();
        if (method.equals("PUT") || method.equals("DELETE")) {
            if (!StringUtils.hasText(headers.getFirst(HttpHeaders.ACCEPT))) {
                // Avoid "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2"
                // from HttpUrlConnection which prevents JSON error response details.
                headers.set(HttpHeaders.ACCEPT, "*/*");
            }
        }
        headers.forEach((headerName, headerValues) -> {
            if (HttpHeaders.COOKIE.equalsIgnoreCase(headerName)) {  // RFC 6265
                String headerValue = StringUtils.collectionToDelimitedString(headerValues, "; ");
                connection.setRequestProperty(headerName, headerValue);
            }
            else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    connection.addRequestProperty(headerName, actualHeaderValue);
                }
            }
        });
    }
}
