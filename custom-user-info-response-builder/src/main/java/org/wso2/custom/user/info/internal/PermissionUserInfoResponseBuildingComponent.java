package org.wso2.custom.user.info.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="user.info.custom.permission.claim.handler"
 * immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1" policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class PermissionUserInfoResponseBuildingComponent {

    private static Log log = LogFactory.getLog(PermissionUserInfoResponseBuildingComponent.class);
    private static RealmService realmService;

    protected void activate(ComponentContext ctxt) {

        try {
            log.info("Carbon Custom Claim Handler activated successfully.");
        } catch (Exception e) {
            log.error("Failed to activate Carbon Custom Claim Handler ", e);
        }
    }

    protected void deactivate(ComponentContext ctxt) {

        if (log.isDebugEnabled()) {
            log.debug("Carbon Custom Claim Handler is deactivated ");
        }
    }

    protected void setRealmService(RealmService realmService) {

        PermissionUserInfoResponseBuildingComponent.realmService = realmService;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is set in the Carbon Custom Claim Handler bundle");
        }
    }

    protected void unsetRealmService(RealmService realmService) {

        PermissionUserInfoResponseBuildingComponent.realmService = null;
        if (log.isDebugEnabled()) {
            log.debug("RealmService is unset in the Carbon Custom Claim Handler bundle");
        }
    }

    public static RealmService getRealmService() {

        return PermissionUserInfoResponseBuildingComponent.realmService;
    }
}
