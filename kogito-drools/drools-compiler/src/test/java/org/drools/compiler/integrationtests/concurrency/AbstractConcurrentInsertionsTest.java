package org.drools.compiler.integrationtests.concurrency;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.*;

abstract class AbstractConcurrentInsertionsTest {

    private Callable<Boolean> getTask(final int objectCount, final KieBase kieBase, final boolean updateFacts) {
        return getTask(objectCount, kieBase.newKieSession(), true, updateFacts);
    }

    protected Callable<Boolean> getTask(final int objectCount, final KieSession ksession, final boolean disposeSession,
                                        final boolean updateFacts) {
        return () -> {
            try {
                for (int j = 0; j < 10; j++) {
                    final FactHandle[] facts = new FactHandle[objectCount];
                    final FactHandle[] stringFacts = new FactHandle[objectCount];
                    for (int i = 0; i < objectCount; i++) {
                        facts[i] = ksession.insert(new AtomicInteger(i));
                        stringFacts[i] = ksession.insert("test_" + i);
                    }
                    if (updateFacts) {
                        for (int i = 0; i < objectCount; i++) {
                            ksession.update(facts[i], new AtomicInteger(-i));
                            ksession.update(stringFacts[i], "updated_test_" + i);
                        }
                    }
                    for (int i = 0; i < objectCount; i++) {
                        ksession.delete(facts[i]);
                        ksession.delete(stringFacts[i]);
                    }
                    ksession.fireAllRules();
                }
                return true;
            } catch (final Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (disposeSession) {
                    ksession.dispose();
                }
            }
        };
    }

    protected void testConcurrentInsertions(final String drl, final int objectCount, final int threadCount,
                                            final boolean newSessionForEachThread,
                                            final boolean updateFacts) throws InterruptedException {

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();

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

            org.junit.jupiter.api.Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
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
}
