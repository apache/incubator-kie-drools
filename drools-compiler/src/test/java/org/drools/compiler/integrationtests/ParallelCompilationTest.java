/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler.integrationtests;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class ParallelCompilationTest {
    private static final int PARALLEL_THREADS = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_THREADS);

    private static final String DRL_FILE = "parallel_compilation.drl";
    private List<User> users;

    @Test(timeout=10000)
    public void testConcurrentRuleAdditions() throws Exception {
        parallelExecute(BuildExecutor.getSolvers());
    }

    private void parallelExecute(Collection<Callable<KnowledgeBase>> solvers) throws Exception {
        CompletionService<KnowledgeBase> ecs = new ExecutorCompletionService<KnowledgeBase>(executor);
        for (Callable<KnowledgeBase> s : solvers) {
            ecs.submit(s);
        }
        for (int i = 0; i < PARALLEL_THREADS; ++i) {
            KnowledgeBase kbase = ecs.take().get();
        }
    }

    public static class BuildExecutor implements Callable<KnowledgeBase> {

        public KnowledgeBase call() throws Exception {
            final Reader source = new InputStreamReader(ParallelCompilationTest.class.getResourceAsStream(DRL_FILE));

            final Properties props = new Properties();
            props.setProperty("drools.dialect.java.compiler", "JANINO");
            props.setProperty("drools.dialect.java.compiler.lnglevel", "1.6");
            KnowledgeBase result;

            final KnowledgeBuilderConfiguration configuration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(props, ParallelCompilationTest.class.getClass().getClassLoader());
            final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(configuration);

            Thread.sleep(Math.round(Math.random()*250));

            Resource newReaderResource = ResourceFactory.newReaderResource(source);
            //synchronized (RuleUtil.class)
            {
                builder.add(newReaderResource, ResourceType.DRL);
            }
            result = builder.newKnowledgeBase();

            return result;
        }

        public static Collection<Callable<KnowledgeBase>> getSolvers() {
            Collection<Callable<KnowledgeBase>> solvers = new ArrayList<Callable<KnowledgeBase>>();
            for (int i = 0; i < PARALLEL_THREADS; ++i) {
                solvers.add(new BuildExecutor());
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
