package org.wso2.custom.http.client.exception;

public class CustomHTTPClientException extends Exception {

    public CustomHTTPClientException(String message) {
        super(message);
    }

    public CustomHTTPClientException(String message, Throwable e) {
        super(message, e);
    }
}
