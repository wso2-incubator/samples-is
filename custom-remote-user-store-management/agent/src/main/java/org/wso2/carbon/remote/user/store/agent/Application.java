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

package org.wso2.carbon.remote.user.store.agent;

import org.wso2.carbon.remote.user.store.agent.exception.DatabaseAccessException;
import org.wso2.carbon.remote.user.store.agent.resource.UserManagement;
import org.wso2.msf4j.MicroservicesRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.wso2.carbon.remote.user.store.agent.constant.CommonConstants.SERVER_PORT;

/**
 * Application entry point.
 *
 * @since 0.1-SNAPSHOT
 */
public class Application {

    public static void main(String[] args) {

        /*
        Path to the properties file is passed as the first argument.
         */
        Properties connectionProperties = getConnectionProperties(args);
        int serverPort = Integer.parseInt(connectionProperties.getProperty(SERVER_PORT));
        new MicroservicesRunner(serverPort)
                .deploy(new UserManagement(connectionProperties))
                .start();
    }

    private static Properties getConnectionProperties(String[] args) {

        Properties connectionProperties = new Properties();
        if (args == null || args.length == 0) {
            throw new DatabaseAccessException("Cannot find the connection properties file");
        }
        try {
            InputStream configFileInputStream = new FileInputStream(args[0]);
            connectionProperties.load(configFileInputStream);
            return connectionProperties;
        } catch (IOException e) {
            throw new DatabaseAccessException("Error while loading the properties file: " + args[0], e);
        }
    }
}
