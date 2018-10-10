package org.wso2.custom.claim.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.config.model.StepConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.FrameworkException;
import org.wso2.carbon.identity.application.authentication.framework.handler.claims.impl.DefaultClaimHandler;

import java.util.HashMap;
import java.util.Map;

public class CustomClaimHandler extends DefaultClaimHandler {

    private static volatile CustomClaimHandler instance;

    public static CustomClaimHandler getInstance() {

        if (instance == null) {
            synchronized (CustomClaimHandler.class) {
                if (instance == null) {
                    instance = new CustomClaimHandler();
                }
            }
        }
        return instance;
    }

    @Override
    public Map<String, String> handleClaimMappings(StepConfig stepConfig,
                                                   AuthenticationContext context, Map<String, String> remoteClaims,
                                                   boolean isFederatedClaims) throws FrameworkException {

        String authenticatedUser = null;

        if (stepConfig != null) {
            // Calling from StepBasedSequenceHandler
            authenticatedUser = stepConfig.getAuthenticatedUser().getUserName();
        } else {
            // Calling from RequestPathBasedSequenceHandler
            authenticatedUser = context.getSequenceConfig().getAuthenticatedUser().getUserName();
        }

        Map<String, String> claims = super.handleClaimMappings(stepConfig, context, remoteClaims, isFederatedClaims);
        claims.putAll(handleExternalClaims(authenticatedUser));

        return claims;
    }

    /**
     * Added method to retrieve claims from external sources. The results will be merged to the local claims when
     * returning the final claim list to be added to the SAML response that is sent back to the SP.
     *
     * @param authenticatedUser : The user for whom we require claim values
     * @return
     */
    private Map<String, String> handleExternalClaims(String authenticatedUser) throws FrameworkException {

        Map<String, String> externalClaims = new HashMap<String, String>();
        externalClaims.put("keplerNumber", "E90836W19881010");
        return externalClaims;
    }
}
