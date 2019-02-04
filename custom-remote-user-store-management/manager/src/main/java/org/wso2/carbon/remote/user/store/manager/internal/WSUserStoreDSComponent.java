/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.remote.user.store.manager.internal;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.remote.user.store.manager.WSUserStoreManager;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.Hashtable;

@Component(
        name = "org.wso2.carbon.remote.user.store.manager.component",
        immediate = true
)
public class WSUserStoreDSComponent {

    private static Log log = LogFactory.getLog(WSUserStoreDSComponent.class);

    private static RealmService realmService;

    protected void activate(ComponentContext componentContext) {

        Hashtable<String, String> props = new Hashtable<String, String>();
        WSUserStoreManager wsUserStoreManager = new WSUserStoreManager();
        componentContext.getBundleContext().registerService(
                UserStoreManager.class.getName(), wsUserStoreManager, props);
        log.info("WSUserStoreManager bundle activated successfully..");
    }

    protected void deactivate(ComponentContext componentContext) {

        if (log.isDebugEnabled()) {
            log.info("WSUserStoreManager bundle is deactivated");
        }
    }

    @Reference(
            name = "realm.service",
            service = org.wso2.carbon.user.core.service.RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {

        log.debug("Setting the Realm Service");
        WSUserStoreComponentHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        log.debug("UnSetting the Realm Service");
        WSUserStoreComponentHolder.getInstance().setRealmService(null);
    }
}
