package org.wso2.custom.http.client.internal;

import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.user.core.service.RealmService;

import java.security.KeyStore;

public class CustomHTTPClientDataHolder {

    private static final CustomHTTPClientDataHolder dataHolder = new CustomHTTPClientDataHolder();
    private RealmService realmService;
    private ServerConfigurationService serverConfigurationService;
    private KeyStore trustStore;

    public static CustomHTTPClientDataHolder getInstance() {

        return dataHolder;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    public RealmService getRealmService() {

        return realmService;
    }

    public KeyStore getTrustStore() {

        return trustStore;
    }

    public void setTrustStore(KeyStore trustStore) {

        this.trustStore = trustStore;
    }

    public ServerConfigurationService getServerConfigurationService() {

        return serverConfigurationService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {

        this.serverConfigurationService = serverConfigurationService;
    }
}
