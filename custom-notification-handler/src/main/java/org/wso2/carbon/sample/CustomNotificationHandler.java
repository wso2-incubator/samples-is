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

package org.wso2.carbon.sample;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.handler.InitConfig;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.notification.NotificationConstants;
import org.wso2.carbon.identity.event.handler.notification.NotificationHandler;
import org.wso2.carbon.identity.event.handler.notification.email.bean.Notification;
import org.wso2.carbon.identity.event.handler.notification.util.NotificationUtil;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.sample.internal.CustomNotificationHandlerDataHolder;
import org.wso2.carbon.user.api.UserStoreException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Customization to change the email template according to the following two flows (email template changing logic is
 * added to the buildNotification() method in the CustomNotificationUtil class).
 * <p>
 * Changing the email template type during the user self signup REST API.
 * ---------------------------------------------------------------------
 * Default email template type will be overridden if the self signup request parameter map contains the
 * customTemplateType key with the value of the custom template.
 * <p>
 * <p>
 * <p>
 * Role based email template change during the user password reset
 * ----------------------------------------------------------
 * <p>
 * Default email template will be overridden by checking a specific role of the user. A registry resource
 * with the path /identity/config/customRoleBasedEmailTemplateTypeConfig needs to be configured by
 * providing a set of registry properties mapping a role to a custom email template
 * as follows.
 * <p>
 * propertyName= <role name>  propertyValue= <custom email template type>
 * <p>
 * e.g. propertyName = role1  propertyValue = PasswordResetForRole1
 * <p>
 * If the user is assigned to one of the roles configured as above in the registry, the corresponding custom
 * email template will be used.
 */
public class CustomNotificationHandler extends NotificationHandler {

    private static final String CLAIM_URI_ROLE = "http://wso2.org/claims/role";
    private static final String CUSTOM_TEMPLATE_TYPE_PROPERTY_KEY = "customTemplateType";
    private static final String ACCOUNT_CONFIRMATION_EMAIL_TEMPLATE_TYPE = "AccountConfirmation";
    private static final String PASSWORD_RESET_EMAIL_TEMPLATE_TYPE = "PasswordReset";
    private static final String CONFIG_LOCATION = "/identity/config/customRoleBasedEmailTemplateTypeConfig";

    private static Log log = LogFactory.getLog(CustomNotificationHandler.class);

    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        Map<String, String> placeHolderData = new HashMap<>();
        for (Map.Entry<String, Object> entry : event.getEventProperties().entrySet()) {
            if (entry.getValue() instanceof String) {
                placeHolderData.put(entry.getKey(), (String) entry.getValue());
            }
        }

        String notificationEvent = (String) event.getEventProperties().get(NotificationConstants.EmailNotification
                .EMAIL_TEMPLATE_TYPE);
        if (ACCOUNT_CONFIRMATION_EMAIL_TEMPLATE_TYPE.equalsIgnoreCase(notificationEvent) && placeHolderData.containsKey
                (CUSTOM_TEMPLATE_TYPE_PROPERTY_KEY)) {
            event.getEventProperties().put(NotificationConstants.EmailNotification
                    .EMAIL_TEMPLATE_TYPE, placeHolderData.get(CUSTOM_TEMPLATE_TYPE_PROPERTY_KEY));
            if (log.isDebugEnabled()) {
                log.debug("Using the custom email template type: " +
                        placeHolderData.get(CUSTOM_TEMPLATE_TYPE_PROPERTY_KEY));
            }
        } else if (PASSWORD_RESET_EMAIL_TEMPLATE_TYPE.equalsIgnoreCase(notificationEvent)) {
            String username = (String) event.getEventProperties().get(IdentityEventConstants.EventProperty.USER_NAME);
            org.wso2.carbon.user.core.UserStoreManager userStoreManager = (org.wso2.carbon.user.core.UserStoreManager)
                    event.getEventProperties().get(
                            IdentityEventConstants.EventProperty.USER_STORE_MANAGER);
            String userStoreDomainName = (String) event.getEventProperties().get(IdentityEventConstants.EventProperty
                    .USER_STORE_DOMAIN);
            String tenantDomain = (String) event.getEventProperties().get(IdentityEventConstants.EventProperty
                    .TENANT_DOMAIN);

            Map<String, String> userClaims = new HashMap<>();
            if (StringUtils.isNotBlank(username) && userStoreManager != null) {
                userClaims = NotificationUtil.getUserClaimValues(username, userStoreManager);
            } else if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(userStoreDomainName) &&
                    StringUtils.isNotBlank(tenantDomain)) {
                userClaims = NotificationUtil.getUserClaimValues(username, userStoreDomainName, tenantDomain);
            }
            String roleClaimString = userClaims.get(CLAIM_URI_ROLE);

            Registry registry = null;
            Resource resource = null;
            try {
                if (CustomNotificationHandlerDataHolder.getInstance().getRealmService() != null && StringUtils
                        .isNotBlank(tenantDomain)) {
                    registry = getRegistry(CustomNotificationHandlerDataHolder.getInstance().getRealmService()
                            .getTenantManager()
                            .getTenantId(tenantDomain));
                } else {
                    throw new IdentityEventException("Unable to retrieve the realm service for custom claim " +
                            "notification handler");
                }

                if (registry != null && registry.resourceExists(CONFIG_LOCATION)) {
                    resource = registry.get(CONFIG_LOCATION);
                    if (roleClaimString != null) {
                        List<String> roleList = Arrays.asList(roleClaimString.trim().split(","));
                        for (String role : roleList) {
                            if (resource.getProperty(role) != null) {
                                event.getEventProperties().put(NotificationConstants.EmailNotification
                                        .EMAIL_TEMPLATE_TYPE, resource.getProperty(role));
                                if (log.isDebugEnabled()) {
                                    log.debug("Using the custom email template type: " + resource.getProperty(role));
                                }
                                break;
                            }
                        }
                    } else {
                        throw new IdentityEventException("Unable to find the role list claim " +
                                "for the user: " + username);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Registry configurations for custom notification handler is not defined. " +
                                "Hence, using the default notification handler");
                    }
                }
            } catch (UserStoreException e) {
                throw new IdentityEventException("Unable to retrieve the registry for the tenant domain: " +
                        tenantDomain, e);
            } catch (RegistryException e) {
                throw new IdentityEventException("Error occurred while reading the registry configurations.", e);
            }
        }
        Notification notification = NotificationUtil.buildNotification(event, placeHolderData);
        super.publishToStream(notification, placeHolderData);
    }

    private static Registry getRegistry(int tenantId) throws IdentityEventException, RegistryException {

        if (CustomNotificationHandlerDataHolder.getInstance().getRegistryService() != null) {
            return CustomNotificationHandlerDataHolder.getInstance().getRegistryService().getConfigSystemRegistry
                    (tenantId);
        } else {
            throw new IdentityEventException("Unable to retrieve the registry service for custom notification " +
                    "handler");
        }
    }

    @Override
    public void init(InitConfig configuration) throws IdentityRuntimeException {

    }

    @Override
    public String getName() {

        return "customEmailSend";
    }
}
