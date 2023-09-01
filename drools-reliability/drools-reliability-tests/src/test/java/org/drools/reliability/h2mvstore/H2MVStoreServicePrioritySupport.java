package org.drools.reliability.h2mvstore;

public class H2MVStoreServicePrioritySupport {

    private H2MVStoreServicePrioritySupport() {
        // utils class
    }

    public static void setH2MVStoreStorageManagerFactoryPriority(int priority) {
        H2MVStoreStorageManagerFactory.servicePriorityValue = priority;
    }
}
