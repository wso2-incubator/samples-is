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

package org.wso2.sample.custom.service.manager;

import org.apache.commons.logging.LogFactory;
import org.wso2.sample.custom.service.manager.model.Log;

public class CustomServiceManager {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(CustomServiceManager.class);
    private static CustomServiceManager customServiceManager;

    static {
        customServiceManager = new CustomServiceManager();
    }

    private CustomServiceManager() {

    }

    public static CustomServiceManager getInstance() {

        return customServiceManager;
    }

    /**
     * A sample method to showcase the capabilities of consuming the OSGi services available in the default product
     * runtime.
     *
     * @param auditLog A model class for the Log. Should not use the classes loaded in the pax logging bundle.
     * @return
     */
    public boolean isUserAvailable(Log auditLog) {

        log.info("Received a log event to the custom service manager");
        // Here we can consume any OSGi service in the Identity Server runtime, since the CustomServiceManager is
        // deployed in the default Identity Server OSGi runtime.

        String username = extractUserNameFromAuditLog(auditLog.getFormattedMessage());

        return isUserExists(username);
    }

    private String extractUserNameFromAuditLog(String formattedMsg) {

        // Analyze the formatted msg and obtain the user name. A sample value is hardcoded in this method.
        return "John";
    }

    private boolean isUserExists(String userName) {

        // Obtain user store manger for the current tenant, and check user existence via the available OSGi services
        // in the Identity Server runtime. A sample value is hardcoded in this method.
        log.info("Calling OSGi services to check user existence in the default WSO2 IS OSGi runtime");
        return true;
    }
}
