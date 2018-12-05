/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.sample.user.operation.event.listener;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.common.AbstractUserOperationEventListener;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

public class RandomPasswordUserOperationEventListener extends AbstractUserOperationEventListener {

    private static final int PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new SecureRandom();

    @Override
    public int getExecutionOrderId() {

        /* This listener should execute before the IdentityMgtEventListener
        Hence the number should be < 95 (Execution order ID of IdentityMgtEventListener)
         */
        return 94;
    }

    @Override
    public boolean doPreAddUser(String userName, Object credential, String[] roleList, Map<String, String> claims,
                                String profile, UserStoreManager userStoreManager) throws UserStoreException {

        if (StringUtils.isNotBlank(claims.get("http://wso2.org/claims/identity/askPassword")) && credential
                instanceof StringBuffer) {

            String characters = "23456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ!@#$%&*";
            String digits = "23456789";
            String lowercaseLetters = "abcdefghjkmnpqrstuvwxyz";
            String uppercaseLetters = "ABCDEFGHJKMNPQRSTUVWXYZ";
            String specialCharacters = "!@#$%&*";
            int mandatoryCharactersCount = 4;

            StringBuilder pw = new StringBuilder();
            int index;
            for (int i = 0; i < PASSWORD_LENGTH - mandatoryCharactersCount; i++) {
                index = RANDOM.nextInt(characters.length());
                pw.append(characters.charAt(index));
            }

            index = RANDOM.nextInt(digits.length());
            pw.append(digits.charAt(index));

            index = RANDOM.nextInt(lowercaseLetters.length());
            pw.append(lowercaseLetters.charAt(index));

            index = RANDOM.nextInt(uppercaseLetters.length());
            pw.append(uppercaseLetters.charAt(index));

            index = RANDOM.nextInt(specialCharacters.length());
            pw.append(specialCharacters.charAt(index));

            char[] password = new char[pw.length()];
            pw.getChars(0, pw.length(), password, 0);

            ((StringBuffer) credential).setLength(0);
            ((StringBuffer) credential).append(password);
        }
        return true;
    }
}
