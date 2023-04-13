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

package org.drools.reliability.infinispan;

import org.drools.util.FileUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static org.drools.reliability.core.CacheManagerFactory.RELIABILITY_CACHE_ALLOWED_PACKAGES;

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
        System.setProperty(RELIABILITY_CACHE_ALLOWED_PACKAGES, "org.test.domain");
        FileUtils.deleteDirectory(Path.of(EmbeddedCacheManager.GLOBAL_STATE_DIR));
        LOG.info("### Deleted directory {}", EmbeddedCacheManager.GLOBAL_STATE_DIR);
    }
}
