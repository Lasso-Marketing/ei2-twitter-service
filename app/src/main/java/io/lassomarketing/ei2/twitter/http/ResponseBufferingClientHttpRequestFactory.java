package io.lassomarketing.ei2.twitter.http;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Factory of {@link ResponseBufferingClientHttpRequest}.
 * Most of this is copied from {@link org.springframework.http.client.SimpleClientHttpRequestFactory}.
 * This implementation provides streaming request output and no request body buffering.
 */
public class ResponseBufferingClientHttpRequestFactory implements ClientHttpRequestFactory,
                                                                  HttpConnectionConfigurable {

    private static final int DEFAULT_CHUNK_SIZE = 4096;

    private int chunkSize = DEFAULT_CHUNK_SIZE;
    private int connectTimeout = -1;
    private int readTimeout = -1;

    @Override
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Set the number of bytes to write in each chunk.
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Set the underlying URLConnection's connect timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     * <p>Default is the system's default timeout.
     * @see URLConnection#setConnectTimeout(int)
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Set the underlying URLConnection's read timeout (in milliseconds).
     * A timeout value of 0 specifies an infinite timeout.
     * <p>Default is the system's default timeout.
     * @see URLConnection#setReadTimeout(int)
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        HttpURLConnection connection = openConnection(uri.toURL());
        prepareConnection(this, connection, httpMethod.name());
        return new ResponseBufferingClientHttpRequest(connection, getChunkSize());
    }


    /**
     * Opens and returns a connection to the given URL.
     * @param url the URL to open a connection to
     * @return the opened connection
     * @throws IOException in case of I/O errors
     */
    static HttpURLConnection openConnection(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        if (!(urlConnection instanceof HttpURLConnection)) {
            throw new IllegalStateException("HttpURLConnection required for [" + url + "] but got: " + urlConnection);
        }
        return (HttpURLConnection) urlConnection;
    }

    /**
     * Template method for preparing the given {@link HttpURLConnection}.
     * <p>The default implementation prepares the connection for input and output, and sets the HTTP method.
     * @param connection the connection to prepare
     * @param httpMethod the HTTP request method ({@code GET}, {@code POST}, etc.)
     * @throws IOException in case of I/O errors
     */
    static void prepareConnection(HttpConnectionConfigurable httpConnectionConfigurable,
                                  HttpURLConnection connection,
                                  String httpMethod
    ) throws IOException {
        if (httpConnectionConfigurable.getConnectTimeout() >= 0) {
            connection.setConnectTimeout(httpConnectionConfigurable.getConnectTimeout());
        }
        if (httpConnectionConfigurable.getReadTimeout() >= 0) {
            connection.setReadTimeout(httpConnectionConfigurable.getReadTimeout());
        }

        connection.setDoInput(true);

        if ("GET".equals(httpMethod)) {
            connection.setInstanceFollowRedirects(true);
        }
        else {
            connection.setInstanceFollowRedirects(false);
        }

        if ("POST".equals(httpMethod) || "PUT".equals(httpMethod) ||
                "PATCH".equals(httpMethod) || "DELETE".equals(httpMethod)) {
            connection.setDoOutput(true);
        }
        else {
            connection.setDoOutput(false);
        }

        connection.setRequestMethod(httpMethod);
    }


}
