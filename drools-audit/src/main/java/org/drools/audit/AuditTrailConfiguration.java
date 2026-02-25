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

import jakarta.persistence.EntityManagerFactory;

import org.drools.audit.jpa.JpaAuditStore;
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
 * <p>Usage with JPA store:</p>
 * <pre>{@code
 * AuditTrailService service = AuditTrailConfiguration.builder()
 *     .jpa(entityManagerFactory)
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
        private EntityManagerFactory emf;
        private StoreType storeType = StoreType.IN_MEMORY;

        private Builder() {
        }

        public Builder inMemory() {
            this.storeType = StoreType.IN_MEMORY;
            return this;
        }

        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Builder jpa(EntityManagerFactory emf) {
            this.storeType = StoreType.JPA;
            this.emf = emf;
            return this;
        }

        public Builder store(AuditStore store) {
            this.storeType = StoreType.CUSTOM;
            this.store = store;
            return this;
        }

        public AuditTrailService build() {
            AuditStore resolvedStore = switch (storeType) {
                case IN_MEMORY -> new InMemoryAuditStore(maxCapacity);
                case JPA -> {
                    if (emf == null) {
                        throw new IllegalStateException("EntityManagerFactory is required for JPA store");
                    }
                    yield new JpaAuditStore(emf);
                }
                case CUSTOM -> {
                    if (store == null) {
                        throw new IllegalStateException("AuditStore instance is required for custom store");
                    }
                    yield store;
                }
            };
            return new AuditTrailService(resolvedStore);
        }

        private enum StoreType {
            IN_MEMORY, JPA, CUSTOM
        }
    }
}
