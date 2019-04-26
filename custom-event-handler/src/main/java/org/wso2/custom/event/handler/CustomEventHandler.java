/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
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
package org.wso2.custom.event.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;

import java.util.Map;

public class CustomEventHandler extends AbstractEventHandler {

    private static Log log = LogFactory.getLog(CustomEventHandler.class);

    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        log.info("Custom event handler received events successfully.");
        String eventName = "POST_AUTHENTICATION";
        if (eventName.equals(event.getEventName())) {
            Map<String, Object> eventProperties = event.getEventProperties();
            String userName = (String) eventProperties.get(IdentityEventConstants.EventProperty.USER_NAME);
            log.info("Authenticated user : " + userName);
        }
    }

    @Override
    public String getName() {
        return "custom.event.handler";
    }
}
