package org.wso2.custom.http.client.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CustomHTTPClientBaseConfiguration implements CustomHTTPClientConfiguration {


    private static final String CONFIG_FILE = "config.properties";
    private static final String CONFIG_FILE_PATH = CarbonUtils.getCarbonConfigDirPath() + File.separator + CONFIG_FILE;

    private static final String PROPERTY_CODE_KEY = "property.code";
    private static final String CONNECTION_API_KEY = "connection.api.key";
    private static final String CONNECTION_TIMEOUT_KEY = "connection.timeout";
    private static final String CONNECTION_REQUEST_TIMEOUT_KEY = "connection.request.timeout";
    private static final String CONNECTION_SOCKET_TIMEOUT_KEY = "connection.socket.timeout";
    private static final String ASYC_MAX_CONNECTION_KEY = "aync.max.connection";
    private static final String ASYC_MAX_CONNECTION_PER_ROUTE_KEY = "aync.max.connection.per.route";
    private static final String ASYC_HOST_NAME_VERIFIER_LEVEL_KEY = "aync.hostname.verifier.level";

    private static final Log log = LogFactory.getLog(CustomHTTPClientBaseConfiguration.class);

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Error while loading configurations from " + CONFIG_FILE_PATH, e);
        }
    }

    public CustomHTTPClientBaseConfiguration() {

    }

    public Properties getProperties() {

        return properties;
    }

    public static String getPropertyCode() {

        return properties.getProperty(PROPERTY_CODE_KEY);
    }


    public static String getAPIKey() {

        return properties.getProperty(CONNECTION_API_KEY);
    }

    public static int getConnectionTimeout() {

        return getInt(properties.getProperty(CONNECTION_TIMEOUT_KEY));
    }

    public static int getConnectionRequestTimeout() {

        return getInt(properties.getProperty(CONNECTION_REQUEST_TIMEOUT_KEY));
    }

    public static int getSocketTimeout() {

        return getInt(properties.getProperty(CONNECTION_SOCKET_TIMEOUT_KEY));
    }

    public static int getMaxConnections() {

        return getInt(properties.getProperty(ASYC_MAX_CONNECTION_KEY));
    }

    public static int getMaxConnectionsPerRoute() {

        return getInt(properties.getProperty(ASYC_MAX_CONNECTION_PER_ROUTE_KEY));
    }

    public static String getHostNameVerifierLevelKey() {

        return properties.getProperty(ASYC_HOST_NAME_VERIFIER_LEVEL_KEY);
    }

    private static int getInt(String value) {

        return Integer.parseInt(value);
    }
}
