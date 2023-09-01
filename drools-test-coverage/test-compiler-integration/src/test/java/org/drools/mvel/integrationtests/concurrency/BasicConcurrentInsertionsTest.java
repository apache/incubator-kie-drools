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

import java.util.Collection;
import java.util.concurrent.Callable;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

@RunWith(Parameterized.class)
public class BasicConcurrentInsertionsTest extends AbstractConcurrentInsertionsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BasicConcurrentInsertionsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout = 20000)
    public void testConcurrentInsertionsFewObjectsManyThreads() throws InterruptedException {
        final String drl = "import " + Bean.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a : Bean( seed != 1 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 1, 1000, false, false, kieBaseTestConfiguration);
    }

    @Test(timeout = 20000)
    public void testConcurrentInsertionsManyObjectsFewThreads() throws InterruptedException {
        final String drl = "import " + Bean.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a : Bean( seed != 1 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 1000, 4, false, false, kieBaseTestConfiguration);
    }

    @Test(timeout = 20000)
    public void testConcurrentInsertionsNewSessionEachThreadUpdateFacts() throws InterruptedException {
        // This tests also ObjectTypeNode concurrency
        final String drl = "import " + Bean.class.getCanonicalName() + ";\n" +
                " query existsBeanSeed5More() \n" +
                "     Bean( seed > 5 ) \n" +
                " end \n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    existsBeanSeed5More() \n" +
                "then\n" +
                "end \n" +
                "rule \"R2\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "then\n" +
                "end\n";
        testConcurrentInsertions(drl, 10, 1000, true, true, kieBaseTestConfiguration);
    }

    @Test(timeout = 20000)
    public void testConcurrentInsertionsNewSessionEachThread() throws InterruptedException {
        final String drl = "import " + Bean.class.getCanonicalName() + ";\n" +
                " query existsBeanSeed5More() \n" +
                "     Bean( seed > 5 ) \n" +
                " end \n" +
                "\n" +
                "rule \"R\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    $b: Bean( seed != 2 )\n" +
                "    existsBeanSeed5More() \n" +
                "then\n" +
                "end \n" +
                "rule \"R2\"\n" +
                "when\n" +
                "    $a: Bean( seed != 1 )\n" +
                "    $b: Bean( seed != 2 )\n" +
                "then\n" +
                "end\n" +
                "rule \"R3\"\n" +
                "when\n" +
                "    $a: Bean( seed != 3 )\n" +
                "    $b: Bean( seed != 4 )\n" +
                "    $c: Bean( seed != 5 )\n" +
                "    $d: Bean( seed != 6 )\n" +
                "    $e: Bean( seed != 7 )\n" +
                "then\n" +
                "end";
        testConcurrentInsertions(drl, 10, 1000, true, false, kieBaseTestConfiguration);
    }

    protected Callable<Boolean> getTask(
            final int objectCount,
            final KieSession ksession,
            final boolean disposeSession,
            final boolean updateFacts) {
        return () -> {
            try {
                for (int j = 0; j < 10; j++) {
                    FactHandle[] facts = new FactHandle[objectCount];
                    for (int i = 0; i < objectCount; i++) {
                        facts[i] = ksession.insert(new Bean(i));
                    }
                    if (updateFacts) {
                        for (int i = 0; i < objectCount; i++) {
                            ksession.update(facts[i], new Bean(-i));
                        }
                    }
                    for (FactHandle fact : facts) {
                        ksession.delete(fact);
                    }
                    ksession.fireAllRules();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (disposeSession) {
                    ksession.dispose();
                }
            }
        };
    }

    public static class Bean {

        private final int seed;
        private final String threadName;

        public Bean(int seed) {
            this.seed = seed;
            threadName = Thread.currentThread().getName();
        }

        public int getSeed() {
            return seed;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Bean)) return false;
            return seed == ((Bean)other).seed && threadName.equals( ((Bean)other).threadName );
        }

        @Override
        public int hashCode() {
            return 29 * seed + 31 * threadName.hashCode();
        }

        @Override
        public String toString() {
            return "Bean #" + seed + " created by " + threadName;
        }
    }

}
