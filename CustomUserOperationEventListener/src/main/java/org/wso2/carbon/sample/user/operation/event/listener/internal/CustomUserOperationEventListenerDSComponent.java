package org.wso2.carbon.sample.user.operation.event.listener.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.sample.user.operation.event.listener.CustomUserOperationEventListener;
import org.wso2.carbon.user.core.listener.UserOperationEventListener;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.Properties;
import javax.jws.soap.SOAPBinding;

/**
 * @scr.component name="sample.user.operation.event.listener.dscomponent" immediate=true
 */

public class CustomUserOperationEventListenerDSComponent {

    private static Log log = LogFactory.getLog(CustomUserOperationEventListenerDSComponent.class);

    protected void activate(ComponentContext context) {

        CustomUserOperationEventListener customUserOperationEventListener = new
                CustomUserOperationEventListener();
        //register the custom listener as an OSGI service.
        context.getBundleContext().registerService(
                UserOperationEventListener.class.getName(), customUserOperationEventListener, null);

        log.info("CustomUserOperationEventListenerDSComponent bundle activated successfully..");
    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("CustomUserOperationEventListenerDSComponent is deactivated ");
        }
    }

}
