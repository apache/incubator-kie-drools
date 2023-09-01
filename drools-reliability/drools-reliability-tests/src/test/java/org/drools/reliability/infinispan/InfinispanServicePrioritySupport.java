package org.drools.reliability.infinispan;

public class InfinispanServicePrioritySupport {

    private InfinispanServicePrioritySupport() {
        // utils class
    }

    public static void setInfinispanStorageManagerFactoryPriority(int priority) {
        InfinispanStorageManagerFactory.servicePriorityValue = priority;
    }

    public static void setSimpleInfinispanReliableObjectStoreFactoryPriority(int priority) {
        SimpleInfinispanReliableObjectStoreFactory.servicePriorityValue = priority;
    }

    public static void setInfinispanReliableGlobalResolverFactoryPriority(int priority) {
        InfinispanReliableGlobalResolverFactory.servicePriorityValue = priority;
    }
}
