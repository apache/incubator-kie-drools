/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.concurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.*;

public abstract class AbstractConcurrentTest {

    protected void parallelTest(final int threadCount, final TestExecutor testExecutor) throws InterruptedException {
        final List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int counter = i;
            tasks.add(() -> testExecutor.execute(counter));
        }

        final ExecutorService executorService = createExecutorService(threadCount);
        try {
            executeAndAssertTasks(executorService, tasks, threadCount);
        } finally {
            shutdownExecutorService(executorService);
        }
    }

    protected void parallelTest(final Parameters params, final int repetitions, final int threadCount,
                                final TestWithSessionExecutor testExecutor, final String globalName,
                                final Object global, final String... drls)
            throws InterruptedException {
        for (int rep = 0; rep < repetitions; rep++) {
            final ExecutorService executor = createExecutorService(threadCount);
            final KieBase sharedKieBase = getKieBase(params, drls);
            final KieSession sharedKieSession = getKieSession(sharedKieBase, globalName, global);
            try {
                final List<Callable<Boolean>> tasks = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    final int counter = i;
                    tasks.add(() -> {
                        final KieBase kieBaseForTest = params.sharedKieBase ? sharedKieBase : getKieBase(params, drls);
                        if (params.sharedKieSession) {
                            return testExecutor.execute(sharedKieSession, counter);
                        } else {
                            return executeInSeparateSession(testExecutor, kieBaseForTest, globalName, global, counter);
                        }
                    });
                }

                executeAndAssertTasks(executor, tasks, threadCount);
            } finally {
                sharedKieSession.dispose();
                shutdownExecutorService(executor);
            }
        }
    }

    private void executeAndAssertTasks(final ExecutorService executor, final List<Callable<Boolean>> tasks,
                                       final int threadCount) {
        final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
        for (final Callable<Boolean> task : tasks) {
            ecs.submit(task);
        }

        org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(40), () -> {
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
        });
    }

    private ExecutorService createExecutorService(final int threadCount) {
        return Executors.newFixedThreadPool(threadCount, r -> {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return new Thread(r);
        });
    }

    private void shutdownExecutorService(final ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }

    private boolean executeInSeparateSession(final TestWithSessionExecutor testExecutor, final KieBase kieBase,
                                             final String globalName,
                                             final Object global, final int counter) {
        final KieSession kieSessionLocal = getKieSession(kieBase, globalName, global);
        try {
            return testExecutor.execute(kieSessionLocal, counter);
        } finally {
            kieSessionLocal.dispose();
        }
    }

    protected synchronized KieBase getKieBase(final Parameters params, final String... drls) {
        final KieHelper kieHelper = new KieHelper();
        for (final String drl : drls) {
            kieHelper.addContent(drl, ResourceType.DRL);
        }

        KieBase kieBase;
        if (params.isEnforcedJitting()) {
            kieBase = kieHelper.build(ConstraintJittingThresholdOption.get(0));
        } else {
            kieBase = kieHelper.build();
        }
        if (params.isSerializeKieBase()) {
            kieBase = serializeAndDeserializeKieBase(kieBase);
        }
        return kieBase;
    }

    private KieSession getKieSession(final KieBase kieBase, final String globalName, final Object global) {
        final KieSession kieSession = kieBase.newKieSession();
        if (global != null) {
            kieSession.setGlobal(globalName, global);
        }
        return kieSession;
    }

    private KieBase serializeAndDeserializeKieBase(final KieBase kieBase) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
                out.writeObject(kieBase);
                out.flush();
            }

            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                return (KieBase) in.readObject();
            }
        } catch (final IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    interface TestWithSessionExecutor {

        boolean execute(KieSession kieSession, int counter);
    }

    interface TestExecutor {

        boolean execute(int counter);
    }

    static class Parameters {

        private final boolean enforcedJitting;
        private final boolean serializeKieBase;
        private final boolean sharedKieBase;
        private final boolean sharedKieSession;

        public Parameters(final boolean enforcedJitting, final boolean serializeKieBase) {
            this(enforcedJitting, serializeKieBase, false);
        }

        public Parameters(final boolean enforcedJitting, final boolean serializeKieBase, final boolean sharedKieBase) {
            this(enforcedJitting, serializeKieBase, sharedKieBase, false);
        }

        public Parameters(final boolean enforcedJitting, final boolean serializeKieBase,
                          final boolean sharedKieBase, final boolean sharedKieSession) {
            this.enforcedJitting = enforcedJitting;
            this.serializeKieBase = serializeKieBase;
            this.sharedKieBase = sharedKieBase;
            this.sharedKieSession = sharedKieSession;
        }

        public boolean isEnforcedJitting() {
            return enforcedJitting;
        }

        public boolean isSerializeKieBase() {
            return serializeKieBase;
        }

        public boolean isSharedKieBase() {
            return sharedKieBase;
        }

        public boolean isSharedKieSession() {
            return sharedKieSession;
        }

        private static int bitSetToInt(final BitSet bitSet) {
            int bitInteger = 0;
            for(int i = 0 ; i < 32; i++)
                if(bitSet.get(i))
                    bitInteger |= (1 << i);
            return bitInteger;
        }

        @Override
        public String toString() {
            int result = enforcedJitting ? 1 : 0;
            result += serializeKieBase ? 2 : 0 ;
            result += sharedKieBase ? 4 : 0;
            result += sharedKieSession ? 8 : 0;
            return "Parameters[" + result + "]";
        }
    }
}
