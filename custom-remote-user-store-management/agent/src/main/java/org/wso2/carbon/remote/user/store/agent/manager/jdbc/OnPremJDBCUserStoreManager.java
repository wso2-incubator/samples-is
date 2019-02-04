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

package org.wso2.carbon.remote.user.store.agent.manager.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.remote.user.store.agent.exception.UserStoreException;
import org.wso2.carbon.remote.user.store.agent.manager.common.UserStoreManager;
import org.wso2.carbon.remote.user.store.agent.util.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;

import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.GET_ALL_USERS_SQL;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.TABLE_COLUMN_USER_NAME;

/**
 * User store manager for the remote JDBC user store.
 */
public class OnPremJDBCUserStoreManager implements UserStoreManager {

    private static Log log = LogFactory.getLog(OnPremJDBCUserStoreManager.class);
    private DataSource dataSource;

    public OnPremJDBCUserStoreManager(DataSource dataSource) {

        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    public String[] doListExistingUsers() throws UserStoreException {

        try (Connection dbConnection = this.getDBConnection()) {
            String sql = GET_ALL_USERS_SQL;
            if (log.isDebugEnabled()) {
                log.debug(sql);
            }
            try (PreparedStatement prepStmt = dbConnection.prepareStatement(sql)) {
                ResultSet rs = prepStmt.executeQuery();
                dbConnection.commit();
                List<String> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(rs.getString(TABLE_COLUMN_USER_NAME));
                }
                return users.toArray(new String[]{});
            }
        } catch (SQLException e) {
            log.debug("Error while getting the database connection", e);
            throw new UserStoreException("User Check Failure");
        }
    }

    private Connection getDBConnection() throws SQLException {

        Connection connection = this.dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }
}