/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lassomarketing.ei2.twitter.api.oauth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OAuthAuthorization {

    private static final Random RAND = new Random();
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final HttpParameter OAUTH_SIGNATURE_METHOD = new HttpParameter("oauth_signature_method", "HMAC-SHA1");

    private final String consumerKey;
    private final String consumerSecret;
    private final String token;
    private final String tokenSecret;

    OAuthAuthorization(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.token = token;
        this.tokenSecret = tokenSecret;
    }

    private String generateAuthorizationHeader(String method, String url, List<HttpParameter> httpParameters, String nonce, String timestamp) {
        List<HttpParameter> oauthHeaderParams = new ArrayList<>(5);
        oauthHeaderParams.add(new HttpParameter("oauth_consumer_key", consumerKey));
        oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD);
        oauthHeaderParams.add(new HttpParameter("oauth_timestamp", timestamp));
        oauthHeaderParams.add(new HttpParameter("oauth_nonce", nonce));
        oauthHeaderParams.add(new HttpParameter("oauth_version", "1.0"));
        oauthHeaderParams.add(new HttpParameter("oauth_token", token));

        List<HttpParameter> signatureBaseParams = new ArrayList<>(oauthHeaderParams.size() + httpParameters.size());
        signatureBaseParams.addAll(oauthHeaderParams);
        if (!HttpParameter.containsFile(httpParameters)) {
            signatureBaseParams.addAll(httpParameters);
        }
        parseGetParameters(url, signatureBaseParams);
        StringBuilder base = new StringBuilder(method).append("&")
                .append(HttpParameter.encode(constructRequestURL(url))).append("&");
        base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)));
        String oauthBaseString = base.toString();
        String signature = generateSignature(oauthBaseString);

        oauthHeaderParams.add(new HttpParameter("oauth_signature", signature));

        return "OAuth " + encodeParameters(oauthHeaderParams, ",", true);
    }

    private void parseGetParameters(String url, List<HttpParameter> signatureBaseParams) {
        int queryStart = url.indexOf("?");
        if (-1 != queryStart) {
            String[] queryStrs = split(url.substring(queryStart + 1), "&");
            for (String query : queryStrs) {
                String[] split = split(query, "=");
                if (split.length == 2) {
                    signatureBaseParams.add(
                            new HttpParameter(URLDecoder.decode(split[0],
                                    StandardCharsets.UTF_8), URLDecoder.decode(split[1],
                                    StandardCharsets.UTF_8)));
                } else {
                    signatureBaseParams.add(new HttpParameter(URLDecoder.decode(split[0], StandardCharsets.UTF_8), ""));
                }
            }
        }
    }

    /**
     * @return generated authorization header
     * @see <a href="http://oauth.net/core/1.0a/#rfc.section.5.4.1">OAuth Core - 5.4.1.  Authorization Header</a>
     */
    String generateAuthorizationHeader(String method, String url, List<HttpParameter> httpParameters) {
        long timestamp = System.currentTimeMillis() / 1000;
        long nonce = timestamp + RAND.nextInt();
        return generateAuthorizationHeader(method, url, httpParameters, String.valueOf(nonce), String.valueOf(timestamp));
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data the data to be signed
     * @return signature
     * @see <a href="http://oauth.net/core/1.0a/#rfc.section.9.2.1">OAuth Core - 9.2.1.  Generating Signature</a>
     */
    private String generateSignature(String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            String oauthSignature = HttpParameter.encode(consumerSecret) + "&" + HttpParameter.encode(tokenSecret);
            SecretKeySpec spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            mac.init(spec);
            byte[] byteHMAC = mac.doFinal(data.getBytes());
            return Base64Encoder.encode(byteHMAC);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The request parameters are collected, sorted and concatenated into a normalized string:<br>
     * \u0095	Parameters in the OAuth HTTP Authorization header excluding the realm parameter.<br>
     * \u0095	Parameters in the HTTP POST request body (with a content-type of application/x-www-form-urlencoded).<br>
     * \u0095	HTTP GET parameters added to the URLs in the query part (as defined by [RFC3986] section 3).<br>
     * <br>
     * The oauth_signature parameter MUST be excluded.<br>
     * The parameters are normalized into a single string as follows:<br>
     * 1.	Parameters are sorted by name, using lexicographical byte value ordering. If two or more parameters share the same name, they are sorted by their value. For example:<br>
     * 2.	                    a=1, c=hi%20there, f=25, f=50, f=a, z=p, z=t<br>
     * 3.	<br>
     * 4.	Parameters are concatenated in their sorted order into a single string. For each parameter, the name is separated from the corresponding value by an \u0091=\u0092 character (ASCII code 61), even if the value is empty. Each name-value pair is separated by an \u0091&\u0092 character (ASCII code 38). For example:<br>
     * 5.	                    a=1&c=hi%20there&f=25&f=50&f=a&z=p&z=t<br>
     * 6.	<br>
     *
     * @param params parameters to be normalized and concatenated
     * @return normalized and concatenated parameters
     * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.1">OAuth Core - 9.1.1.  Normalize Request Parameters</a>
     */
    private static String normalizeRequestParameters(List<HttpParameter> params) {
        Collections.sort(params);
        return encodeParameters(params);
    }

    /**
     * @param httpParams parameters to be encoded and concatenated
     * @return encoded string
     * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
     * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
     */
    public static String encodeParameters(List<HttpParameter> httpParams) {
        return encodeParameters(httpParams, "&", false);
    }

    public static String encodeParameters(HttpParameter... httpParams) {
        return encodeParameters(List.of(httpParams));
    }

    public static String encodeParameters(List<HttpParameter> httpParams, String splitter, boolean quot) {
        StringBuilder buf = new StringBuilder();
        for (HttpParameter param : httpParams) {
            if (!param.isFile()) {
                if (buf.length() != 0) {
                    if (quot) {
                        buf.append("\"");
                    }
                    buf.append(splitter);
                }
                buf.append(HttpParameter.encode(param.getName())).append("=");
                if (quot) {
                    buf.append("\"");
                }
                buf.append(HttpParameter.encode(param.getValue()));
            }
        }
        if (buf.length() != 0) {
            if (quot) {
                buf.append("\"");
            }
        }
        return buf.toString();
    }

    /**
     * The Signature Base String includes the request absolute URL, tying the signature to a specific endpoint. The URL used in the Signature Base String MUST include the scheme, authority, and path, and MUST exclude the query and fragment as defined by [RFC3986] section 3.<br>
     * If the absolute request URL is not available to the Service Provider (it is always available to the Consumer), it can be constructed by combining the scheme being used, the HTTP Host header, and the relative HTTP request URL. If the Host header is not available, the Service Provider SHOULD use the host name communicated to the Consumer in the documentation or other means.<br>
     * The Service Provider SHOULD document the form of URL used in the Signature Base String to avoid ambiguity due to URL normalization. Unless specified, URL scheme and authority MUST be lowercase and include the port number; http default port 80 and https default port 443 MUST be excluded.<br>
     * <br>
     * For example, the request:<br>
     * HTTP://Example.com:80/resource?id=123<br>
     * Is included in the Signature Base String as:<br>
     * http://example.com/resource
     *
     * @param url the url to be normalized
     * @return the Signature Base String
     * @see <a href="http://oauth.net/core/1.0#rfc.section.9.1.2">OAuth Core - 9.1.2.  Construct Request URL</a>
     */
    private static String constructRequestURL(String url) {
        int index = url.indexOf("?");
        if (-1 != index) {
            url = url.substring(0, index);
        }
        int slashIndex = url.indexOf("/", 8);
        String baseURL = url.substring(0, slashIndex).toLowerCase();
        int colonIndex = baseURL.indexOf(":", 8);
        if (-1 != colonIndex) {
            // url contains port number
            if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
                // http default port 80 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            } else if (baseURL.startsWith("https://") && baseURL.endsWith(":443")) {
                // http default port 443 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            }
        }
        url = baseURL + url.substring(slashIndex);

        return url;
    }

    private static String[] split(String str, String separator) {
        int index = str.indexOf(separator);
        if (index == -1) {
            return new String[]{str};
        }

        List<String> strList = new ArrayList<>();
        int oldIndex = 0;
        while (index != -1) {
            String subStr = str.substring(oldIndex, index);
            strList.add(subStr);
            oldIndex = index + separator.length();
            index = str.indexOf(separator, oldIndex);
        }
        if (oldIndex != str.length()) {
            strList.add(str.substring(oldIndex));
        }
        return strList.toArray(new String[0]);
    }
}
