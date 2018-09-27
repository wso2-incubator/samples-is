package com.wso2.custom.password.generator.internal;

import com.wso2.custom.password.generator.CustomPasswordGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.user.mgt.common.RandomPasswordGenerator;

/**
 * @scr.component name="com.wso2.custom.password.generator" immediate=true
 */

public class CustomPasswordGeneratorDSComponent {

    private static Log log = LogFactory.getLog(CustomPasswordGeneratorDSComponent.class);

    protected void activate(ComponentContext context) {

        RandomPasswordGenerator customPasswordGenerator = new CustomPasswordGenerator();

        // Register the custom password generator as an OSGI service.
        context.getBundleContext().registerService(RandomPasswordGenerator.class.getName(), customPasswordGenerator,
                null);
        log.info("CustomPasswordGeneratorDSComponent bundle activated successfully..");
    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("CustomPasswordGeneratorDSComponent is deactivated ");
        }
    }

}
