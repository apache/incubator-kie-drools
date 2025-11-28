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
package org.drools.ruleunits.impl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.ruleunits.api.RuleUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;

class RuleUnitProviderImplConcurrencyTest {

    @EnabledIfSystemProperty(named = "runTurtleTests", matches = "true")
    @Test
    void loadRuleUnitsFromMultipleThreads() throws InterruptedException {
        // https://github.com/apache/incubator-kie-drools/issues/6445
        for (int n = 0; n < 100; n++) {
            loadRuleUnitsFromMultipleThreadsOnce();
        }
    }

    private void loadRuleUnitsFromMultipleThreadsOnce() throws InterruptedException {
        RuleUnitProviderImpl provider = new RuleUnitProviderImpl();
        provider.invalidateRuleUnits(HelloWorldUnit.class);

        int threadCount = 3; // too many threads don't seem to trigger the issue more often
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount);
        Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        startBarrier.await();
                        RuleUnit<HelloWorldUnit> ruleUnit = provider.getRuleUnit(new HelloWorldUnit());
                        assertThat(ruleUnit).isNotNull();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        errors.add(e);
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                });
            }

            executorService.shutdown();
            assertThat(executorService.awaitTermination(30, TimeUnit.SECONDS)).isTrue();
        } finally {
            executorService.shutdownNow();
        }

        assertThat(errors)
                .withFailMessage(() -> "Unexpected errors while loading rule units concurrently: " + errors)
                .isEmpty();
    }
}
