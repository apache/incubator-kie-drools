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
package org.drools.mvel.integrationtests.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractConcurrentInsertionsTest {

    protected void testConcurrentInsertions(final String drl, final int objectCount, final int threadCount,
                                          final boolean newSessionForEachThread, final boolean updateFacts, KieBaseTestConfiguration kieBaseTestConfiguration) throws InterruptedException {

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        final ExecutorService executor = Executors.newFixedThreadPool(threadCount, r -> {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        KieSession ksession = null;
        try {
            final Callable[] tasks = new Callable[threadCount];
            if (newSessionForEachThread) {
                for (int i = 0; i < threadCount; i++) {
                    tasks[i] = getTask(objectCount, kieBase, updateFacts);
                }
            } else {
                ksession = kieBase.newKieSession();
                for (int i = 0; i < threadCount; i++) {
                    tasks[i] = getTask(objectCount, ksession, false, updateFacts);
                }
            }

            final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
            for (final Callable task : tasks) {
                ecs.submit(task);
            }

            int successCounter = 0;
            for (int i = 0; i < threadCount; i++) {
                try {
                    if (ecs.take().get()) {
                        successCounter++;
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertThat(successCounter).isEqualTo(threadCount);
            if (ksession != null) {
                ksession.dispose();
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }

    private Callable<Boolean> getTask( final int objectCount, final KieBase kieBase, final boolean updateFacts) {
        return getTask(objectCount, kieBase.newKieSession(), true, updateFacts);
    }

    protected abstract Callable<Boolean> getTask(int objectCount, KieSession ksession, boolean disposeSession,
            boolean updateFacts);
}
