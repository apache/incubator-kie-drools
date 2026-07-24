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
package org.kie.kogito.app.jobs.springboot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Mock repository that tracks data persistence attempts and simulates
 * transactional behavior to verify that rollbacks prevent duplicate data.
 */
@Component
public class MockDataRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MockDataRepository.class);

    private final ConcurrentHashMap<String, AtomicInteger> committedData = new ConcurrentHashMap<>();
    private final AtomicInteger totalPersistenceAttempts = new AtomicInteger(0);
    private final AtomicInteger successfulCommits = new AtomicInteger(0);
    private final AtomicInteger rolledBackAttempts = new AtomicInteger(0);

    /**
     * Simulates persisting data within a transaction.
     * If the transaction is rolled back, the data should not be committed.
     */
    public void persistData(String key, String value) {
        totalPersistenceAttempts.incrementAndGet();

        // Register a transaction synchronization to track commit/rollback
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // Only commit the data if transaction succeeds
                    committedData.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
                    successfulCommits.incrementAndGet();
                }

                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        rolledBackAttempts.incrementAndGet();
                    }
                }
            });
        } else {
            // No transaction active - commit immediately (shouldn't happen in our tests)
            committedData.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
            successfulCommits.incrementAndGet();
        }
    }

    public int getCommittedCount(String key) {
        AtomicInteger count = committedData.get(key);
        return count != null ? count.get() : 0;
    }

    public int getTotalPersistenceAttempts() {
        return totalPersistenceAttempts.get();
    }

    public int getSuccessfulCommits() {
        return successfulCommits.get();
    }

    public int getRolledBackAttempts() {
        return rolledBackAttempts.get();
    }

    public void reset() {
        committedData.clear();
        totalPersistenceAttempts.set(0);
        successfulCommits.set(0);
        rolledBackAttempts.set(0);
    }
}
