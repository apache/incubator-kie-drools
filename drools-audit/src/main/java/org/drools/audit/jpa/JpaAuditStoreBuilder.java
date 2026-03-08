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
package org.drools.audit.jpa;

import jakarta.persistence.EntityManagerFactory;

import org.drools.audit.store.AuditStore;

/**
 * Factory for creating JPA-backed {@link AuditStore} instances.
 *
 * <p>This class intentionally isolates the {@code jakarta.persistence} dependency
 * so that {@link org.drools.audit.AuditTrailConfiguration} can be loaded without
 * JPA on the classpath. Users who need JPA persistence should call this builder
 * and pass the result to
 * {@link org.drools.audit.AuditTrailConfiguration.Builder#store(AuditStore)}.</p>
 *
 * <pre>{@code
 * AuditTrailService service = AuditTrailConfiguration.builder()
 *     .store(JpaAuditStoreBuilder.create(entityManagerFactory))
 *     .build();
 * }</pre>
 */
public final class JpaAuditStoreBuilder {

    private JpaAuditStoreBuilder() {
    }

    /**
     * Creates a {@link JpaAuditStore} backed by the given {@link EntityManagerFactory}.
     *
     * @param emf the entity manager factory for audit event persistence; must not be null
     * @return a new JPA-backed audit store
     * @throws IllegalArgumentException if {@code emf} is null
     */
    public static AuditStore create(EntityManagerFactory emf) {
        if (emf == null) {
            throw new IllegalArgumentException("EntityManagerFactory must not be null");
        }
        return new JpaAuditStore(emf);
    }
}