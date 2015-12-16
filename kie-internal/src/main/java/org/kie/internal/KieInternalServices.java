/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.internal;

import org.kie.internal.process.CorrelationKeyFactory;

public interface KieInternalServices {
    
    CorrelationKeyFactory newCorrelationKeyFactory();

    /**
     * A Factory for this KieServices
     */
    public static class Factory {
        private static KieInternalServices INSTANCE;

        static {
            try {                
                INSTANCE = ( KieInternalServices ) Class.forName( "org.kie.internal.builder.impl.KieInternalServicesImpl" ).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to instance KieServices", e);
            }
        }

        /**
         * Returns a reference to the KieServices singleton
         */
        public static KieInternalServices get() {
            return INSTANCE;
        }
    }
}
