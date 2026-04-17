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
package org.drools.model.codegen.execmodel;

import java.lang.reflect.Field;
import java.util.PriorityQueue;

import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.time.impl.TimerJobInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class PseudoClockSchedulerRetentionTest extends BaseModelTest {

    private static final String CANCELLED_JOB_PURGE_THRESHOLD_PROPERTY = "drools.pseudoclock.cancelledJobPurgeThreshold";
    private static final int CANCELLED_JOB_PURGE_THRESHOLD = 1000;

    @ParameterizedTest
    @MethodSource("parameters")
    public void testCanceledExpirationJobsAreRetainedUntilPurgeThreshold(RUN_TYPE runType) throws Exception {
        String drl =
                "package org.test;\n" +
                "import " + TestEvent.class.getCanonicalName() + ";\n" +
                "rule DeleteMatchedEvent when\n" +
                "  $e : TestEvent()\n" +
                "then\n" +
                "  delete($e);\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, CepTest.getCepKieModuleModel(), drl);
        try {
            PseudoClockScheduler scheduler = (PseudoClockScheduler) ksession.getSessionClock();

            for (int i = 0; i < CANCELLED_JOB_PURGE_THRESHOLD; i++) {
                ksession.insert(new TestEvent(i));
                assertThat(ksession.fireAllRules()).isEqualTo(1);
            }

            assertThat(getQueueSize(scheduler)).isEqualTo(CANCELLED_JOB_PURGE_THRESHOLD);
            assertThat(getCanceledJobCount(scheduler)).isEqualTo(CANCELLED_JOB_PURGE_THRESHOLD);

            ksession.insert(new TestEvent(CANCELLED_JOB_PURGE_THRESHOLD));
            assertThat(ksession.fireAllRules()).isEqualTo(1);

            assertThat(getQueueSize(scheduler)).isZero();
            assertThat(getCanceledJobCount(scheduler)).isZero();
        } finally {
            ksession.dispose();
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testCanceledExpirationJobsUseConfiguredPurgeThreshold(RUN_TYPE runType) throws Exception {
        withCancelledJobPurgeThreshold("3", () -> {
            String drl =
                    "package org.test;\n" +
                    "import " + TestEvent.class.getCanonicalName() + ";\n" +
                    "rule DeleteMatchedEvent when\n" +
                    "  $e : TestEvent()\n" +
                    "then\n" +
                    "  delete($e);\n" +
                    "end\n";

            KieSession ksession = getKieSession(runType, CepTest.getCepKieModuleModel(), drl);
            try {
                PseudoClockScheduler scheduler = (PseudoClockScheduler) ksession.getSessionClock();

                for (int i = 0; i < 3; i++) {
                    ksession.insert(new TestEvent(i));
                    assertThat(ksession.fireAllRules()).isEqualTo(1);
                }

                assertThat(getQueueSize(scheduler)).isEqualTo(3);
                assertThat(getCanceledJobCount(scheduler)).isEqualTo(3);

                ksession.insert(new TestEvent(3));
                assertThat(ksession.fireAllRules()).isEqualTo(1);

                assertThat(getQueueSize(scheduler)).isZero();
                assertThat(getCanceledJobCount(scheduler)).isZero();
            } finally {
                ksession.dispose();
            }
        });
    }

    private static int getQueueSize(PseudoClockScheduler scheduler) throws ReflectiveOperationException {
        return getQueue(scheduler).size();
    }

    private static int getCanceledJobCount(PseudoClockScheduler scheduler) throws ReflectiveOperationException {
        int canceledJobs = 0;
        for (TimerJobInstance timerJobInstance : getQueue(scheduler)) {
            if (timerJobInstance.isCanceled()) {
                canceledJobs++;
            }
        }
        return canceledJobs;
    }

    @SuppressWarnings("unchecked")
    private static PriorityQueue<TimerJobInstance> getQueue(PseudoClockScheduler scheduler) throws ReflectiveOperationException {
        Field queueField = PseudoClockScheduler.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        return (PriorityQueue<TimerJobInstance>) queueField.get(scheduler);
    }

    private static void withCancelledJobPurgeThreshold(String threshold, ThrowingRunnable action) throws Exception {
        String previousValue = System.getProperty(CANCELLED_JOB_PURGE_THRESHOLD_PROPERTY);
        try {
            System.setProperty(CANCELLED_JOB_PURGE_THRESHOLD_PROPERTY, threshold);
            action.run();
        } finally {
            if (previousValue == null) {
                System.clearProperty(CANCELLED_JOB_PURGE_THRESHOLD_PROPERTY);
            } else {
                System.setProperty(CANCELLED_JOB_PURGE_THRESHOLD_PROPERTY, previousValue);
            }
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @Role(Role.Type.EVENT)
    @Expires("2h")
    public static class TestEvent {

        private final int id;

        public TestEvent(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
