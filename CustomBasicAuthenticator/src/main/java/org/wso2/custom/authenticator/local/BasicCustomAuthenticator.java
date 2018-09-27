/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.custom.authenticator.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.LocalApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.config.ConfigurationFacade;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import org.wso2.custom.authenticator.local.internal.BasicCustomAuthenticatorServiceComponent;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Username Password based custom Authenticator
 */
public class BasicCustomAuthenticator extends AbstractApplicationAuthenticator implements LocalApplicationAuthenticator {

    private static final long serialVersionUID = 4345354156955223654L;
    private static final Log log = LogFactory.getLog(BasicCustomAuthenticator.class);


    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 AuthenticationContext context)
            throws AuthenticationFailedException {

        String loginPage = ConfigurationFacade.getInstance().getAuthenticationEndpointURL();//This is the
        // default WSO2 IS login page. If you can create your custom login page you can use
        // that instead.
        String queryParams =
                FrameworkUtils.getQueryStringWithFrameworkContextId(context.getQueryParams(),
                        context.getCallerSessionKey(),
                        context.getContextIdentifier());

        try {
            String retryParam = "";

            if (context.isRetrying()) {
                retryParam = "&authFailure=true&authFailureMsg=login.fail.message";
            }

            response.sendRedirect(response.encodeRedirectURL(loginPage + ("?" + queryParams)) +
                    "&authenticators=BasicAuthenticator:" + "LOCAL" + retryParam);
        } catch (IOException e) {
            throw new AuthenticationFailedException(e.getMessage(), e);
        }
    }

    /**
     * This method is used to process the authentication response.
     * Inside here we check if this is a authentication request coming from oidc flow and then check if the user is
     * in the 'photoSharingRole'.
     */
    @Override
    protected void processAuthenticationResponse(HttpServletRequest request,
                                                 HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException {

        String username = request.getParameter(BasicCustomAuthenticatorConstants.USER_NAME);
        context.setSubject(AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(username));
        try {
            int tenantId = BasicCustomAuthenticatorServiceComponent.getRealmService().getTenantManager().
                    getTenantId(MultitenantUtils.getTenantDomain(username));
            UserStoreManager userStoreManager = (UserStoreManager) BasicCustomAuthenticatorServiceComponent.getRealmService().
                    getTenantUserRealm(tenantId).getUserStoreManager();
            userStoreManager.setUserClaimValue(username, "http://test.wso2.org/claims/customname",
                    "nilathiru", "default");
        } catch (UserStoreException e) {
            log.error(e);
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            log.error(e);
        }
    }

    @Override
    protected boolean retryAuthenticationEnabled() {
        return true;
    }

    @Override
    public String getFriendlyName() {
        //Set the name to be displayed in local authenticator drop down lsit
        return BasicCustomAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME;
    }

    @Override
    public boolean canHandle(HttpServletRequest httpServletRequest) {
        String userName = httpServletRequest.getParameter(BasicCustomAuthenticatorConstants.USER_NAME);
        String password = httpServletRequest.getParameter(BasicCustomAuthenticatorConstants.PASSWORD);
        if (userName != null && password != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getContextIdentifier(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getParameter("sessionDataKey");
    }

    @Override
    public String getName() {
        return BasicCustomAuthenticatorConstants.AUTHENTICATOR_NAME;
    }
}