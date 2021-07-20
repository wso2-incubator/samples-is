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
package org.wso2.carbon.identity.application.authenticator.custombasicauth;

import org.apache.commons.lang.StringUtils;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.identity.application.authentication.framework.AuthenticatorFlowStatus;
import org.wso2.carbon.identity.application.authentication.framework.config.builder.FileBasedConfigurationBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.AuthenticatorConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.exception.LogoutFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.application.authenticator.custombasicauth.internal.CustomBasicAuthenticatorServiceComponent;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit test cases for the Custom Basic Authenticator.
 */
@PrepareForTest({IdentityTenantUtil.class, CustomBasicAuthenticatorServiceComponent.class, User
        .class, MultitenantUtils.class, FrameworkUtils.class, FileBasedConfigurationBuilder.class,
        IdentityUtil.class, UserCoreUtil.class})
public class CustomBasicAuthenticatorTestCase extends PowerMockIdentityBaseTest {

    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private AuthenticationContext mockAuthnCtxt;
    private RealmService mockRealmService;
    private UserRealm mockRealm;
    private UserStoreManager mockUserStoreManager;
    private FileBasedConfigurationBuilder mockFileBasedConfigurationBuilder;
    private User mockUser;

    private AuthenticatedUser authenticatedUser;

    private String dummyUserName = "dummyUserName";
    private String dummyPassword = "dummyPassword";
    private int dummyTenantId = -1234;
    private String dummyVal = "dummyVal";
    private String dummyDomainName = "dummyDomain";

    private CustomBasicAuthenticator customBasicAuthenticator;

    @BeforeTest
    public void setup() {

        customBasicAuthenticator = new CustomBasicAuthenticator();
    }

    @DataProvider(name = "UsernameAndPasswordProvider")
    public Object[][] getWrongUsername() {

        return new String[][]{
                {"admin", "true"},
                {null, "false"},
                {"", "true"}
        };
    }

    @Test(dataProvider = "UsernameAndPasswordProvider")
    public void canHandleTestCase(String userName, String expected) {

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter(CustomBasicAuthenticatorConstants.USER_NAME)).thenReturn(userName);
        assertEquals(Boolean.valueOf(expected).booleanValue(), customBasicAuthenticator.canHandle(mockRequest),
                "Invalid can handle response for the request.");
    }

    @Test
    public void processSuccessTestCase() throws Exception {

        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockAuthnCtxt = mock(AuthenticationContext.class);
        when(mockAuthnCtxt.isLogoutRequest()).thenReturn(true);
        assertEquals(customBasicAuthenticator.process(mockRequest, mockResponse, mockAuthnCtxt),
                AuthenticatorFlowStatus.SUCCESS_COMPLETED);
    }

    @Test
    public void processIncompleteTestCase() throws IOException, AuthenticationFailedException, LogoutFailedException {

        when(mockAuthnCtxt.isLogoutRequest()).thenReturn(false);
        assertEquals(customBasicAuthenticator.process(mockRequest, mockResponse, mockAuthnCtxt),
                AuthenticatorFlowStatus.INCOMPLETE);
    }

    @Test
    public void getFriendlyNameTestCase() {

        assertEquals(customBasicAuthenticator.getFriendlyName(), CustomBasicAuthenticatorConstants.AUTHENTICATOR_FRIENDLY_NAME);
    }

    @Test
    public void getNameTestCase() {

        assertEquals(customBasicAuthenticator.getName(), CustomBasicAuthenticatorConstants.AUTHENTICATOR_NAME);
    }

    @Test
    public void retryAuthenticationEnabledTestCase() {

        assertTrue(customBasicAuthenticator.retryAuthenticationEnabled());
    }

    @Test
    public void getContextIdentifierTestCase() {

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter("sessionDataKey")).thenReturn(dummyVal);
        assertEquals(customBasicAuthenticator.getContextIdentifier(mockRequest), dummyVal);
    }

    @DataProvider(name = "realmProvider")
    public Object[][] getRealm() {

        mockRealm = mock(UserRealm.class);
        mockUserStoreManager = mock(UserStoreManager.class);

        return new Object[][]{
                {null, "Cannot find the user realm for the given tenant: " + dummyTenantId, null},
                {mockRealm, "User authentication failed due to invalid credentials", dummyVal},
                {mockRealm, "User authentication failed due to invalid credentials", null},
        };
    }

    @Test(dataProvider = "realmProvider")
    public void processAuthenticationResponseTestCaseForException(Object realm, Object expected, Object
            recapchaUserDomain) throws Exception {

        mockAuthnCtxt = mock(AuthenticationContext.class);
        when(mockAuthnCtxt.getProperties()).thenReturn(new HashMap<String, Object>());

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter(CustomBasicAuthenticatorConstants.USER_NAME)).thenReturn(dummyUserName);

        mockResponse = mock(HttpServletResponse.class);

        mockStatic(IdentityTenantUtil.class);
        when(IdentityTenantUtil.getTenantIdOfUser(dummyUserName)).thenReturn(dummyTenantId);

        mockStatic(CustomBasicAuthenticatorServiceComponent.class);
        mockRealmService = mock(RealmService.class);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService()).thenReturn(mockRealmService);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService().getTenantUserRealm(dummyTenantId)).thenReturn((UserRealm) realm);
        when(mockRealm.getUserStoreManager()).thenReturn(mockUserStoreManager);

        mockStatic(MultitenantUtils.class);
        when(MultitenantUtils.getTenantAwareUsername(dummyUserName)).thenReturn(dummyPassword);
        when(mockUserStoreManager.isExistingUser(
                MultitenantUtils.getTenantAwareUsername(dummyUserName))).thenReturn(false);

        mockStatic(IdentityUtil.class);
        Map<String, Object> mockedThreadLocalMap = new HashMap<>();
        mockedThreadLocalMap.put("user-domain-recaptcha", recapchaUserDomain);
        IdentityUtil.threadLocalProperties.set(mockedThreadLocalMap);

        mockUser = mock(User.class);
        when(mockUser.getUserName()).thenReturn(dummyUserName);
        mockStatic(User.class);
        when(User.getUserFromUserName(anyString())).thenReturn(mockUser);
        try {
            customBasicAuthenticator.processAuthenticationResponse(mockRequest, mockResponse, mockAuthnCtxt);
        } catch (Exception ex) {
            assertEquals(ex.getMessage(), expected);
        }
    }

    @DataProvider(name = "multipleAttributeprovider")
    public Object[][] getMultipleAttributeProvider() {

        String dummyUserNameValue = "dummyusernameValue";
        return new String[][]{
                {null, dummyUserName, null, "true", "false"},
                {null, dummyUserName, "", "true", "false"},
                {null, dummyUserName, dummyUserNameValue, "true", "true"},
                {null, dummyUserName, dummyUserNameValue, "true", "false"},
                {null, dummyUserName, null, "false", "true"},
                {null, dummyUserName, null, "false", "false"},
                {"", dummyUserName, null, "false", "false"},
                {dummyDomainName, dummyUserName, null, "false", "false"},
                {null, "", null, "false", "false"},
                {null, null, null, "false", "false"}
        };
    }

    @Test(dataProvider = "multipleAttributeprovider")
    public void processAuthenticationResponseTestcaseWithMultiAttribute(String domainName, String userNameUri,
            String userNameValue, String multipleAttributeEnable, String debugEnabled)
            throws UserStoreException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            AuthenticationFailedException, NoSuchFieldException {

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("UserNameAttributeClaimUri", userNameUri);
        AuthenticatorConfig authenticatorConfig = new AuthenticatorConfig(dummyUserName, true, parameterMap);

        processAuthenticationResponseStartUp();

        mockStatic(FileBasedConfigurationBuilder.class);
        mockFileBasedConfigurationBuilder = mock(FileBasedConfigurationBuilder.class);
        when(FileBasedConfigurationBuilder.getInstance()).thenReturn(mockFileBasedConfigurationBuilder);
        when(FileBasedConfigurationBuilder.getInstance().getAuthenticatorBean(anyString())).thenReturn(authenticatorConfig);

        mockStatic(UserCoreUtil.class);
        when(UserCoreUtil.getDomainFromThreadLocal()).thenReturn(domainName);

        RealmConfiguration mockRealmConfiguration = mock(RealmConfiguration.class);

        if (StringUtils.isNotBlank(domainName)) {
            when(mockUserStoreManager.getSecondaryUserStoreManager(dummyDomainName)).thenReturn(mockUserStoreManager);
        }

        when(mockUserStoreManager
                .getRealmConfiguration()).thenReturn(mockRealmConfiguration);

        when(mockRealmConfiguration.getUserStoreProperty("MultipleAttributeEnable"))
                .thenReturn(multipleAttributeEnable);

        when(mockUserStoreManager.
                getUserClaimValue(MultitenantUtils.getTenantAwareUsername(dummyUserName), dummyUserName, null))
                .thenReturn(userNameValue);

        when(MultitenantUtils.getTenantDomain(dummyUserName)).thenReturn("dummyTenantDomain");
        when(FrameworkUtils.prependUserStoreDomainToName(userNameValue)).thenReturn(dummyDomainName +
                CarbonConstants.DOMAIN_SEPARATOR + userNameValue);

        mockStatic(IdentityUtil.class);
        when(IdentityUtil.getPrimaryDomainName()).thenReturn(dummyDomainName);

        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {

                authenticatedUser = (AuthenticatedUser) invocation.getArguments()[0];
                return null;
            }
        }).when(mockAuthnCtxt).setSubject(any(AuthenticatedUser.class));

        customBasicAuthenticator.processAuthenticationResponse(mockRequest, mockResponse, mockAuthnCtxt);

        if (StringUtils.isNotBlank(userNameValue)) {
            assertEquals(authenticatedUser.getAuthenticatedSubjectIdentifier(), dummyDomainName +
                    CarbonConstants.DOMAIN_SEPARATOR + userNameValue + "@" + "dummyTenantDomain");
        } else {
            assertEquals(authenticatedUser.getAuthenticatedSubjectIdentifier(), dummyUserName);
        }

    }

    private void processAuthenticationResponseStartUp() throws UserStoreException {

        mockAuthnCtxt = mock(AuthenticationContext.class);
        when(mockAuthnCtxt.getProperties()).thenReturn(null);

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter(CustomBasicAuthenticatorConstants.USER_NAME)).thenReturn(dummyUserName);

        mockResponse = mock(HttpServletResponse.class);

        mockStatic(IdentityTenantUtil.class);
        when(IdentityTenantUtil.getTenantIdOfUser(dummyUserName)).thenReturn(dummyTenantId);

        mockStatic(CustomBasicAuthenticatorServiceComponent.class);
        mockRealmService = mock(RealmService.class);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService()).thenReturn(mockRealmService);
        mockRealm = mock(UserRealm.class);
        mockUserStoreManager = mock(UserStoreManager.class);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService().getTenantUserRealm(dummyTenantId)).thenReturn(mockRealm);
        when(mockRealm.getUserStoreManager()).thenReturn(mockUserStoreManager);

        mockStatic(MultitenantUtils.class);
        when(MultitenantUtils.getTenantAwareUsername(dummyUserName)).thenReturn(dummyUserName);
        when(mockUserStoreManager.isExistingUser(
                MultitenantUtils.getTenantAwareUsername(dummyUserName))).thenReturn(true);

        mockUser = mock(User.class);
        when(mockUser.getUserName()).thenReturn(dummyUserName);
        mockStatic(User.class);
        when(User.getUserFromUserName(anyString())).thenReturn(mockUser);

        mockStatic(FrameworkUtils.class);
        when(MultitenantUtils.getTenantDomain(anyString())).thenReturn("carbon.super");
        when(FrameworkUtils.prependUserStoreDomainToName(anyString())).thenReturn(dummyUserName);
    }

    @Test
    public void processAuthenticationResponseTestcaseWithuserStoreException() throws IOException,
            UserStoreException, NoSuchFieldException, IllegalAccessException {

        mockAuthnCtxt = mock(AuthenticationContext.class);
        when(mockAuthnCtxt.getProperties()).thenReturn(null);

        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameter(CustomBasicAuthenticatorConstants.USER_NAME)).thenReturn(dummyUserName);

        mockResponse = mock(HttpServletResponse.class);

        mockStatic(IdentityTenantUtil.class);
        when(IdentityTenantUtil.getTenantIdOfUser(dummyUserName)).thenReturn(-1234);

        mockStatic(User.class);
        mockUser = mock(User.class);
        when(User.getUserFromUserName(anyString())).thenReturn(mockUser);

        mockStatic(CustomBasicAuthenticatorServiceComponent.class);
        mockRealmService = mock(RealmService.class);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService()).thenReturn(mockRealmService);
        mockRealm = mock(UserRealm.class);
        mockUserStoreManager = mock(UserStoreManager.class);
        when(CustomBasicAuthenticatorServiceComponent.getRealmService().getTenantUserRealm(-1234)).thenThrow(new org
                .wso2.carbon.user.api.UserStoreException());
        try {
            customBasicAuthenticator.processAuthenticationResponse(
                    mockRequest, mockResponse, mockAuthnCtxt);
        } catch (AuthenticationFailedException e) {
            assertNotNull(e);
        }
    }


    @ObjectFactory
    public IObjectFactory getObjectFactory() {

        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }
}

