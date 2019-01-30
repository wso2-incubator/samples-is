/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.sample.identity.oauth2.grant.biometric;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.ResponseHeader;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;

/**
 * New grant type for Identity Server
 */
public class BiometricGrant extends AbstractAuthorizationGrantHandler {

    private static Log log = LogFactory.getLog(BiometricGrant.class);

    public static final String BIO_DATA_PARAM = "biodata";

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext oAuthTokenReqMessageContext)
            throws IdentityOAuth2Exception {

        log.info("Biometric Grant handler is hit");

        boolean authenticationStatus = false;
        String bioData = null;

        // extract request parameters
        RequestParameter[] parameters =
                oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getRequestParameters();

        // find biometric data
        for (RequestParameter parameter : parameters) {
            if (BIO_DATA_PARAM.equals(parameter.getKey())) {
                if (parameter.getValue() != null && parameter.getValue().length > 0) {
                    bioData = parameter.getValue()[0];
                }
            }
        }

        if (bioData != null) {
            //validate biometric data
            authenticationStatus = isValidBioData(bioData);

            if (authenticationStatus) {
                // if valid set authorized biometric user as grant user
                AuthenticatedUser authenticatedUser = new AuthenticatedUser();
                // TODO: 1/30/19 authenticatedUser.setUserName(username);
                oAuthTokenReqMessageContext.setAuthorizedUser(authenticatedUser);
                oAuthTokenReqMessageContext.setScope(
                        oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getScope());
            } else {
                ResponseHeader responseHeader = new ResponseHeader();
                responseHeader.setKey("SampleHeader-999");
                responseHeader.setValue("Provided Bio Data is Invalid.");
                oAuthTokenReqMessageContext.addProperty("RESPONSE_HEADERS", new ResponseHeader[]{responseHeader});
            }
        }

        return authenticationStatus;
    }

    public boolean authorizeAccessDelegation(OAuthTokenReqMessageContext tokReqMsgCtx)
            throws IdentityOAuth2Exception {

        // if we need to just ignore the end user's extended verification
        return true;
    }

    public boolean validateScope(OAuthTokenReqMessageContext tokReqMsgCtx)
            throws IdentityOAuth2Exception {

        // if we need to just ignore the scope verification
        return true;
    }

    /**
     * TODO
     * You need to implement how to validate the biometric data.
     *
     * @param bioData
     * @return
     */
    private boolean isValidBioData(String bioData) {

        // call external web service here
        return true;
    }
}
