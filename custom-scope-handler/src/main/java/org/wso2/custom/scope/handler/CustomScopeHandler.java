/*
 *   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.custom.scope.handler;

import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.validators.OAuth2ScopeHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom scope handler to remove the scopes other than the allowed scopes from the token request.
 */
public class CustomScopeHandler extends OAuth2ScopeHandler {

    public boolean validateScope(OAuthTokenReqMessageContext oAuthTokenReqMessageContext)
            throws IdentityOAuth2Exception {

        List<String> allowedScopes = new ArrayList<>();
        allowedScopes.add("Scope_A");
        allowedScopes.add("Scope_B");
        allowedScopes.add("Scope_C");

        List<String> scopeList = new ArrayList<>();

        String[] requestedScopes = oAuthTokenReqMessageContext.getScope();
        if (requestedScopes != null && requestedScopes.length > 0) {
            for (String currentScope : requestedScopes) {
                if (allowedScopes.contains(currentScope)) {
                    scopeList.add(currentScope);
                }
            }
        }

        oAuthTokenReqMessageContext.setScope(scopeList.toArray(new String[0]));
        return true;
    }

    public boolean canHandle(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) {

        return true;
    }
}
