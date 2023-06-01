package io.lassomarketing.ei2.twitter.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Reads the response's body into memory, thus allowing for multiple invocations of {@link #getBody()}.
 */
class BufferingClientHttpResponse extends AbstractClientHttpResponse {

    private final HttpURLConnection connection;

    private HttpHeaders headers;
    private byte[] body;

    BufferingClientHttpResponse(HttpURLConnection connection) {
        this.connection = connection;
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return this.connection.getResponseCode();
    }

    @Override
    public String getStatusText() throws IOException {
        String result = this.connection.getResponseMessage();
        return (result != null) ? result : "";
    }

    @Override
    public void close() {
        try {
            if (this.body == null) {
                getBody();
            }
        }
        catch (Exception ex) {
            // ignore
        }
    }

    @Override
    public InputStream getBody() throws IOException {
        if (body == null) {
            InputStream errorStream = this.connection.getErrorStream();
            InputStream responseStream = (errorStream != null ? errorStream : this.connection.getInputStream());
            body = StreamUtils.copyToByteArray(responseStream);
            responseStream.close();
        }

        return new ByteArrayInputStream(body);
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            this.headers = new HttpHeaders();
            // Header field 0 is the status line for most HttpURLConnections, but not on GAE
            String name = this.connection.getHeaderFieldKey(0);
            if (StringUtils.hasLength(name)) {
                this.headers.add(name, this.connection.getHeaderField(0));
            }
            int i = 1;
            while (true) {
                name = this.connection.getHeaderFieldKey(i);
                if (!StringUtils.hasLength(name)) {
                    break;
                }
                this.headers.add(name, this.connection.getHeaderField(i));
                i++;
            }
        }
        return this.headers;
    }
}
