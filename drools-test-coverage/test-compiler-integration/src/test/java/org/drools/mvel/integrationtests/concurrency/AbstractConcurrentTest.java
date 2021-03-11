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

package org.drools.mvel.integrationtests.concurrency;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

public abstract class AbstractConcurrentTest {

    protected final boolean enforcedJitting;
    protected final boolean serializeKieBase;
    protected final boolean sharedKieBase;
    protected final boolean sharedKieSession;

    public AbstractConcurrentTest(final boolean enforcedJitting, final boolean serializeKieBase,
                                  final boolean sharedKieBase, final boolean sharedKieSession) {
        this.enforcedJitting = enforcedJitting;
        this.serializeKieBase = serializeKieBase;
        this.sharedKieBase = sharedKieBase;
        this.sharedKieSession = sharedKieSession;
    }

    interface TestWithSessionExecutor {
        boolean execute(KieSession kieSession, int counter);
    }

    interface TestExecutor {
        boolean execute(int counter);
    }

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

    protected void parallelTest(final int repetitions, final int threadCount, final TestWithSessionExecutor testExecutor,
                                final String globalName, final Object global, final String... drls)
            throws InterruptedException {
        for (int rep = 0; rep < repetitions; rep++) {
            final ExecutorService executor = createExecutorService(threadCount);
            final KieBase sharedKieBase = getKieBase(drls);
            final KieSession sharedKieSession = getKieSession(sharedKieBase, globalName, global);
            try {
                final List<Callable<Boolean>> tasks = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    final int counter = i;
                    tasks.add(() -> {
                        final KieBase kieBaseForTest = this.sharedKieBase ? sharedKieBase : getKieBase(drls);
                        if (this.sharedKieSession) {
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

    private void executeAndAssertTasks(final ExecutorService executor, final List<Callable<Boolean>> tasks, final int threadCount) {
        final CompletionService<Boolean> ecs = new ExecutorCompletionService<>(executor);
        for (final Callable<Boolean> task : tasks) {
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

        Assertions.assertThat(successCounter).isEqualTo(threadCount);
    }

    private ExecutorService createExecutorService(final int threadCount) {
        return Executors.newFixedThreadPool(threadCount, r -> {
            final Thread t = new Thread( r );
            t.setDaemon( true );
            return new Thread(r);
        });
    }

    private void shutdownExecutorService(final ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }

    private boolean executeInSeparateSession(final TestWithSessionExecutor testExecutor, final KieBase kieBase, final String globalName,
                                             final Object global, final int counter) {
        final KieSession kieSessionLocal = getKieSession(kieBase, globalName, global);
        try {
            return testExecutor.execute(kieSessionLocal, counter);
        } finally {
            kieSessionLocal.dispose();
        }
    }

    protected synchronized KieBase getKieBase(final String... drls) {
        final KieHelper kieHelper = new KieHelper();
        for (final String drl : drls) {
            kieHelper.addContent(drl, ResourceType.DRL);
        }

        KieBase kieBase;
        if (enforcedJitting) {
            kieBase = kieHelper.build(ConstraintJittingThresholdOption.get(0));
        } else {
            kieBase = kieHelper.build();
        }
        if (serializeKieBase) {
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
}
