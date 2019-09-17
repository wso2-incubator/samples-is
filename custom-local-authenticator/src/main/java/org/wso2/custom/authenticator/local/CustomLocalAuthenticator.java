/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.custom.authenticator.local;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.custom.authenticator.local.internal.CustomLocalAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.authentication.framework.AbstractApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.LocalApplicationAuthenticator;
import org.wso2.carbon.identity.application.authentication.framework.config.ConfigurationFacade;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.InvalidCredentialsException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Username Password based custom Authenticator
 */
public class CustomLocalAuthenticator extends AbstractApplicationAuthenticator
        implements LocalApplicationAuthenticator {

    private static final long serialVersionUID = 4345354156955223654L;
    private static final Log log = LogFactory.getLog(CustomLocalAuthenticator.class);

    private static final String AUTHENTICATOR_NAME = "CustomLocalAuthenticator";
    private static final String AUTHENTICATOR_FRIENDLY_NAME = "Custom Local Authenticator";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String PHOTO_SHARING_ROLE = "customRole";
    private static final String OIDC = "oidc";

    @Override
    protected void initiateAuthenticationRequest(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 AuthenticationContext context)
            throws AuthenticationFailedException {

        log.info("This is the initiateAuthenticationRequest method");
        String loginPage = ConfigurationFacade.getInstance().getAuthenticationEndpointURL();
        // This is the default WSO2 IS login page. If you can create your custom login page you can use
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
     */
    @Override
    protected void processAuthenticationResponse(HttpServletRequest request,
                                                 HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException {

        log.info("This is the processAuthenticationResponse method");
        String username = request.getParameter(USER_NAME);
        boolean isAuthenticated = true;

        if(isAuthenticated) {
            boolean authorization = false;
            if (OIDC.equalsIgnoreCase(context.getRequestType())) {
                log.info("Authentication request type is OIDC. Engaging custom local authenticator...");
                // authorization only for openid connect requests
                try {
                    int tenantId = CustomLocalAuthenticatorServiceComponent.getRealmService().getTenantManager().
                            getTenantId(MultitenantUtils.getTenantDomain(username));
                    UserStoreManager userStoreManager =
                            (UserStoreManager) CustomLocalAuthenticatorServiceComponent.
                                    getRealmService().getTenantUserRealm(tenantId).getUserStoreManager();

                    // verify user is assigned to role
                    authorization = ((AbstractUserStoreManager) userStoreManager).isUserInRole(
                            username, PHOTO_SHARING_ROLE);
                } catch (UserStoreException e) {
                    log.error(e);
                }
            }

            if (!authorization) {
                log.error("user authorization is failed.");

                throw new InvalidCredentialsException("User authentication failed due to invalid credentials",
                        User.getUserFromUserName(username));
            } else {
                context.setSubject(AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(username));
            }
        }
    }

    @Override
    protected boolean retryAuthenticationEnabled() {
        return true;
    }

    @Override
    public String getFriendlyName() {
        //Set the name to be displayed in local authenticator drop down lsit
        return AUTHENTICATOR_FRIENDLY_NAME;
    }

    @Override
    public boolean canHandle(HttpServletRequest httpServletRequest) {

        String userName = httpServletRequest.getParameter(USER_NAME);
        String password = httpServletRequest.getParameter(PASSWORD);
        if (userName != null && password != null) {
            log.info("Custom local authenticator can handle this request.");
            return true;
        }
        log.info("Custom local authenticator cannot handle this request.");
        return false;
    }

    @Override
    public String getContextIdentifier(HttpServletRequest httpServletRequest) {

        return httpServletRequest.getParameter("sessionDataKey");
    }

    @Override
    public String getName() {
        return AUTHENTICATOR_NAME;
    }
}
