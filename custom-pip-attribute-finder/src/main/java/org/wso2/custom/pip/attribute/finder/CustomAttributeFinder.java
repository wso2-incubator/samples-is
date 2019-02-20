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
package org.wso2.custom.pip.attribute.finder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.custom.pip.attribute.finder.cache.RoleMappingCache;
import org.wso2.custom.pip.attribute.finder.cache.RoleMappingCacheEntry;
import org.wso2.custom.pip.attribute.finder.internal.AttributeFinderDataHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.entitlement.pip.DefaultAttributeFinder;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomAttributeFinder extends DefaultAttributeFinder {

    private static final Log log = LogFactory.getLog(CustomAttributeFinder.class);

    private static final String ROLE = "http://wso2.org/claims/role";
    private static final String ROLE_MAPPING_PROPERTY_NAME = "RoleMappingJSON";
    private static final String EXTERNAL_ROLE_ELEMENT_NAME = "externalRole";
    private static final String INTERNAL_ROLE_ELEMENT_NAME = "internalRole";


    public void init(Properties properties) throws Exception {

        if (log.isDebugEnabled()) {
            log.info("CustomAttributeFinder is initialized successfully");
        }
    }

    public String getModuleName() { return "Custom Attribute Finder"; }

    @Override
    public Set<String> getAttributeValues(String subjectId, String resourceId, String actionId,
                                          String environmentId, String attributeId, String issuer) throws Exception {

        Set<String> attributeValues = null;

        if (log.isDebugEnabled()) {
            log.debug("CustomAttributeFinder calling custom get attribute values, " +
                    "subjectId: " + subjectId + " resourceId: " + resourceId + " actionId: " + actionId +
                    " environmentId: " + environmentId + " attributeId: " + attributeId + " issuer: " + issuer);
        }

        attributeValues = super.getAttributeValues(subjectId, resourceId, actionId, environmentId,
                attributeId.toString(), issuer);
        if (log.isDebugEnabled()) {
            log.debug("attributeValues from DefaultAttributeFinder: " + attributeValues);
        }

        // If attributeId!=ROLE, just return attributeValues from DefaultAttributeFinder
        if (!ROLE.equals(attributeId)){
            if (log.isDebugEnabled()) {
                log.debug("Returning since attributeId is not " + ROLE);
            }
            return  attributeValues;
        }

        String regexForSecondaryUserStore = ".+" + UserCoreConstants.DOMAIN_SEPARATOR + ".+";
        Pattern pattern = Pattern.compile(regexForSecondaryUserStore);
        Matcher matcher = pattern.matcher(subjectId);

        if (!subjectId.startsWith(UserCoreConstants.PRIMARY_DEFAULT_DOMAIN_NAME + UserCoreConstants.DOMAIN_SEPARATOR)
                && matcher.matches()) {

            // Check for cache validity and populate if invalid.
            if (!isValidRoleMappingCache()) {

                if (log.isDebugEnabled()) {
                    log.debug("Role Mapping Cache is invalidated. Repopulating...");
                }

                RealmConfiguration secondaryRealmConfiguration = null;
                try {
                    int tenantId = AttributeFinderDataHolder.getInstance().getRealmService().getTenantManager().
                            getTenantId(MultitenantUtils.getTenantDomain(subjectId));
                    secondaryRealmConfiguration = AttributeFinderDataHolder.getInstance().getRealmService().
                            getTenantUserRealm(tenantId).getRealmConfiguration().getSecondaryRealmConfig();
                } catch (UserStoreException e) {
                    log.error("Error while retrieving user store configurations", e);
                }

                if (secondaryRealmConfiguration == null) {
                    return null;
                } else {
                    Map<String, String> userStoreProperties = secondaryRealmConfiguration.getUserStoreProperties();
                    String json = userStoreProperties.get(ROLE_MAPPING_PROPERTY_NAME);
                    populateRoleMappingCache(json);
                }
            }

            // Setting role mappings
            setMappedRolesFromCache(attributeValues);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("User not in secondary userStore");
            }
        }
        return attributeValues;
    }

    /**
     * Check for the default cache entry and if it exists, considers the cache is valid.
     *
     * @return True if cache is valid, False otherwise.
     */
    private boolean isValidRoleMappingCache() {

        RoleMappingCache roleMappingCache = RoleMappingCache.getInstance();
        RoleMappingCacheEntry entry = roleMappingCache.getValueFromCache(RoleMappingCache.CACHE_VALIDATION_ENTRY_KEY);
        return entry != null;
    }

    /**
     * Go through external roles in attributeValues set and add mapping roles for each from the cache.
     *
     * @param attributeValues Set of external roles.
     */
    private void setMappedRolesFromCache(Set<String> attributeValues) {

        Set<String> externalRoles = new HashSet<String>(attributeValues);
        RoleMappingCache roleMappingCache = RoleMappingCache.getInstance();
        for (String externalRole:externalRoles) {

            RoleMappingCacheEntry mappedRoles = roleMappingCache.getValueFromCache(externalRole);
            if (mappedRoles != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Role Mapping Cache hit. Key: " + externalRole + ", values: " +
                            mappedRoles.getInternalRoles());
                }
                attributeValues.addAll(mappedRoles.getInternalRoles());
            }
        }
    }

    /**
     * Read the role mapping JSON and populate the role mapping cache.
     *
     * @param roleMappingJSON Role mapping JSON read from the user store configuration.
     */
    private void populateRoleMappingCache(String roleMappingJSON) {

        RoleMappingCache roleMappingCache = RoleMappingCache.getInstance();
        roleMappingCache.clear();

        // Adding cache validation entry
        roleMappingCache.addToCache(RoleMappingCache.CACHE_VALIDATION_ENTRY_KEY, new RoleMappingCacheEntry());

        JSONArray jsonArray = new JSONArray(roleMappingJSON);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject mapping = jsonArray.getJSONObject(i);
            String externalRole = (String) mapping.get(EXTERNAL_ROLE_ELEMENT_NAME);
            Set<String> internalRoles = new HashSet<String>();
            JSONArray mappedRolesArray = mapping.getJSONArray(INTERNAL_ROLE_ELEMENT_NAME);
            for (int j = 0; j < mappedRolesArray.length(); j++) {
                internalRoles.add(mappedRolesArray.getString(j));
            }
            if (log.isDebugEnabled()) {
                log.debug("Adding Role Mapping Cache entry. Key: " + externalRole + ", values: " + internalRoles);
            }
            RoleMappingCacheEntry entry = new RoleMappingCacheEntry();
            entry.setInternalRoles(internalRoles);
            roleMappingCache.addToCache(externalRole, entry);
        }
        if (log.isDebugEnabled()) {
            log.debug("Role Mapping Cache population completed.");
        }
    }
}
