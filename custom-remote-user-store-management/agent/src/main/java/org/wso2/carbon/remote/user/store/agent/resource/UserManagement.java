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

package org.wso2.carbon.remote.user.store.agent.resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.remote.user.store.agent.constant.CommonConstants;
import org.wso2.carbon.remote.user.store.agent.exception.UserStoreException;
import org.wso2.carbon.remote.user.store.agent.manager.common.UserStoreManager;
import org.wso2.carbon.remote.user.store.agent.manager.common.UserStoreManagerBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.remote.user.store.agent.util.DatabaseUtils.createUserStoreDataSource;

/**
 * REST endpoint for authentication.
 * This will be available at https://localhost:8888/wso2agent/authenticate
 */
@Path(CommonConstants.APPLICATION_CONTEXT_PATH)
public class UserManagement {

    private static Logger log = LoggerFactory.getLogger(UserManagement.class);
    private static Properties connectionProperties = new Properties();
    private DataSource dataSource;

    public UserManagement(Properties connectionProperties) {

        this.dataSource = createUserStoreDataSource(connectionProperties);
        log.debug("Successfully created the datasource");
    }

    /**
     * Get all the names of the available users.
     *
     * @return 200 OK response with all the available names of the users.
     */
    @GET
    @Path("/list-users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUsers() {

        try {
            Map<String, String[]> returnMap = new HashMap<>();
            UserStoreManager userStoreManager = UserStoreManagerBuilder.getUserStoreManager(dataSource);
            String[] userExists = userStoreManager.doListExistingUsers();
            returnMap.put("users", userExists);
            return Response.status(Response.Status.OK).entity(new JSONObject(returnMap).toString()).build();
        } catch (UserStoreException e) {
            log.error(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }
}
