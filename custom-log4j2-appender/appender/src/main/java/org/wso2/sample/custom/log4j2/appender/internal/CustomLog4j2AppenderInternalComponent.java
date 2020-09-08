package org.wso2.sample.custom.log4j2.appender.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

@Component(
        name = "org.wso2.sample.custom.log4j2.appender",
        immediate = true)
public class CustomLog4j2AppenderInternalComponent {

    private static final Log log = LogFactory.getLog(CustomLog4j2AppenderInternalComponent.class);

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            log.info("org.wso2.sample.custom.log4j2.appender bundle is activated");
        } catch (Throwable e) {
            log.error("org.wso2.sample.custom.log4j2.appender bundle activation Failed", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        if (log.isDebugEnabled()) {
            log.debug("org.wso2.sample.custom.log4j2.appender bundle is de-activated");
        }
    }
}
