package org.wso2.sample.custom.service.manager.internal;

public class CustomServiceManagerComponentDataHolder {

    private static final CustomServiceManagerComponentDataHolder
            dataHolder = new CustomServiceManagerComponentDataHolder();

    public static CustomServiceManagerComponentDataHolder getInstance() {

        return dataHolder;
    }
}
