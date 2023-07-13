/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
