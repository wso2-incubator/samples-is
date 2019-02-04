/*
 * Copyright (c) 2019, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.carbon.remote.user.store.agent.manager.common;

import org.wso2.carbon.remote.user.store.agent.exception.UserStoreException;

/**
 * Manager class for the database.
 */
public interface UserStoreManager {

    /**
     * Get all the names of the available users in the user store.
     *
     * @return A {@link String} array with all the user names available in the user store.
     * @throws UserStoreException An error while getting user names from the user store.
     */
    String[] doListExistingUsers() throws UserStoreException;
}
