package org.wso2.sample.custom.service.manager.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(
        name = "org.wso2.custom.log4j2.appender.service.manager",
        immediate = true)
public class CustomServiceManagerInternalComponent {

    private static final Log log = LogFactory.getLog(CustomServiceManagerInternalComponent.class);

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            log.info("org.wso2.custom.log4j2.appender.service.manager bundle is activated");
        } catch (Throwable e) {
            log.error("org.wso2.custom.log4j2.appender.service.manager bundle activation " +
                    "Failed", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("org.wso2.custom.log4j2.appender.service.manager bundle is " +
                    "de-activated");
        }
    }
}
