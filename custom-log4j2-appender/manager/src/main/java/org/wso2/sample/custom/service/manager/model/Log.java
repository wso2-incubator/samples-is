package org.wso2.sample.custom.service.manager.model;

import java.util.Map;

public class Log {

    private String formattedMessage;
    private Map<String, String> contextData;

    public Log(String formattedMessage, Map<String, String> contextData) {

        this.formattedMessage = formattedMessage;
        this.contextData = contextData;
    }

    public String getFormattedMessage() {

        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {

        this.formattedMessage = formattedMessage;
    }

    public Map<String, String> getContextData() {

        return contextData;
    }

    public void setContextData(Map<String, String> contextData) {

        this.contextData = contextData;
    }
}
