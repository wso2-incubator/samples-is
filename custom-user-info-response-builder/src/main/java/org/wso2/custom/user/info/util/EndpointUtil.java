package org.wso2.custom.user.info.util;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;

public class EndpointUtil {

    private static OAuthServerConfiguration oauthServerConfiguration;

    public static OAuthServerConfiguration getOAuthServerConfiguration() {

        return oauthServerConfiguration;
    }
    public static void setOauthServerConfiguration(OAuthServerConfiguration oauthServerConfiguration) {

        EndpointUtil.oauthServerConfiguration = oauthServerConfiguration;
    }

    public static String getUserInfoClaimRetriever() {

        return getOAuthServerConfiguration().getOpenIDConnectUserInfoEndpointClaimRetriever();
    }

    public static String getUserInfoRequestValidator() throws OAuthSystemException {

        return getOAuthServerConfiguration().getOpenIDConnectUserInfoEndpointRequestValidator();
    }

    public static String getAccessTokenValidator() {

        return getOAuthServerConfiguration().getOpenIDConnectUserInfoEndpointAccessTokenValidator();
    }

    public static String getUserInfoResponseBuilder() {

        return getOAuthServerConfiguration().getOpenIDConnectUserInfoEndpointResponseBuilder();
    }
}
