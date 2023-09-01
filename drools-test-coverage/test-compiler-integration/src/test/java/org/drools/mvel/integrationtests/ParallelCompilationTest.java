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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;

@RunWith(Parameterized.class)
public class ParallelCompilationTest {

    private static final int PARALLEL_THREADS = 5;
    private static final String DRL_FILE = "parallel_compilation.drl";

    private ExecutorService executor;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ParallelCompilationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Before
    public void setUp() throws Exception {
        executor = Executors.newFixedThreadPool(PARALLEL_THREADS);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdownNow();
    }

    @Test(timeout=10000)
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(BuildExecutor.getSolvers(kieBaseTestConfiguration));
    }

    private void parallelExecute(Collection<Callable<KieBase>> solvers) throws Exception {
        CompletionService<KieBase> ecs = new ExecutorCompletionService<KieBase>(executor);
        for (Callable<KieBase> s : solvers) {
            ecs.submit(s);
        }
        for (int i = 0; i < PARALLEL_THREADS; ++i) {
            KieBase kbase = ecs.take().get();
        }
    }

    public static class BuildExecutor implements Callable<KieBase> {

        private KieBaseTestConfiguration myKieBaseTestConfiguration;

        public BuildExecutor(KieBaseTestConfiguration kieBaseTestConfiguration) {
            this.myKieBaseTestConfiguration = kieBaseTestConfiguration;
        }

        public KieBase call() throws Exception {
            Thread.sleep(Math.round(Math.random()*250));

            Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
            kieModuleConfigurationProperties.put("drools.dialect.java.compiler.lnglevel", "1.6");
            KieBase result = KieBaseUtil.getKieBaseFromClasspathResourcesWithClassLoaderForKieBuilder("test", getClass(), ParallelCompilationTest.class.getClass().getClassLoader(), myKieBaseTestConfiguration, kieModuleConfigurationProperties, DRL_FILE);

            return result;
        }

        public static Collection<Callable<KieBase>> getSolvers(KieBaseTestConfiguration kieBaseTestConfiguration) {
            Collection<Callable<KieBase>> solvers = new ArrayList<Callable<KieBase>>();
            for (int i = 0; i < PARALLEL_THREADS; ++i) {
                solvers.add(new BuildExecutor(kieBaseTestConfiguration));
            }
            return solvers;
        }
    }

    public static class User {
        private int age;
        private boolean risky;
        private Gender gender;
        private String name;

        public enum Gender { MALE, FEMALE, OTHER}

        public User(int age, Gender gender, String name) {
            this.age = age;
            this.gender = gender;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Gender getGender() {
            return gender;
        }

        public void setGender(Gender gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isRisky() {
            return risky;
        }

        public void setRisky(boolean risky) {
            this.risky = risky;
        }
    }
}
