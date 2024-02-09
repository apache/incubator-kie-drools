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
package org.drools.compiler.integrationtests.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.mvel.expr.MvelEvaluator;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseConcurrencyTest {

    protected static int LOOP = 500;
    protected static int THREADS = 32;
    protected static int REQUESTS = 32;
    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BaseConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test(timeout = 300000)
    public void testConcurrency() {
        final String drl = getDrl();

        List<Exception> exceptions = new ArrayList<>();

        // Basically, what we want to test is "synced_till_eval"

//        MvelEvaluator.setEvaluatorType( MvelEvaluator.EvaluatorType.THREAD_UNSAFE ); // fails with all tests with non-exec-model
//        MvelEvaluator.setEvaluatorType( MvelEvaluator.EvaluatorType.THREAD_SAFE_ON_FIRST_EVAL ); // fails with ConsequenceWithAndOrConcurrencyTest, ConstraintWithAndOrConcurrencyTest, ConstraintWithAndOrJittingConcurrencyTest with non-exec-model
        MvelEvaluator.setEvaluatorType( MvelEvaluator.EvaluatorType.SYNCHRONIZED_TILL_EVALUATED ); // passes all tests
//        MvelEvaluator.setEvaluatorType( MvelEvaluator.EvaluatorType.FULLY_SYNCHRONIZED ); // passes all tests

        try {
            KieBase kieBase = null;
            if (kieBaseTestConfiguration.isExecutableModel()) { // exec-model doesn't have mvel optimization so we can create the KieBase once
                kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
            }
            for (int i = 0; i < LOOP; i++) {
                if (!kieBaseTestConfiguration.isExecutableModel()) { // to reset MVELConstraint Jitting we need to create a new KieBase each time
                    kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
                }

                preprocess(kieBase);

                ExecutorService executor = Executors.newFixedThreadPool(THREADS);
                CountDownLatch latch = new CountDownLatch(THREADS);
                for (int j = 0; j < REQUESTS; j++) {
                    KieBase finalKieBase = kieBase;
                    executor.execute(() -> {
                        KieSession kSession = finalKieBase.newKieSession();

                        setGlobal(kSession);
                        insertFacts(kSession);

                        try {
                            latch.countDown();
                            latch.await();
                        } catch (InterruptedException e) {
                            // ignore
                        }

                        try {
                            kSession.fireAllRules();
                        } catch (Exception e) {
                            exceptions.add(e);
                        } finally {
                            kSession.dispose();
                        }
                    });
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(300, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            if (!exceptions.isEmpty()) {
                exceptions.get(0).printStackTrace();
            }

            assertThat(exceptions.size()).isEqualTo(0);

        } finally {
            MvelEvaluator.resetEvaluatorType();
        }
    }

    protected void preprocess(KieBase kieBase) {
        // by default, no preprocess
    }

    protected void setGlobal(KieSession kSession) {
        // by default, no global
    }

    protected abstract String getDrl();

    // This is a typical insertion but sub class may override
    protected void insertFacts(KieSession kSession) {
        Bus bus1 = new Bus("red", 30);
        bus1.getKaraoke().getDvd().put("POWER PLANT", new Album("POWER PLANT", "GAMMA RAY"));
        bus1.getKaraoke().getDvd().put("Somewhere Out In Space", new Album("Somewhere Out In Space", "GAMMA RAY"));
        kSession.insert(bus1);
    }
}
