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

package org.wso2.sample.custom.service.manager.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(
        name = "org.wso2.custom.log4j2.appender.service.manager",
        immediate = true)
public class CustomServiceManagerInternalComponent {

    private static final Log log = LogFactory.getLog(CustomServiceManagerInternalComponent.class);

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            log.info("org.wso2.custom.log4j2.appender.service.manager bundle is activated");
        } catch (Throwable e) {
            log.error("org.wso2.custom.log4j2.appender.service.manager bundle activation " +
                    "Failed", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("org.wso2.custom.log4j2.appender.service.manager bundle is " +
                    "de-activated");
        }
    }
}
