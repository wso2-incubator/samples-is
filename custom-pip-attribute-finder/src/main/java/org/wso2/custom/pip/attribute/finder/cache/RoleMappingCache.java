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
package org.wso2.custom.pip.attribute.finder.cache;

import org.wso2.carbon.identity.application.common.cache.BaseCache;

/**
 * This class is used to cache Role Mapping of external users.
 */
public class RoleMappingCache extends BaseCache<String, RoleMappingCacheEntry> {

    private static final String ROLE_MAPPING_CACHE_NAME = "RoleMappingCache";
    private static volatile RoleMappingCache instance;
    public static final String CACHE_VALIDATION_ENTRY_KEY = "RoleMappingCacheValidationEntry";

    private RoleMappingCache() {
        super(ROLE_MAPPING_CACHE_NAME);
    }

    /**
     * Returns the Cache instance.
     *
     * @return RoleMappingCache instance.
     */
    public static RoleMappingCache getInstance() {

        if (instance == null) {
            synchronized (RoleMappingCache.class) {
                if (instance == null) {
                    instance = new RoleMappingCache();
                }
            }
        }
        return instance;
    }
}
