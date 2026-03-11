/*
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
package org.drools.audit;

import org.drools.audit.store.AuditStore;
import org.drools.audit.store.InMemoryAuditStore;

/**
 * Builder for creating a fully configured {@link AuditTrailService}.
 *
 * <p>Usage with in-memory store:</p>
 * <pre>{@code
 * AuditTrailService service = AuditTrailConfiguration.builder()
 *     .inMemory()
 *     .maxCapacity(50_000)
 *     .build();
 * }</pre>
 *
 * <p>Usage with JPA store (requires {@code jakarta.persistence-api} on the classpath):</p>
 * <pre>{@code
 * import org.drools.audit.jpa.JpaAuditStoreBuilder;
 *
 * AuditTrailService service = AuditTrailConfiguration.builder()
 *     .store(JpaAuditStoreBuilder.create(entityManagerFactory))
 *     .build();
 * }</pre>
 *
 * <p>Usage with a custom store:</p>
 * <pre>{@code
 * AuditTrailService service = AuditTrailConfiguration.builder()
 *     .store(myCustomStore)
 *     .build();
 * }</pre>
 */
public final class AuditTrailConfiguration {

    private AuditTrailConfiguration() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AuditStore store;
        private int maxCapacity = 100_000;

        private Builder() {
        }

        public Builder inMemory() {
            this.store = null;
            return this;
        }

        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        /**
         * Sets a pre-built {@link AuditStore} implementation (JPA, custom, etc.).
         * For JPA, use {@code JpaAuditStoreBuilder.create(emf)} to obtain the store.
         */
        public Builder store(AuditStore store) {
            this.store = store;
            return this;
        }

        public AuditTrailService build() {
            AuditStore resolvedStore = (store != null) ? store : new InMemoryAuditStore(maxCapacity);
            return new AuditTrailService(resolvedStore);
        }
    }
}