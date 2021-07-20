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
package org.wso2.carbon.identity.application.authenticator.custombasicauth;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.*;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.InvalidCredentialsException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkConstants;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authenticator.custombasicauth.internal.CustomBasicAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Username Only based Custom Basic Authenticator
 */
public class CustomBasicAuthenticator extends AbstractApplicationAuthenticator
        implements LocalApplicationAuthenticator {

    private static final long serialVersionUID = 1819664539416029785L;
    private static final Log log = LogFactory.getLog(CustomBasicAuthenticator.class);
    private static String RE_CAPTCHA_USER_DOMAIN = "user-domain-recaptcha";

    @Override
    public boolean canHandle(HttpServletRequest request) {
        String userName = request.getParameter(CustomBasicAuthenticatorConstants.USER_NAME);
        return userName != null;
    }

    @Override
    public AuthenticatorFlowStatus process(HttpServletRequest request,
                                           HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException, LogoutFailedException {

        if (context.isLogoutRequest()) {
            return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
        } else {
            // if an authentication flow
            if (!context.isLogoutRequest()) {
                if (!canHandle(request)
                        || Boolean.TRUE.equals(request.getAttribute(FrameworkConstants.REQ_ATTR_HANDLED))) {
                    if (getName().equals(context.getProperty(FrameworkConstants.LAST_FAILED_AUTHENTICATOR))) {
                        context.setRetrying(true);
                    }
                    context.setCurrentAuthenticator(getName());
                    context.setRetrying(false);
                    try {
                        processAuthenticationResponse(request, response, context);
                        if (this instanceof LocalApplicationAuthenticator) {
                            if (!context.getSequenceConfig().getApplicationConfig().isSaaSApp()) {
                                String userDomain = context.getSubject().getTenantDomain();
                                String tenantDomain = context.getTenantDomain();
                                if (!StringUtils.equals(userDomain, tenantDomain)) {
                                    context.setProperty("UserTenantDomainMismatch", true);
                                    throw new AuthenticationFailedException("Service Provider tenant domain must be " +
                                            "equal to user tenant domain for non-SaaS applications", context.getSubject());
                                }
                            }
                        }
                        request.setAttribute(FrameworkConstants.REQ_ATTR_HANDLED, true);
                        context.setProperty(FrameworkConstants.LAST_FAILED_AUTHENTICATOR, null);
                        return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
                    } catch (AuthenticationFailedException e) {
                        request.setAttribute(FrameworkConstants.REQ_ATTR_HANDLED, true);
                        context.setProperty(FrameworkConstants.LAST_FAILED_AUTHENTICATOR, getName());
                        return AuthenticatorFlowStatus.INCOMPLETE;

                    }
                }
                return AuthenticatorFlowStatus.INCOMPLETE;
                // if a logout flow
            } else {
                try {
                    if (!canHandle(request)) {
                        context.setCurrentAuthenticator(getName());
                        initiateLogoutRequest(request, response, context);
                        return AuthenticatorFlowStatus.INCOMPLETE;
                    } else {
                        processLogoutResponse(request, response, context);
                        return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
                    }
                } catch (UnsupportedOperationException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring UnsupportedOperationException.", e);
                    }
                    return AuthenticatorFlowStatus.SUCCESS_COMPLETED;
                }
            }
        }
    }

    @Override
    protected void processAuthenticationResponse(HttpServletRequest request,
                                                 HttpServletResponse response, AuthenticationContext context)
            throws AuthenticationFailedException {

        String username = request.getParameter(CustomBasicAuthenticatorConstants.USER_NAME);

        Map<String, Object> authProperties = context.getProperties();
        if (authProperties == null) {
            authProperties = new HashMap<>();
            context.setProperties(authProperties);
        }

        Map<String, String> runtimeParams = getRuntimeParams(context);
        if (runtimeParams != null) {
            String usernameFromContext = runtimeParams.get(FrameworkConstants.JSAttributes.JS_OPTIONS_USERNAME);
            if (usernameFromContext != null && !usernameFromContext.equals(username)) {
                if (log.isDebugEnabled()) {
                    log.debug("Username set for identifier first login: " + usernameFromContext + " and username " +
                            "submitted from login page" + username + " does not match.");
                }
                throw new InvalidCredentialsException("Credential mismatch.");
            }
        }

        boolean isAuthenticated;
        UserStoreManager userStoreManager;
        // Reset RE_CAPTCHA_USER_DOMAIN thread local variable before the authentication
        IdentityUtil.threadLocalProperties.get().remove(RE_CAPTCHA_USER_DOMAIN);
        // Check the authentication
        try {
            int tenantId = IdentityTenantUtil.getTenantIdOfUser(username);
            UserRealm userRealm = CustomBasicAuthenticatorServiceComponent.getRealmService().getTenantUserRealm(tenantId);
            if (userRealm != null) {
                userStoreManager = (UserStoreManager) userRealm.getUserStoreManager();
                isAuthenticated = userStoreManager.isExistingUser(MultitenantUtils.getTenantAwareUsername(username));
            } else {
                throw new AuthenticationFailedException("Cannot find the user realm for the given tenant: " +
                        tenantId, User.getUserFromUserName(username));
            }
        } catch (IdentityRuntimeException e) {
            if (log.isDebugEnabled()) {
                log.debug("CustomBasicAuthentication failed while trying to get the tenant ID of the user " + username, e);
            }
            throw new AuthenticationFailedException(e.getMessage(), User.getUserFromUserName(username), e);
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            if (log.isDebugEnabled()) {
                log.debug("CustomBasicAuthentication failed while trying to authenticate", e);
            }
            throw new AuthenticationFailedException(e.getMessage(), User.getUserFromUserName(username), e);
        }

        if (!isAuthenticated) {
            if (log.isDebugEnabled()) {
                log.debug("User authentication failed due to invalid credentials");
            }
            if (IdentityUtil.threadLocalProperties.get().get(RE_CAPTCHA_USER_DOMAIN) != null) {
                username = IdentityUtil.addDomainToName(
                        username, IdentityUtil.threadLocalProperties.get().get(RE_CAPTCHA_USER_DOMAIN).toString());
            }
            IdentityUtil.threadLocalProperties.get().remove(RE_CAPTCHA_USER_DOMAIN);
            throw new InvalidCredentialsException("User authentication failed due to invalid credentials",
                    User.getUserFromUserName(username));
        }

        String tenantDomain = MultitenantUtils.getTenantDomain(username);

        //TODO: user tenant domain has to be an attribute in the AuthenticationContext
        authProperties.put("user-tenant-domain", tenantDomain);

        username = FrameworkUtils.prependUserStoreDomainToName(username);

        if (getAuthenticatorConfig().getParameterMap() != null) {
            String userNameUri = getAuthenticatorConfig().getParameterMap().get("UserNameAttributeClaimUri");
            if (StringUtils.isNotBlank(userNameUri)) {
                boolean multipleAttributeEnable;
                String domain = UserCoreUtil.getDomainFromThreadLocal();
                if (StringUtils.isNotBlank(domain)) {
                    multipleAttributeEnable = Boolean.parseBoolean(userStoreManager.getSecondaryUserStoreManager(domain)
                            .getRealmConfiguration().getUserStoreProperty("MultipleAttributeEnable"));
                } else {
                    multipleAttributeEnable = Boolean.parseBoolean(userStoreManager.
                            getRealmConfiguration().getUserStoreProperty("MultipleAttributeEnable"));
                }
                if (multipleAttributeEnable) {
                    try {
                        if (log.isDebugEnabled()) {
                            log.debug("Searching for UserNameAttribute value for user " + username +
                                    " for claim uri : " + userNameUri);
                        }
                        String usernameValue = userStoreManager.
                                getUserClaimValue(MultitenantUtils.getTenantAwareUsername(username), userNameUri, null);
                        if (StringUtils.isNotBlank(usernameValue)) {
                            tenantDomain = MultitenantUtils.getTenantDomain(username);
                            usernameValue = FrameworkUtils.prependUserStoreDomainToName(usernameValue);
                            username = usernameValue + "@" + tenantDomain;
                            if (log.isDebugEnabled()) {
                                log.debug("UserNameAttribute is found for user. Value is :  " + username);
                            }
                        }
                    } catch (UserStoreException e) {
                        //ignore  but log in debug
                        if (log.isDebugEnabled()) {
                            log.debug("Error while retrieving UserNameAttribute for user : " + username, e);
                        }
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("MultipleAttribute is not enabled for user store domain : " + domain + " " +
                                "Therefore UserNameAttribute is not retrieved");
                    }
                }
            }
        }

        context.setSubject(AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(username));
    }

    @Override
    protected boolean retryAuthenticationEnabled() {
        return true;
    }

    @Override
    public String getContextIdentifier(HttpServletRequest request) {
        return request.getParameter("sessionDataKey");
    }

    @Override
    public String getFriendlyName() {
        return CustomBasicAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME;
    }

    @Override
    public String getName() {
        return CustomBasicAuthenticatorConstants.AUTHENTICATOR_NAME;
    }
}
