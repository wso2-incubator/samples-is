package org.wso2.carbon.sample.user.operation.event.listener.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.sample.user.operation.event.listener.RandomPasswordUserOperationEventListener;
import org.wso2.carbon.user.core.listener.UserOperationEventListener;

/**
 * @scr.component name="sample.user.operation.event.listener.dscomponent" immediate=true
 */
public class RandomPasswordUserOperationEventListenerDSComponent {

    private static Log log = LogFactory.getLog(RandomPasswordUserOperationEventListenerDSComponent.class);

    protected void activate(ComponentContext context) {

        RandomPasswordUserOperationEventListener randomPasswordUserOperationEventListener = new
                RandomPasswordUserOperationEventListener();
        // Register the custom listener as an OSGI service.
        context.getBundleContext().registerService(
                UserOperationEventListener.class.getName(), randomPasswordUserOperationEventListener, null);

        log.info("RandomPasswordUserOperationEventListenerDSComponent bundle activated successfully..");
    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("RandomPasswordUserOperationEventListenerDSComponent is deactivated ");
        }
    }
}
