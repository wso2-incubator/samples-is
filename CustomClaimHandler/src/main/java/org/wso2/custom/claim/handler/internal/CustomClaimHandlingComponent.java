package org.wso2.custom.claim.handler.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.custom.claim.handler.CustomClaimHandler;

/**
 * @scr.component name="custom.permission.claim.handler"
 * immediate="true"
 */
public class CustomClaimHandlingComponent {

    private static Log log = LogFactory.getLog(CustomClaimHandlingComponent.class);

    protected void activate(ComponentContext ctxt) {

        CustomClaimHandler customClaimHandler = new
                CustomClaimHandler();
        // Register the custom listener as an OSGI service.
        ctxt.getBundleContext().registerService(
                CustomClaimHandler.class.getName(), customClaimHandler, null);
        log.info("Carbon Custom Claim Handler activated successfully.");
    }

    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Carbon Custom Claim Handler is deactivated ");
        }
    }
}
