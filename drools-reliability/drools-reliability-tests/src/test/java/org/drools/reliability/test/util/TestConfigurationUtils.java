package org.drools.reliability.test.util;

import org.drools.reliability.core.CoreServicePrioritySupport;
import org.drools.reliability.h2mvstore.H2MVStoreServicePrioritySupport;
import org.drools.reliability.infinispan.InfinispanServicePrioritySupport;

import static org.drools.reliability.test.util.TestConfigurationUtils.Module.H2MVSTORE;
import static org.drools.reliability.test.util.TestConfigurationUtils.Module.INFINISPAN;
import static org.drools.util.Config.getConfig;

public class TestConfigurationUtils {

    public enum Module {
        INFINISPAN,
        H2MVSTORE
    }

    public static final String DROOLS_RELIABILITY_MODULE_TEST = "drools.reliability.module.test";

    private TestConfigurationUtils() {
        // util class
    }

    public static void configureServicePriorities() {
        Module module = Module.valueOf(getConfig(DROOLS_RELIABILITY_MODULE_TEST, INFINISPAN.name()));
        if (module == INFINISPAN) {
            prioritizeInfinispanServices();
        } else if (module == H2MVSTORE) {
            prioritizeH2MVStoreServices();
        } else {
            throw new IllegalStateException("Unknown module: " + module);
        }
    }

    private static void prioritizeInfinispanServices() {
        InfinispanServicePrioritySupport.setInfinispanStorageManagerFactoryPriority(100);
        InfinispanServicePrioritySupport.setSimpleInfinispanReliableObjectStoreFactoryPriority(100);
        InfinispanServicePrioritySupport.setInfinispanReliableGlobalResolverFactoryPriority(100);
    }

    private static void prioritizeH2MVStoreServices() {
        H2MVStoreServicePrioritySupport.setH2MVStoreStorageManagerFactoryPriority(100);
        CoreServicePrioritySupport.setSimpleSerializationReliableObjectStoreFactoryPriority(100);
        CoreServicePrioritySupport.setReliableGlobalResolverFactoryImplPriority(100);
    }
}
