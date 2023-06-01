package io.lassomarketing.ei2.twitter.http;

public interface HttpConnectionConfigurable {

    int getChunkSize();
    int getConnectTimeout();
    int getReadTimeout();
}
