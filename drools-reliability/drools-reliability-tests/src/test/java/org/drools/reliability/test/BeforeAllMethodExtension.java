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
package org.drools.reliability.test;

import java.nio.file.Path;

import org.drools.reliability.h2mvstore.H2MVStoreStorageManager;
import org.drools.reliability.infinispan.EmbeddedStorageManager;
import org.drools.reliability.test.util.TestConfigurationUtils;
import org.drools.util.FileUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_ALLOWED_PACKAGES;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_MARSHALLER;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER;

public class BeforeAllMethodExtension implements BeforeAllCallback {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeAllMethodExtension.class);

    // note: cache directory is shared, so we must not run junit 5 with multi-thread (e.g. ExecutionMode.CONCURRENT)
    // nor surefire-plugin with fork > 1
    // So this flag doesn't have to be AtomicBoolean
    private static boolean initialized = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        // This method will be called before the first test method of all test classes
        // So it makes sure to clean up even if we terminate a process while debugging
        if (initialized) {
            return;
        }
        initialized = true;

        System.setProperty(INFINISPAN_STORAGE_ALLOWED_PACKAGES, "org.test.domain");
        FileUtils.deleteDirectory(Path.of(EmbeddedStorageManager.GLOBAL_STATE_DIR));
        LOG.info("### Deleted directory {}", EmbeddedStorageManager.GLOBAL_STATE_DIR);

        H2MVStoreStorageManager.cleanUpDatabase();
        LOG.info("### Deleted database file {}", H2MVStoreStorageManager.STORE_FILE_NAME);

        LOG.info("### Set marshaller to {}", System.getProperty(INFINISPAN_STORAGE_MARSHALLER));
        LOG.info("### Set initializer to {}", System.getProperty(INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER));

        // configureServicePriorities triggers Factory instantiation, so should be called after directory cleanup
        TestConfigurationUtils.configureServicePriorities();
    }
}
