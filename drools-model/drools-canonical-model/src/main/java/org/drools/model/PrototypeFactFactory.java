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
package org.drools.model;

import java.util.Map;
import java.util.ServiceLoader;

public interface PrototypeFactFactory {

    PrototypeFact createMapBasedFact(Prototype prototype);

    PrototypeFact createMapBasedFact(Prototype prototype, Map<String, Object> valuesMap);

    static PrototypeFactFactory get() {
        return Factory.get();
    }

    /**
     * A Factory for this KieServices
     */
    class Factory {

        private static class LazyHolder {
            private static PrototypeFactFactory INSTANCE = ServiceLoader.load(PrototypeFactFactory.class)
                    .findFirst().orElseThrow(() -> new RuntimeException("Unable to fine PrototypeFactFactory service, is drools-model-compiler on the classpath?"));
        }

        /**
         * Returns a reference to the KieServices singleton
         */
        public static PrototypeFactFactory get() {
            return LazyHolder.INSTANCE;
        }
    }
}