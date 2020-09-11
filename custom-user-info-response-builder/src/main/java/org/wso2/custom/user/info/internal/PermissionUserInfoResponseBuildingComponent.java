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
package org.wso2.custom.user.info.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="user.info.custom.permission.claim.handler"
 * immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class PermissionUserInfoResponseBuildingComponent {

    private static Log log = LogFactory.getLog(PermissionUserInfoResponseBuildingComponent.class);
    private static RealmService realmService;

    protected void activate(ComponentContext ctxt) {

        try {
            log.info("Carbon Custom Claim Handler activated successfully.");
        } catch (Exception e) {
            log.error("Failed to activate Carbon Custom Claim Handler ", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Carbon Custom Claim Handler is deactivated ");
        }
    }

    protected void setRealmService(RealmService realmService) {

        PermissionUserInfoResponseBuildingComponent.realmService = realmService;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is set in the Carbon Custom Claim Handler bundle");
        }
    }

    protected void unsetRealmService(RealmService realmService) {

        PermissionUserInfoResponseBuildingComponent.realmService = null;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is unset in the Carbon Custom Claim Handler bundle");
        }
    }

    public static RealmService getRealmService() {

        return PermissionUserInfoResponseBuildingComponent.realmService;
    }
}
