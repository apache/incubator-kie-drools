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

package org.drools.reliability;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BeforeAllMethodExtension implements BeforeAllCallback {

    // note: cache directory is shared, so we must not run junit 5 with multi-thread (e.g. ExecutionMode.CONCURRENT)
    // nor surefire-plugin with fork > 1
    // So this flag doesn't have to be AtomicBoolean
    private static boolean initialized = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        // This method will be called before the first test method of all test classes
        // So it makes sure to clean up even if we terminate a process while debugging
        if (initialized) {
            return;
        }
        initialized = true;
        if (!CacheManager.INSTANCE.isRemote()) {
            EmbeddedCacheManagerDelegate.cleanUpGlobalStateAndFileStore();
        }
    }
}
