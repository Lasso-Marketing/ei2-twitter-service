package io.lassomarketing.ei2.twitter.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

/**
 * Executes a request with output streaming, runs the given chain of interceptors on the request,
 * and then delegates execution using a request factory.
 * Interceptors will receive null request bodies.
 */
public class RequestStreamingInterceptingClientHttpRequest extends AbstractClientHttpRequest {

    private final ClientHttpRequestFactory requestFactory;
    private final List<ClientHttpRequestInterceptor> interceptors;
    private final HttpURLConnection connection;

    private AbstractClientHttpRequest targetRequest;
    private OutputStream body;


    protected RequestStreamingInterceptingClientHttpRequest(
            HttpURLConnection connection,
            ClientHttpRequestFactory requestFactory,
            List<ClientHttpRequestInterceptor> interceptors
    ) {
        this.connection = connection;
        this.requestFactory = requestFactory;
        this.interceptors = interceptors;
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

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        if (targetRequest == null) {
            targetRequest = (AbstractClientHttpRequest) requestFactory.createRequest(
                    getURI(), getMethod());
            getHeaders().forEach((key, value) -> targetRequest.getHeaders().addAll(key, value));
            body = targetRequest.getBody();
        }

        return StreamUtils.nonClosing(body);
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
        InterceptingRequestExecution requestExecution = new InterceptingRequestExecution();
        return requestExecution.execute(this, null);
    }



    class InterceptingRequestExecution implements ClientHttpRequestExecution {

        private final Iterator<ClientHttpRequestInterceptor> iterator;

        public InterceptingRequestExecution() {
            this.iterator = interceptors.iterator();
        }

        @Override
        public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
            if (this.iterator.hasNext()) {
                ClientHttpRequestInterceptor nextInterceptor = this.iterator.next();
                return nextInterceptor.intercept(request, body, this);
            }
            else {
                if (targetRequest == null) {
                    targetRequest = (AbstractClientHttpRequest) requestFactory.createRequest(
                            getURI(), getMethod());
                    getHeaders().forEach((key, value) -> targetRequest.getHeaders().addAll(key, value));
                }
                return targetRequest.execute();
            }
        }
    }
}
