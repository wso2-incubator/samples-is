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
package org.wso2.custom.claim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimHandler;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.AuthorizationManager;
import org.wso2.custom.claim.internal.PermissionClaimHandlingComponent;

import java.util.Arrays;
import java.util.Map;

/**
 * Custom OIDC Claim handler that get permissions.
 */
public class PermissionClaimHandler extends DefaultClaimHandler  {

    private static final String PERMISSION_CLAIM = "http://wso2.org/claims/permission";
    private static final Log log = LogFactory.getLog(PermissionClaimHandler.class);

    /**
     * Retrieve permission of a user and assigned as requested claims.
     *
     * @param stepConfig
     * @param context
     * @param remoteClaims
     * @param isFederatedClaims
     * @return
     * @throws FrameworkException
     */
    @Override
    public Map<String, String> handleClaimMappings(StepConfig stepConfig,
                                                   AuthenticationContext context, Map<String, String> remoteClaims,
                                                   boolean isFederatedClaims) throws FrameworkException {

        String userName = stepConfig.getAuthenticatedUser().getUserName();
        Map<String, String> requestedClaimMappings = context.getSequenceConfig().getApplicationConfig().
                getRequestedClaimMappings();
        Map<String, String> claimMappings = super.handleClaimMappings(stepConfig, context, remoteClaims,
                isFederatedClaims);
        try {
            AuthorizationManager authorizationManager =  PermissionClaimHandlingComponent.getRealmService()
                                                            .getBootstrapRealm().getAuthorizationManager();
            log.info("Get permission list for the user " + userName);
            // Get permission from the root for a user.
            String[] permissionList = authorizationManager.getAllowedUIResourcesForUser(userName, "/");
            String permissionvalue = Arrays.toString(permissionList);
            permissionvalue = permissionvalue.substring(1, permissionvalue.length() - 1);
            // Check whether permission claim selected as requested claim.
            for (String key : requestedClaimMappings.keySet()) {
                if (PERMISSION_CLAIM.equals(key)) {
                    claimMappings.put("permission", permissionvalue);
                }
            }
        } catch (UserStoreException e) {
            throw new FrameworkException("Error while getting real service or getting permission list for the user "
                    + userName, e);
        }
        return claimMappings;
    }
}
