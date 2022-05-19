/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.concurrency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Result;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.test.testcategory.TurtleTestCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class GlobalConcurrencyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalConcurrencyTest.class);
    
    protected static int LOOP = 3000;
    protected static int MAX_THREAD = 30;
    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GlobalConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN}); // DROOLS-6961 : exec-model only
        return parameters;
    }

    @Test
    public void testGlobalConcurrency() {
        String str =
                "package org.mypkg;" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "global Result globalResult;" +
                     "rule R1 when\n" +
                     "  $p1 : Person(name == \"Mark\")\n" +
                     "then\n" +
                     "  globalResult.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p1 : Person(name == \"Edson\")\n" +
                     "then\n" +
                     "  globalResult.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                     "end";

        List<Exception> exceptionList = new ArrayList<>();

        for (int i = 0; i < LOOP; i++) {
            if (i % 100 == 0) {
                System.out.println("loop : " + i);
            }

            KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("global-test", kieBaseTestConfiguration, str);

            ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD);
            final CountDownLatch latch = new CountDownLatch(MAX_THREAD);
            for (int n = 0; n < MAX_THREAD; n++) {
                executor.execute(new Runnable() {

                    public void run() {

                        KieSession ksession = kieBase.newKieSession();
                        Result result = new Result();
                        ksession.setGlobal("globalResult", result);

                        ksession.insert(new Person("Mark", 37));
                        ksession.insert(new Person("Edson", 35));
                        ksession.insert(new Person("Mario", 40));

                        latch.countDown();
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            LOGGER.error(e.getMessage(), e);
                        }

                        try {
                            ksession.fireAllRules();
                        } catch (Exception e) {
                            exceptionList.add(e);
                        }

                        ksession.dispose();
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }

            if (!exceptionList.isEmpty()) {
                break;
            }
        }

        if (exceptionList.size() > 0) {
            LOGGER.error(exceptionList.get(0).getMessage(), exceptionList.get(0));
        }

        assertThat(exceptionList).isEmpty();

    }
}
