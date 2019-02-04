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

package org.wso2.carbon.remote.user.store.agent.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.util.Properties;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_DRIVER_NAME;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_MAX_ACTIVE;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_MAX_IDLE;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_MAX_WAIT;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_MIN_EVIC_TABLE_IDLE_TIME_MILLIS;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_MIN_IDLE;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_PASSWORD;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_TEST_WHILE_IDLE;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_URL;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_USER_NAME;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_VALIDATION_INTERVAL;
import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.CONNECTION_PARAMETER_VALIDATION_QUERY;

public class DatabaseUtils {

    private static Log log = LogFactory.getLog(DatabaseUtils.class);
    private static final int DEFAULT_MAX_ACTIVE = 40;
    private static final int DEFAULT_MAX_WAIT = 1000 * 60;
    private static final int DEFAULT_MIN_IDLE = 5;
    private static final int DEFAULT_MAX_IDLE = 6;
    private static DataSource dataSource = null;
    private static final long DEFAULT_VALIDATION_INTERVAL = 30000;

    public static DataSource createUserStoreDataSource(Properties connectionProperties) {

        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(connectionProperties.getProperty(CONNECTION_PARAMETER_DRIVER_NAME));
        if (poolProperties.getDriverClassName() == null) {
            return null;
        }
        poolProperties.setUrl(connectionProperties.getProperty(CONNECTION_PARAMETER_URL));
        poolProperties.setUsername(connectionProperties.getProperty(CONNECTION_PARAMETER_USER_NAME));
        poolProperties.setPassword(connectionProperties.getProperty(CONNECTION_PARAMETER_PASSWORD));

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_ACTIVE) != null &&
                !connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_ACTIVE).trim().equals("")) {
            poolProperties.setMaxActive(Integer.parseInt(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_MAX_ACTIVE)));
        } else {
            poolProperties.setMaxActive(DEFAULT_MAX_ACTIVE);
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_MIN_IDLE) != null &&
                !connectionProperties.getProperty(CONNECTION_PARAMETER_MIN_IDLE).trim().equals("")) {
            poolProperties.setMinIdle(Integer.parseInt(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_MIN_IDLE)));
        } else {
            poolProperties.setMinIdle(DEFAULT_MIN_IDLE);
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_IDLE) != null &&
                !connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_IDLE).trim().equals("")) {
            poolProperties.setMinIdle(Integer.parseInt(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_MAX_IDLE)));
        } else {
            poolProperties.setMinIdle(DEFAULT_MAX_IDLE);
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_WAIT) != null &&
                !connectionProperties.getProperty(CONNECTION_PARAMETER_MAX_WAIT).trim().equals("")) {
            poolProperties.setMaxWait(Integer.parseInt(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_MAX_WAIT)));
        } else {
            poolProperties.setMaxWait(DEFAULT_MAX_WAIT);
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_TEST_WHILE_IDLE) != null &&
                !connectionProperties.getProperty(CONNECTION_PARAMETER_TEST_WHILE_IDLE).trim().equals("")) {
            poolProperties.setTestWhileIdle(Boolean.parseBoolean(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_TEST_WHILE_IDLE)));
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_TIME_BETWEEN_EVICTION_RUNS_MILLIS) != null &&
                !connectionProperties.getProperty(
                        CONNECTION_PARAMETER_TIME_BETWEEN_EVICTION_RUNS_MILLIS).trim().equals("")) {
            poolProperties.setTimeBetweenEvictionRunsMillis(Integer.parseInt(
                    connectionProperties.getProperty(
                            CONNECTION_PARAMETER_TIME_BETWEEN_EVICTION_RUNS_MILLIS)));
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_MIN_EVIC_TABLE_IDLE_TIME_MILLIS) != null &&
                !connectionProperties.getProperty(
                        CONNECTION_PARAMETER_MIN_EVIC_TABLE_IDLE_TIME_MILLIS).trim().equals("")) {
            poolProperties.setMinEvictableIdleTimeMillis(Integer.parseInt(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_MIN_EVIC_TABLE_IDLE_TIME_MILLIS)));
        }

        if (connectionProperties.getProperty(CONNECTION_PARAMETER_VALIDATION_QUERY) != null) {
            poolProperties.setValidationQuery(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_VALIDATION_QUERY));
            poolProperties.setTestOnBorrow(true);
        }
        if (StringUtils.isNotEmpty(connectionProperties.getProperty(CONNECTION_PARAMETER_VALIDATION_INTERVAL)) &&
                StringUtils.isNumeric(connectionProperties.getProperty(CONNECTION_PARAMETER_VALIDATION_INTERVAL))) {
            poolProperties.setValidationInterval(Long.parseLong(connectionProperties.getProperty(
                    CONNECTION_PARAMETER_VALIDATION_INTERVAL)));
        } else {
            poolProperties.setValidationInterval(DEFAULT_VALIDATION_INTERVAL);
        }
        return new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
    }
}
