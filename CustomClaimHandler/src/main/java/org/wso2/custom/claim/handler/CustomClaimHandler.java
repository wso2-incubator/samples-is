/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.custom.claim.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimHandler;

import java.util.HashMap;
import java.util.Map;

public class CustomClaimHandler extends DefaultClaimHandler {

    private static volatile CustomClaimHandler instance;

    public static CustomClaimHandler getInstance() {

        if (instance == null) {
            synchronized (CustomClaimHandler.class) {
                if (instance == null) {
                    instance = new CustomClaimHandler();
                }
            }
        }
        return instance;
    }

    @Override
    public Map<String, String> handleClaimMappings(StepConfig stepConfig,
                                                   AuthenticationContext context, Map<String, String> remoteClaims,
                                                   boolean isFederatedClaims) throws FrameworkException {

        String authenticatedUser = null;

        if (stepConfig != null) {
            // Calling from StepBasedSequenceHandler
            authenticatedUser = stepConfig.getAuthenticatedUser().getUserName();
        } else {
            // Calling from RequestPathBasedSequenceHandler
            authenticatedUser = context.getSequenceConfig().getAuthenticatedUser().getUserName();
        }

        Map<String, String> claims = super.handleClaimMappings(stepConfig, context, remoteClaims, isFederatedClaims);
        claims.putAll(handleExternalClaims(authenticatedUser));

        return claims;
    }

    /**
     * Added method to retrieve claims from external sources. The results will be merged to the local claims when
     * returning the final claim list to be added to the SAML response that is sent back to the SP.
     *
     * @param authenticatedUser : The user for whom we require claim values
     * @return
     */
    private Map<String, String> handleExternalClaims(String authenticatedUser) throws FrameworkException {

        Map<String, String> externalClaims = new HashMap<String, String>();
        externalClaims.put("keplerNumber", "E90836W19881010");
        return externalClaims;
    }
}
