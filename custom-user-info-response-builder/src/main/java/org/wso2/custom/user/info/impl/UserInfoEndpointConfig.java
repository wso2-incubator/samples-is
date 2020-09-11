package org.wso2.custom.user.info.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.carbon.identity.oauth.user.UserInfoAccessTokenValidator;
import org.wso2.carbon.identity.oauth.user.UserInfoClaimRetriever;
import org.wso2.carbon.identity.oauth.user.UserInfoRequestValidator;
import org.wso2.carbon.identity.oauth.user.UserInfoResponseBuilder;
import org.wso2.custom.user.info.util.EndpointUtil;

public class UserInfoEndpointConfig {

    private static final Log log = LogFactory.getLog(UserInfoEndpointConfig.class);
    private static UserInfoEndpointConfig config = new UserInfoEndpointConfig();
    private UserInfoRequestValidator requestValidator;
    private UserInfoAccessTokenValidator accessTokenValidator;
    private UserInfoResponseBuilder responseBuilder;
    private UserInfoClaimRetriever claimRetriever;

    private UserInfoEndpointConfig() {

        if (log.isDebugEnabled()) {
            log.debug("Initializing the UserInfoEndpointConfig singleton");
        }
    }

    public static UserInfoEndpointConfig getInstance() {

        return config;
    }

    public UserInfoClaimRetriever getUserInfoClaimRetriever() {

        if (claimRetriever == null) {
            synchronized (UserInfoClaimRetriever.class) {
                if (claimRetriever == null) {
                    try {
                        String claimRetrieverClassName = EndpointUtil.getUserInfoClaimRetriever();
                        Class claimRetrieverClass =
                                this.getClass().getClassLoader()
                                        .loadClass(claimRetrieverClassName);
                        claimRetriever = (UserInfoClaimRetriever) claimRetrieverClass.newInstance();
                    } catch (ClassNotFoundException e) {
                        log.error("Error while loading configuration", e);
                    } catch (InstantiationException e) {
                        log.error("Error while loading configuration", e);
                    } catch (IllegalAccessException e) {
                        log.error("Error while loading configuration", e);
                    }
                }
            }
        }
        return claimRetriever;
    }
}
