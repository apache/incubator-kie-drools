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

package org.drools.reliability.core;

import org.kie.api.internal.utils.KieService;

public interface StorageManagerFactory extends KieService {

    String SESSION_STORAGE_PREFIX = "session_";
    String SHARED_STORAGE_PREFIX = "shared_";
    String DELIMITER = "_";

    String RELIABILITY_STORAGE_PREFIX = "drools.reliability.storage";

    StorageManager getStorageManager();

    class Holder {
        private static final StorageManagerFactory INSTANCE = createInstance();

        private Holder() {
        }

        static StorageManagerFactory createInstance() {
            StorageManagerFactory factory = KieService.load( StorageManagerFactory.class );
            if (factory == null) {
                throwExceptionForMissingRuntime();
                return null;
            }
            factory.getStorageManager().initStorageManager();
            return factory;
        }
    }

    static StorageManagerFactory get() {
        return StorageManagerFactory.Holder.INSTANCE;
    }

    static <T> T throwExceptionForMissingRuntime() {
        throw new ReliabilityConfigurationException("Cannot find any persistence implementation");
    }
}
