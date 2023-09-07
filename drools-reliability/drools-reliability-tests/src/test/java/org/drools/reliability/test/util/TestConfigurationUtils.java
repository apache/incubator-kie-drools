/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.reliability.test.util;

import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.StorageManagerFactory;

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
        ReliableGlobalResolverFactory.get("infinispan");
        SimpleReliableObjectStoreFactory.get("infinispan");
        StorageManagerFactory.get("infinispan");
    }

    private static void prioritizeH2MVStoreServices() {
        ReliableGlobalResolverFactory.get("core");
        SimpleReliableObjectStoreFactory.get("core");
        StorageManagerFactory.get("h2mvstore");
    }
}
