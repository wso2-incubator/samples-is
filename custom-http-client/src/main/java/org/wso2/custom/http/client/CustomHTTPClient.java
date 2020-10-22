package org.wso2.custom.http.client;

import org.wso2.custom.http.client.config.CustomHTTPClientBaseConfiguration;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.custom.http.client.exception.CustomHTTPClientException;
import org.wso2.custom.http.client.internal.CustomHTTPClientDataHolder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class CustomHTTPClient {

    private static final RequestConfig config;
    private static final String TYPE_APPLICATION_JSON = "application/json";
    private static CloseableHttpAsyncClient asyncClient;
    private static final Log log = LogFactory.getLog(org.wso2.custom.http.client.CustomHTTPClient.class);

    static  {

        config = RequestConfig.custom()
                .setConnectTimeout(CustomHTTPClientBaseConfiguration.getConnectionTimeout())
                .setConnectionRequestTimeout(CustomHTTPClientBaseConfiguration.getConnectionRequestTimeout())
                .setSocketTimeout(CustomHTTPClientBaseConfiguration.getSocketTimeout())
                .build();
    }

    public static void startAsyncClient() {

        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom().setDefaultRequestConfig(config);
        addSslContext(httpClientBuilder, MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);

        PoolingNHttpClientConnectionManager poolingHttpClientConnectionManager;
        try {
            poolingHttpClientConnectionManager = new
                    PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor());
            poolingHttpClientConnectionManager.setMaxTotal(CustomHTTPClientBaseConfiguration.getMaxConnections());
            poolingHttpClientConnectionManager.setDefaultMaxPerRoute(
                    CustomHTTPClientBaseConfiguration.getMaxConnectionsPerRoute());

            httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);

            asyncClient = httpClientBuilder.build();
            asyncClient.start();
        } catch (IOReactorException e) {
            log.error("Error while creating the Asyc client.", e);
        }
    }

    public static void closeAsyncClient()  {

        if (asyncClient != null) {
            try {
                asyncClient.close();
            } catch (IOException e) {
                log.error("Error while closing the Asyc client.", e);
            }
        }
    }

    public static void invokeAsyncAPI(String epUrl, String payload) throws CustomHTTPClientException {

        HttpPost request = new HttpPost(epUrl);
        try {
            request.setHeader(ACCEPT, TYPE_APPLICATION_JSON);
            request.setHeader(CONTENT_TYPE, TYPE_APPLICATION_JSON);

            String authHeader = getAuthenticationHeader(true);
            request.setHeader("Authorization", "Basic " + authHeader);
            request.setHeader("X-Company-Code",  CustomHTTPClientBaseConfiguration.getPropertyCode());

            if (log.isDebugEnabled()) {
                log.debug("Invocation of Async API to " + epUrl + ", with the payload : " + payload);
            }
            request.setEntity(new StringEntity(payload));

            asyncClient.execute(request, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response) {

                    int responseCode = response.getStatusLine().getStatusCode();

                    if (responseCode == 200) {
                        if (log.isDebugEnabled()) {
                            log.debug("Invocation of Async API to " + epUrl + " successful.");
                        }
                    } else {
                        log.warn("Invocation of Async API to " + epUrl + " is failed with response code " + responseCode);
                    }
                }

                @Override
                public void failed(final Exception ex) {

                    log.error("Invocation of Async API to " + epUrl + " is failed.", ex);
                }

                @Override
                public void cancelled() {

                    log.error("Invocation of Async API to " + epUrl + " is cancelled.");
                }

            });
        } catch (IOException e) {
            throw new CustomHTTPClientException("Error while making Async call to endpoint " + epUrl, e);
        } catch (ParseException e) {
            throw new CustomHTTPClientException("Error while parsing request to endpoint " + epUrl, e);
        }
    }

    public static String invokeGet(String url) throws CustomHTTPClientException {

        HttpGet request = new HttpGet(url);
        return invoke(request);
    }

    public static String invokeAPI(String url, String payload) throws CustomHTTPClientException {

        HttpPost request = new HttpPost(url);
        try {
            request.setEntity(new StringEntity(payload));
        } catch (UnsupportedEncodingException e) {
            throw new CustomHTTPClientException("Error while parsing request to " + url, e);
        }

        return invoke(request);
    }

    public static String invokePut(String url, String payload) throws CustomHTTPClientException {

        HttpPut request = new HttpPut(url);
        if (StringUtils.isNotBlank(payload)) {
            try {
                request.setEntity(new StringEntity(payload));
            } catch (UnsupportedEncodingException e) {
                throw new CustomHTTPClientException("Error while parsing request to " + url, e);
            }
        }
        return invoke(request);
    }

    public static String invokePost(String url, String payload) throws CustomHTTPClientException {

        HttpPost request = new HttpPost(url);
        try {
            request.setEntity(new StringEntity(payload));
        } catch (UnsupportedEncodingException e) {
            throw new CustomHTTPClientException("Error while parsing request to " + url, e);
        }

        return invoke(request);
    }

    private static String invoke(HttpRequestBase request) throws CustomHTTPClientException {

        String url = request.getURI().toString();
        try {
            request.setHeader(ACCEPT, TYPE_APPLICATION_JSON);

            if (request instanceof HttpEntityEnclosingRequestBase && ((HttpEntityEnclosingRequestBase) request).getEntity() != null) {
                request.setHeader(CONTENT_TYPE, TYPE_APPLICATION_JSON);
            }

            String authHeader = getAuthenticationHeader(true);
            request.setHeader("Authorization", authHeader);
            request.setHeader("X-Company-Code",  CustomHTTPClientBaseConfiguration.getPropertyCode());
            request.setHeader("X-P360-Validation",  "true");

            CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

            try (CloseableHttpResponse response = client.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();

                HttpEntity entity = response.getEntity();
                String responseEntity = EntityUtils.toString(entity);
                if (responseCode == 200 || responseCode == 201) {
                    return responseEntity;
                } else if (responseCode == 409) {
                    log.warn("Got response code 409.");
                    return responseEntity;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("The response code of the invoked API " + url + " is " + responseCode +
                                " Response : " + responseEntity);
                    }
                    throw new CustomHTTPClientException("The response code of the invoked API " + url + " is " +
                            responseCode);
                }
            }
        } catch (ConnectTimeoutException e) {
            throw new CustomHTTPClientException("Error while waiting to connect to " + url, e);
        } catch (SocketTimeoutException e) {
            throw new CustomHTTPClientException("Error while waiting for data from " + url, e);
        } catch (IOException e) {
            throw new CustomHTTPClientException("Error while calling endpoint. ", e);
        } catch (ParseException e) {
            throw new CustomHTTPClientException("Error while parsing response. ", e);
        }
    }

    private static void addSslContext(HttpAsyncClientBuilder builder, String tenantDomain) {

        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(CustomHTTPClientDataHolder.getInstance().getTrustStore())
                    .build();

            String hostnameVerifierConfig = CustomHTTPClientBaseConfiguration.getHostNameVerifierLevelKey();
            X509HostnameVerifier hostnameVerifier;
            if ("STRICT".equalsIgnoreCase(hostnameVerifierConfig)) {
                hostnameVerifier = SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER;
            } else if ("ALLOW_ALL".equalsIgnoreCase(hostnameVerifierConfig)) {
                hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            } else {
                hostnameVerifier = SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER;
            }

            builder.setSSLContext(sslContext);
            builder.setHostnameVerifier(hostnameVerifier);
        } catch (Exception e) {
            log.error("Error while creating ssl context for analytics endpoint invocation in tenant domain: " +
                    tenantDomain, e);
        }
    }

    private static String getAuthenticationHeader(boolean useAPIKey) {

        if (useAPIKey) {
            return "Bearer " + CustomHTTPClientBaseConfiguration.getAPIKey();
        }
        String username = CustomHTTPClientDataHolder
                .getInstance().getRealmService().getBootstrapRealmConfiguration().getAdminUserName();
        String password = CustomHTTPClientDataHolder.getInstance().getRealmService().getBootstrapRealmConfiguration().getAdminPassword();
        String toEncode = username + ":" + password;
        byte[] encoding = Base64.encodeBase64(toEncode.getBytes());
        return "Basic " + new String(encoding, Charset.defaultCharset());
    }
}
