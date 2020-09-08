package org.wso2.sample.custom.log4j2.appender;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.wso2.sample.custom.service.manager.CustomServiceManager;

import java.io.Serializable;

@Plugin(name = "CustomLog4j2LogAppender", category = "Core", elementType = "appender", printObject = false)
public class CustomLog4j2Appender extends AbstractAppender {

    private static final Log log = LogFactory.getLog(CustomLog4j2Appender.class);

    private CustomLog4j2Appender(String name, Filter filter, final Layout<? extends Serializable> layout,
                                 boolean ignoreExceptions) {

        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static CustomLog4j2Appender createAppender(
            @PluginAttribute(value = "name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter,
            @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions) {

        return new CustomLog4j2Appender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void start() {

        super.start();
        // Do initiate something here.
    }

    @Override
    public void stop() {

        super.stop();
        // Do stop something here.
    }

    @Override
    public void append(LogEvent logEvent) {

        if (log.isDebugEnabled()) {
            log.debug("Log event: " + logEvent.getMessage().getFormattedMessage() + ", received.");
        }

        /*
         Here we can do something with the received log event. Following example demonstrate that.

         In this example, we are using the information from the log event and perform some logic in the Identity
         Server runtime. Keep in mind that this log appender is deployed under the fragmented host: 'org.ops4j.pax
         .logging.pax-logging-log4j2'. Because of that, we cannot use the OSGi services from the Identity Server
         default OSGi runtime.

         So this sample project keep a different OSGi bundle called 'org.wso2.sample.service.manager' deployed in the
          Identity Server runtime, and consume a singleton instance from it. It's also important that we are not using
          any classes loaded by the fragmented host, and pass that to the service manager to avoid class loading
          errors. This is the reason to use a  separate model class to represent the logEvent.
         */
        boolean isUserExists = CustomServiceManager.getInstance().isUserAvailable(new org.wso2.sample.custom.service
                .manager.model.Log(logEvent.getMessage().getFormattedMessage(), logEvent.getContextData().toMap()));

        // Now we have obtained the user existence from identity Server runtime. We will invoke an API endpoint with
        // this information.
        invokeUserExistenceEndpoint(logEvent.getMessage().getFormattedMessage(), isUserExists);
    }

    private void invokeUserExistenceEndpoint(String formattedLogMessage, boolean isUserExists) {

        // We could call an external endpoint here. Keep in mind that this log appender is not async operation.
        // Therefore, the client used to invoke the endpoint needs to invoke the endpoint asynchronously to avoid
        // performance issues.

        return;
    }
}