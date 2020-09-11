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
package org.wso2.custom.user.info;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.utils.JSONUtils;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.claim.metadata.mgt.ClaimMetadataHandler;
import org.wso2.carbon.identity.claim.metadata.mgt.exception.ClaimMetadataException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth.user.UserInfoEndpointException;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.openidconnect.AbstractUserInfoResponseBuilder;
import org.wso2.carbon.identity.openidconnect.internal.OpenIDConnectServiceComponentHolder;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import org.wso2.custom.user.info.util.ClaimUtil;

import java.util.*;

import static org.apache.commons.collections.MapUtils.isNotEmpty;

public class PermissionsUserInfoResponseBuilder extends AbstractUserInfoResponseBuilder {

    private static final Log log = LogFactory.getLog(PermissionsUserInfoResponseBuilder.class);
    private static final String PERMISSION_CLAIM = "http://wso2.org/claims/permission";
    private static final String LOCAL_DIALECT = "http://wso2.org/claims";
    private static final String OIDC_DIALECT = "http://wso2.org/oidc/claim";

    @Override
    protected Map<String, Object> retrieveUserClaims(OAuth2TokenValidationResponseDTO oAuth2TokenValidationResponseDTO)
            throws UserInfoEndpointException {

        Map<String, Object> userClaims = ClaimUtil.getUserClaimsUsingTokenResponse(oAuth2TokenValidationResponseDTO);
        String userName = null;
        try {
            AccessTokenDO accessTokenDO = OAuth2Util.getAccessTokenDOfromTokenIdentifier(
                    OAuth2Util.getAccessTokenIdentifier(oAuth2TokenValidationResponseDTO));
            String clientId = ClaimUtil.getClientID(accessTokenDO);
            OAuthAppDO oAuthAppDO = OAuth2Util.getAppInformationByClientId(clientId);

            userName = accessTokenDO.getAuthzUser().toFullQualifiedUsername();

            UserRealm realm = IdentityTenantUtil.getRealm(accessTokenDO.getAuthzUser().getTenantDomain(), userName);
            if (realm == null) {
                throw new IdentityException("User realm is empty.");
            }
            List<String> userClaimsInOidcDialect =
                    getUserClaimsInOidcDialect(OAuth2Util.getTenantDomainOfOauthApp(oAuthAppDO), clientId, accessTokenDO.getScope());

            if (log.isDebugEnabled()) {
                if (userClaimsInOidcDialect.isEmpty()) {
                    log.debug("OIDC claim URIs not found.");
                } else {
                    log.debug("OIDC claim URIs: " + userClaimsInOidcDialect.toString());
                }
            }

            Map<String, List<String>> permissionList =
                    createPermissionStructure(userClaimsInOidcDialect, userName, realm);

            if (!permissionList.isEmpty()) {
                userClaims.put("permissions", permissionList);
            }
        } catch (IdentityException e) {
            throw new UserInfoEndpointException("Error occurred while retrieving permission claims of user " + userName, e);
        }

        return userClaims;
    }

    @Override
    protected String buildResponse(OAuth2TokenValidationResponseDTO oAuth2TokenValidationResponseDTO, String s, Map<String, Object> filteredUserClaims) throws UserInfoEndpointException {

        return JSONUtils.buildJSON(filteredUserClaims);
    }

    private List<String> getUserClaimsInOidcDialect(String spTenantDomain, String clientId, String[] requestedScopes)
            throws ClaimMetadataException, IdentityOAuth2Exception {

        Map<String, Object> userClaimsInOidcDialect = new HashMap<>();
        ServiceProvider serviceProvider = OAuth2Util.getServiceProvider(clientId, spTenantDomain);
        ClaimMapping[] requestClaimMappings = serviceProvider.getClaimConfig().getClaimMappings();

        List<String> requestedClaimUris = getRequestedClaimUris(requestClaimMappings);
        // Retrieve OIDC to Local Claim Mappings.
        Map<String, String> oidcToLocalClaimMappings = ClaimMetadataHandler.getInstance()
                .getMappingsMapFromOtherDialectToCarbon(OIDC_DIALECT, null, spTenantDomain, false);
        if (!requestedClaimUris.isEmpty()) {
            for (Map.Entry<String, String> claimMapping : oidcToLocalClaimMappings.entrySet()) {
                if (requestedClaimUris.contains(claimMapping.getValue())) {
                    userClaimsInOidcDialect.put(claimMapping.getKey(), claimMapping.getValue());
                }
            }
        }

        Map<String, Object> filteredUserClaimsInOidcDialect = filterClaimsByScope(userClaimsInOidcDialect,
                requestedScopes, clientId, spTenantDomain);

        return getFilteredClaimUris(filteredUserClaimsInOidcDialect);
    }

    private List<String> getRequestedClaimUris(ClaimMapping[] requestedLocalClaimMap) {

        List<String> claimURIList = new ArrayList<>();
        for (ClaimMapping mapping : requestedLocalClaimMap) {
            if (mapping.isRequested()) {
                claimURIList.add(mapping.getLocalClaim().getClaimUri());
            }
        }
        return claimURIList;
    }

    private List<String> getFilteredClaimUris(Map<String, Object> filteredUserClaimsInOidcDialect) {

        List<String> claimURIList = new ArrayList<>();
        if (isNotEmpty(filteredUserClaimsInOidcDialect)) {
            for (Map.Entry<String, Object> filteredClaim : filteredUserClaimsInOidcDialect.entrySet()) {
                if (filteredClaim.getValue() != null) {
                    claimURIList.add(filteredClaim.getValue().toString());
                }
            }
        }
        return claimURIList;
    }

    private Map<String, Object> filterClaimsByScope(Map<String, Object> userClaims,
                                                      String[] requestedScopes,
                                                      String clientId,
                                                      String serviceProviderTenantDomain) {

        return OpenIDConnectServiceComponentHolder.getInstance()
                .getHighestPriorityOpenIDConnectClaimFilter()
                .getClaimsFilteredByOIDCScopes(userClaims, requestedScopes, clientId, serviceProviderTenantDomain);
    }

    private Map<String, List<String>> createPermissionStructure(List<String> userClaimsInOidcDialect, String userName,
                                                                UserRealm realm) {

        Map<String, List<String>> permissionList = new HashMap<>();

        try {
            AuthorizationManager authorizationManager = realm.getAuthorizationManager();

            for (String claimUri : userClaimsInOidcDialect) {
                if (claimUri.contains(PERMISSION_CLAIM)) {
                    Set<String> leafPermission = new HashSet<>();
                    String permissionRootPath = claimUri.replace(LOCAL_DIALECT, "");
                    if (log.isDebugEnabled()) {
                        log.debug("Retrieving permissions for " + permissionRootPath);
                    }
                    String[] permissions = authorizationManager
                            .getAllowedUIResourcesForUser(MultitenantUtils.getTenantAwareUsername(userName),
                                    permissionRootPath);

                    if (ArrayUtils.isEmpty(permissions)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Permission list is empty for " + permissionRootPath);
                        }
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Retrieved permission list for " + permissionRootPath + ": " +
                                    Arrays.asList(permissions));
                        }
                        for (String permission : permissions) {
                            leafPermission.add(getPermissionLeaf(permissionRootPath, permission));
                        }
                        permissionList.put(permissionRootPath, new ArrayList<>(leafPermission));
                    }
                }
            }
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            log.error("Error while retrieving user claim in local dialect for user: " + userName, e);
        }
        return permissionList;
    }

    private String getPermissionLeaf(String path, String permission) {

        permission = permission.replace(path, "");
        if (StringUtils.isEmpty(permission)) {
            permission = "/";
        }
        return permission;
    }

    protected Map<String, Object> getUserClaimsFilteredByScope(OAuth2TokenValidationResponseDTO validationResponseDTO,
                                                               Map<String, Object> userClaims,
                                                               String[] requestedScopes,
                                                               String clientId,
                                                               String tenantDomain) throws UserInfoEndpointException {

        return userClaims;
    }
}
