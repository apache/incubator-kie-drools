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
package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.internal.utils.KieService;

public interface ReliableGlobalResolverFactory extends KieService {

    ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage);

    class Holder {

        private static final ReliableGlobalResolverFactory INSTANCE = createInstance();

        private Holder() {
        }

        static ReliableGlobalResolverFactory createInstance() {
            ReliableGlobalResolverFactory factory = KieService.load(ReliableGlobalResolverFactory.class);
            if (factory == null) {
                return new ReliableGlobalResolverFactoryImpl();
            }
            return factory;
        }
    }

    static ReliableGlobalResolverFactory get() {
        return ReliableGlobalResolverFactory.Holder.INSTANCE;
    }

    static class ReliableGlobalResolverFactoryImpl implements ReliableGlobalResolverFactory {

        static int servicePriorityValue = 0; // package access for test purposes

        @Override
        public ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage) {
            return new ReliableGlobalResolver(storage);
        }

        @Override
        public int servicePriority() {
            return servicePriorityValue;
        }
    }
}
