/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.custom.http.client.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.base.api.ServerConfigurationService;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.custom.http.client.config.CustomHTTPClientBaseConfiguration;
import org.wso2.custom.http.client.config.CustomHTTPClientConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.wso2.custom.http.client.CustomHTTPClient.startAsyncClient;

@Component(
        name = "org.wso2.custom.http.client",
        immediate = true)
public class CustomHTTPClientServiceComponent {

    private static final Log log = LogFactory.getLog(CustomHTTPClientServiceComponent.class);

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {

            startAsyncClient();

            String filePath = CustomHTTPClientDataHolder.getInstance().getServerConfigurationService()
                    .getFirstProperty("Security.TrustStore.Location");
            String keyStoreType = CustomHTTPClientDataHolder.getInstance().getServerConfigurationService()
                    .getFirstProperty("Security.TrustStore.Type");
            String password = CustomHTTPClientDataHolder.getInstance().getServerConfigurationService()
                    .getFirstProperty("Security.TrustStore.Password");
            try (InputStream keyStoreStream = new FileInputStream(filePath)) {
                KeyStore keyStore = KeyStore.getInstance(keyStoreType); // or "PKCS12"
                keyStore.load(keyStoreStream, password.toCharArray());
                CustomHTTPClientDataHolder.getInstance().setTrustStore(keyStore);
            } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
                log.error("Error while loading truststore.", e);
            }

            ctxt.getBundleContext().registerService(CustomHTTPClientConfiguration.class.getName(), new CustomHTTPClientBaseConfiguration(), null);

            log.info("CustomHTTPClient bundle is activated");
        } catch (Throwable e) {
            log.error("CustomHTTPClient bundle activation Failed", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        closeAsyncClient();

        if (log.isDebugEnabled()) {
            log.debug("CustomHTTPClient bundle is de-activated");
        }
    }

    private void closeAsyncClient() {
    }

    @Reference(
            name = "realm.service",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService")
    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        CustomHTTPClientDataHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the Realm Service");
        }
        CustomHTTPClientDataHolder.getInstance().setRealmService(null);
    }

    @Reference(
            name = "server.configuration.service",
            service = ServerConfigurationService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetServerConfigurationService"
    )
    protected void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the serverConfigurationService");
        }
        CustomHTTPClientDataHolder.getInstance().setServerConfigurationService(serverConfigurationService);
    }

    protected void unsetServerConfigurationService(ServerConfigurationService serverConfigurationService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the ServerConfigurationService");
        }
        CustomHTTPClientDataHolder.getInstance().setServerConfigurationService(null);
    }
}
