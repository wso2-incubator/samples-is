package org.wso2.custom.claim.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.custom.claim.PermissionClaimHandler;

/**
 * @scr.component name="custom.permission.claim.handler"
 * immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class PermissionClaimHandlingComponent {

    private static Log log = LogFactory.getLog(PermissionClaimHandlingComponent.class);
    private static RealmService realmService;

    protected void activate(ComponentContext ctxt) {

        PermissionClaimHandler permissionClaimHandler = new
                PermissionClaimHandler();
        // Register the custom listener as an OSGI service.
        ctxt.getBundleContext().registerService(
                PermissionClaimHandler.class.getName(), permissionClaimHandler, null);

        log.info("Carbon Custom Claim Handler activated successfully.");
    }

    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Carbon Custom Claim Handler is deactivated ");
        }
    }

    protected void setRealmService(RealmService realmService) {

        PermissionClaimHandlingComponent.realmService = realmService;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is set in the Carbon Custom Claim Handler bundle");
        }
    }

    protected void unsetRealmService(RealmService realmService) {

        PermissionClaimHandlingComponent.realmService = null;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is unset in the Carbon Custom Claim Handler bundle");
        }
    }

    public static RealmService getRealmService() {

        return PermissionClaimHandlingComponent.realmService;
    }
}
