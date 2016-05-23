/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.regression;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Test to verify BRMS-532 (Drools Session insert
 * ConcurrentModificationException in Multithreading Environment) is fixed
 */
public class SessionInsertMultiThreadingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionInsertMultiThreadingTest.class);

    private static final int THREADS = 50;
    private static final int RUNS_PER_THREAD = 100;

    private static KieBase kbase;
    private static ExecutorService executor;

    @BeforeClass
    public static void createKbase() throws Exception {
        final Resource resource = KieServices.Factory.get().getResources().newClassPathResource(
                "sessionInsertMultithreadingTest.drl",
                SessionInsertMultiThreadingTest.class);

        kbase = KieBaseUtil.getKieBaseFromResources(true, resource);
    }

    @Before
    public void createExecutor() {
        executor = Executors.newFixedThreadPool(THREADS);
    }

    @After
    public void shutdownExecutor() throws Exception {
        if (kbase != null) {
            for (KieSession ksession : kbase.getKieSessions()) {
                ksession.dispose();
            }
        }

        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            LOGGER.warn("Executor not shut down in 30s!");
            executor.shutdownNow();
        }
        executor = null;
    }

    @Test
    public void testCommonBase() throws Exception {
        final List<Future<?>> futures = new ArrayList<Future<?>>();

        for (int i = 0; i < RUNS_PER_THREAD; i++) {
            for (int j = 0; j < THREADS; j++) {
                futures.add(executor.submit(new KieBaseRunnable(kbase)));
            }
        }

        waitForCompletion(futures);
    }

    @Test
    public void testCommonSession() throws Exception {
        for (int i = 0; i < RUNS_PER_THREAD; i++) {
            testSingleCommonSession();
        }
    }

    private void testSingleCommonSession() throws Exception {
        final List<Future<?>> futures = new ArrayList<Future<?>>();
        final KieSession ksession = kbase.newKieSession();

        try {
            runTestBySeveralThreads(ksession, futures);
            waitForCompletion(futures);
        } finally {
            if (ksession != null) {
                ksession.dispose();
            }
        }
    }

    /**
     * Reproducer for BZ 1187070.
     */
    @Test
    public void testCommonStatelessSessionBZ1187070() throws Exception {
        for (int i = 0; i < RUNS_PER_THREAD; i++) {
            testSingleCommonStatelessSession();
        }
    }

    private void testSingleCommonStatelessSession() throws Exception {
        final List<Future<?>> futures = new ArrayList<Future<?>>();
        final StatelessKieSession statelessKieSession = kbase.newStatelessKieSession();

        runTestBySeveralThreads(statelessKieSession, futures);
        waitForCompletion(futures);
    }

    private void runTestBySeveralThreads(final KieSession ksession, final List<Future<?>> futures) throws Exception {
        for (int j = 0; j < THREADS; j++) {
            futures.add(executor.submit(new KieSessionRunnable(ksession)));
        }
    }

    private void runTestBySeveralThreads(final StatelessKieSession statelessKieSession, final List<Future<?>> futures)
            throws Exception {
        for (int j = 0; j < THREADS; j++) {
            futures.add(executor.submit(new StatelessKieSessionRunnable(statelessKieSession)));
        }
    }

    private void waitForCompletion(final List<Future<?>> futures) throws Exception {
        Exception lastException = null;
        for (Future<?> future : futures) {
            try {
                future.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                lastException = e;
            }
        }
        if (lastException != null) {
            throw lastException;
        }
    }

    /**
     * The Runnable performing the test on a given shared StatelessKieSession.
     */
    public static class StatelessKieSessionRunnable implements Runnable {

        protected final StatelessKieSession statelessKieSession;

        public StatelessKieSessionRunnable(StatelessKieSession statelessKieSession) {
            this.statelessKieSession = statelessKieSession;
        }

        @Override
        public void run() {
            final Message m = new Message();
            final Person p = new Person();
            final KieCommands kieCommands = KieServices.Factory.get().getCommands();

            final List<Command<?>> commandList = new ArrayList<Command<?>>();
            commandList.add(kieCommands.newInsert(m));
            commandList.add(kieCommands.newInsert(p));

            commandList.add(kieCommands.newFireAllRules());

            statelessKieSession.execute(kieCommands.newBatchExecution(commandList));

            Assertions.assertThat(p.getName()).isNotNull();
            Assertions.assertThat(m.getMessage()).isNotNull();
        }
    }

    /**
     * The Runnable performing the test on a given shared KieSession.
     */
    public static class KieSessionRunnable implements Runnable {

        protected final KieSession ksession;

        public KieSessionRunnable(KieSession ksession) {
            this.ksession = ksession;
        }

        @Override
        public void run() {
            final Message m = new Message();
            final Person p = new Person();

            ksession.insert(m);
            ksession.insert(p);

            ksession.fireAllRules();

            Assertions.assertThat(p.getName()).isNotNull();
            Assertions.assertThat(m.getMessage()).isNotNull();
        }
    }

    /**
     * The Runnable performing the test on a given shared KieBase.
     */
    public static class KieBaseRunnable extends KieSessionRunnable {

        public KieBaseRunnable(KieBase kbase) {
            super(kbase.newKieSession());
        }

        @Override
        public void run() {
            try {
                super.run();
            } finally {
                ksession.dispose();
            }
        }
    }

}
