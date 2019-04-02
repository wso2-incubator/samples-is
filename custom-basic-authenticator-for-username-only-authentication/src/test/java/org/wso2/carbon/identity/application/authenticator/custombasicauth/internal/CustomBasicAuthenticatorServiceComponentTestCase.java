/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.application.authenticator.custombasicauth.internal;

import org.osgi.service.component.ComponentContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.testutil.IdentityBaseTest;
import org.wso2.carbon.user.core.service.RealmService;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.testng.Assert.assertNotNull;

public class CustomBasicAuthenticatorServiceComponentTestCase extends IdentityBaseTest {

    private RealmService mockRealmService;
    private CustomBasicAuthenticatorServiceComponent customBasicAuthenticatorServiceComponent;
    private ComponentContext mockComponentContext;

    @BeforeTest
    public void setup() {

        customBasicAuthenticatorServiceComponent = new CustomBasicAuthenticatorServiceComponent();
    }

    @Test
    public void setRealmTestCase() throws NoSuchFieldException, IllegalAccessException {
        mockRealmService = mock(RealmService.class);
        customBasicAuthenticatorServiceComponent.setRealmService(mockRealmService);
        assertNotNull(CustomBasicAuthenticatorServiceComponent.getRealmService());
    }

    @Test
    public void deactivateTestCase() throws NoSuchFieldException, IllegalAccessException {
        mockComponentContext = mock(ComponentContext.class);
        customBasicAuthenticatorServiceComponent.deactivate(mockComponentContext);
    }

    @Test
    public void unSetRealmTestCase() throws NoSuchFieldException, IllegalAccessException {
        mockRealmService = mock(RealmService.class);
        customBasicAuthenticatorServiceComponent.unsetRealmService(mockRealmService);
    }
}
