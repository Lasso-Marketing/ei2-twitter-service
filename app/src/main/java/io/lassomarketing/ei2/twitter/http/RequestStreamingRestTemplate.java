package io.lassomarketing.ei2.twitter.http;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * This subclass allows for more performant usage of {@link ClientHttpRequestInterceptor}s,
 * such that request bodies will not necessarily be buffered into byte[],
 * which retains consistency with {@link ClientHttpRequestFactory} implementations
 * that try to stream request content.
 * For any instance of this class where interceptors are used, the request factory must implement
 * {@link HttpConnectionConfigurable}.
 *
 * @param <T>
 */
public class RequestStreamingRestTemplate<T extends ClientHttpRequestFactory & HttpConnectionConfigurable>
        extends RestTemplate {

    private T requestFactory;

    @SuppressWarnings("unchecked")
    public RequestStreamingRestTemplate() {
        super();

        List<ClientHttpRequestInterceptor> interceptors = List.copyOf(getInterceptors());
        setInterceptors(Collections.emptyList());
        requestFactory = (T) super.getRequestFactory();
        setInterceptors(interceptors);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        this.requestFactory = (T) requestFactory;
    }

    @Override
    public ClientHttpRequestFactory getRequestFactory() {
        List<ClientHttpRequestInterceptor> interceptors = getInterceptors();
        if (!CollectionUtils.isEmpty(interceptors)) {
            return new RequestStreamingInterceptingClientHttpRequestFactory(
                    requestFactory,
                    interceptors,
                    requestFactory.getConnectTimeout(),
                    requestFactory.getReadTimeout(),
                    requestFactory.getChunkSize());
        }
        else {
            return requestFactory;
        }
    }



    static class RequestStreamingInterceptingClientHttpRequestFactory
            extends AbstractClientHttpRequestFactoryWrapper implements HttpConnectionConfigurable {

        private final List<ClientHttpRequestInterceptor> interceptors;
        private final int connectTimeout;
        private final int readTimeout;
        private final int chunkSize;

        /**
         * Create a new instance of the {@code InterceptingClientHttpRequestFactory} with the given parameters.
         * @param requestFactory the request factory to wrap
         * @param interceptors the interceptors that are to be applied (can be {@code null})
         */
        public RequestStreamingInterceptingClientHttpRequestFactory(
                ClientHttpRequestFactory requestFactory,
                @Nullable List<ClientHttpRequestInterceptor> interceptors,
                int connectTimeout,
                int readTimeout,
                int chunkSize
        ) {
            super(requestFactory);
            this.interceptors = (interceptors != null ? interceptors : Collections.emptyList());
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.chunkSize = chunkSize;
        }

        @Override
        public int getChunkSize() {
            return chunkSize;
        }

        @Override
        public int getConnectTimeout() {
            return connectTimeout;
        }

        @Override
        public int getReadTimeout() {
            return readTimeout;
        }

        @Override
        protected ClientHttpRequest createRequest(URI uri,
                                                  HttpMethod httpMethod,
                                                  ClientHttpRequestFactory requestFactory
        ) throws IOException {
            HttpURLConnection connection = ResponseBufferingClientHttpRequestFactory.openConnection(uri.toURL());
            ResponseBufferingClientHttpRequestFactory.prepareConnection(
                    this, connection, httpMethod.name());
            return new RequestStreamingInterceptingClientHttpRequest(
                    connection, requestFactory, interceptors);
        }
    }
}
