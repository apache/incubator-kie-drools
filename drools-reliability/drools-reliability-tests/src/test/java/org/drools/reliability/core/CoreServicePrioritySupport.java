package org.drools.reliability.core;

public class CoreServicePrioritySupport {

    private CoreServicePrioritySupport() {
        // utils class
    }

    public static void setSimpleSerializationReliableObjectStoreFactoryPriority(int priority) {
        SimpleSerializationReliableObjectStoreFactory.servicePriorityValue = priority;
    }

    public static void setReliableGlobalResolverFactoryImplPriority(int priority) {
        ReliableGlobalResolverFactory.ReliableGlobalResolverFactoryImpl.servicePriorityValue = priority;
    }
}
