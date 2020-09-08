package org.wso2.sample.custom.service.manager;

import org.wso2.sample.custom.service.manager.model.Log;

public class CustomServiceManager {

    private static CustomServiceManager customServiceManager = new CustomServiceManager();

    static {
        customServiceManager = new CustomServiceManager();
    }

    private CustomServiceManager() {
    }

    public static CustomServiceManager getInstance() {

        return customServiceManager;
    }

    /**
     * A sample method to showcase the capabilities of consuming the OSGi services available in the default product
     * runtime.
     *
     * @param log A model class for the Log. Should not use the classes loaded in the pax logging bundle.
     * @return
     */
    public boolean isUserAvailable(Log log) {

        // Here we can consume any OSGi service in the Identity Server runtime, since the CustomServiceManager is
        // deployed in the default Identity Server OSGi runtime.

        String username = extractUserNameFromAuditLog(log.getFormattedMessage());

        return isUserExists(username);
    }

    private String extractUserNameFromAuditLog(String formattedMsg) {

        // Analyze the formatted msg and obtain the user name. A sample value is hardcoded in this method.
        return "John";
    }

    private boolean isUserExists(String userName) {

        // Obtain user store manger for the current tenant, and check user existence via the available OSGi services
        // in the Identity Server runtime. A sample value is hardcoded in this method.
        return true;
    }
}
