package org.drools.integrationtests;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.*;

import static junit.framework.Assert.assertEquals;

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
