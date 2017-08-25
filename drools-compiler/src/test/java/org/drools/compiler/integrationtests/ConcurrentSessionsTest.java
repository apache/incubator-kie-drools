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

package org.drools.compiler.integrationtests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

@RunWith(Parameterized.class)
public class ConcurrentSessionsTest {

    private final boolean enforcedJitting;

    @Parameterized.Parameters(name = "Enforced jitting={0}")
    public static List<Boolean> getTestParameters() {
        return Arrays.asList(false, true);
    }

    public ConcurrentSessionsTest(final boolean enforcedJitting) {
        this.enforcedJitting = enforcedJitting;
    }

    interface KieSessionExecutor {
        boolean execute(KieSession kieSession, int counter);
    }

    private void parallelTest(final int repetitions, final int threadCount, final KieSessionExecutor kieSessionExecutor,
            final String... drls) throws InterruptedException {
        for (int rep = 0; rep < repetitions; rep++) {
            final KieHelper kieHelper = new KieHelper();
            for (final String drl : drls) {
                kieHelper.addContent( drl, ResourceType.DRL );
            }
            final KieBase kieBase;
            if (enforcedJitting) {
                kieBase = kieHelper.build(ConstraintJittingThresholdOption.get(2));
            } else {
                kieBase = kieHelper.build();
            }

//            ReteDumper.dumpRete(kieBase.newKieSession());

            final ExecutorService executor = Executors.newFixedThreadPool( threadCount, new ThreadFactory() {
                public Thread newThread( final Runnable r ) {
                    final Thread t = new Thread( r );
                    t.setDaemon( true );
                    return new Thread(r);
                }
            } );

            try {
                final Callable<Boolean>[] tasks = new Callable[threadCount];

                for (int i = 0; i < threadCount; i++) {
                    final int counter = i;
                    tasks[i] = new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return kieSessionExecutor.execute(kieBase.newKieSession(), counter);
                        }
                    };
                }

                final CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
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

                assertEquals(threadCount, successCounter);
            } finally {
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
        }
    }

    @Test(timeout = 5000)
    public void test1() throws InterruptedException {
        final String drl = "rule R when String() then end";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( final KieSession kieSession, final int counter ) {
                kieSession.insert( "test" );
                return kieSession.fireAllRules() == 1;
            }
        }, drl );
    }

    @Test(timeout = 5000)
    public void test2NoSubnetwork() throws InterruptedException {
        test2(getRule("R1", "this == \"odd\"", false, false, "Number( intValue > 0 )"),
                getRule("R2", "this == \"pair\"", false, false, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 5000)
    public void test2WithSubnetwork() throws InterruptedException {
        test2(getRule("R1", "this == \"odd\"", false, true, "Number( intValue > 0 )"),
                getRule("R2", "this == \"pair\"", false, true, "Number( intValue < 10000 )"));
    }

    private void test2(final String... drls) throws InterruptedException {
        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( final KieSession kieSession, final int counter ) {
                final Product[] products = new Product[10];
                for (int i = 0; i < 10; i++) {
                    products[i] = new Product( "" + i, i % 2 == 0 ? "pair" : "odd" );
                }

                kieSession.insert( "odd" );
                kieSession.insert( "pair" );
                for (int i = 0; i < 10; i++) {
                    kieSession.insert( products[i] );
                }

                kieSession.fireAllRules();

                for (int i = 0; i < 10; i++) {
                    if ( !products[i].getCategory().equals( products[i].getDescription() ) ) {
                        return false;
                    }
                }
                return true;
            }
        }, drls );
    }

    @Test(timeout = 5000)
    public void test3NoSubnetwork() throws InterruptedException {
        test3(getRule("R1", "this == \"odd\"", false, false, "Number( intValue > 0 )"),
                getRule("R2", "this == \"pair\"", false, false, "Number( intValue < 10000 )"));
    }

    @Test(timeout = 5000)
    public void test3WithSubnetwork() throws InterruptedException {
        test3(getRule("R1", "this == \"odd\"", false, true, "Number( intValue > 0 )"),
                getRule("R2", "this == \"pair\"", false, true, "Number( intValue < 10000 )"));
    }

    @Test
    public void test3WithSharedSubnetwork() throws InterruptedException {
        final String ruleTemplate = "import " + Product.class.getCanonicalName() + ";\n" +
                "rule ${ruleName} when\n" +
                "  $s : String()\n" +
                "  $p : Product( category == $s )\n" +
                "  $n : Number(intValue > 0) from accumulate (\n" +
                "    $s_1 : String( this == $s ) and\n" +
                "    $p_1 : Product( category == $s_1 )\n" +
                "    ;count($p_1))\n" +
                "  Product(this == $p, category == \"${category}\", firings not contains \"${ruleName}\")\n" +
                "then\n" +
                "  $p.getFirings().add(\"${ruleName}\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);\n" +
                "end\n";

        final String drl1 = ruleTemplate.replace("${ruleName}", "R1").replace("${category}", "odd");
        final String drl2 = ruleTemplate.replace("${ruleName}", "R2").replace("${category}", "pair");
        test3(drl1, drl2);
    }

    private void test3(final String... drls) throws InterruptedException {
        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( final KieSession kieSession, final int counter ) {
                final Product[] products = new Product[10];
                for (int i = 0; i < 10; i++) {
                    products[i] = new Product( "" + i, i % 2 == 0 ? "pair" : "odd" );
                }

                for (int i = 0; i < 10; i++) {
                    kieSession.insert( products[i] );
                }

                kieSession.fireAllRules();

                final boolean pair = counter % 2 == 0;
                kieSession.insert( pair ? "pair" : "odd" );

                kieSession.fireAllRules();

                for (int i = 0; i < 10; i++) {
                    if ( pair ) {
                        if ( products[i].getCategory().equals( "pair" ) && !products[i].getDescription().equals( "pair" ) ) {
                            return false;
                        }
                        if ( products[i].getCategory().equals( "odd" ) && !products[i].getDescription().equals( "" ) ) {
                            return false;
                        }
                    }
                    if ( !pair ) {
                        if ( products[i].getCategory().equals( "pair" ) && !products[i].getDescription().equals( "" ) ) {
                            return false;
                        }
                        if ( products[i].getCategory().equals( "odd" ) && !products[i].getDescription().equals( "odd" ) ) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }, drls );
    }

    @Test(timeout = 10000)
    public void test4NoSharing() throws InterruptedException {
        test4(getRule("R1", "", false, true, "Number( intValue > 5 )"),
                getRule("R2", "", false, true, "Number( intValue < 5 )"));
    }

    @Test(timeout = 10000)
    public void test4WithSharing() throws InterruptedException {
        test4(getRule("R1", "", true, true, "Number( intValue > 5 )"),
                getRule("R2", "", true, true, "Number( intValue < 5 )"));
    }

    private void test4(final String... drls) throws InterruptedException {
        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( final KieSession kieSession, final int counter ) {
                final Product[] products = new Product[10];
                for (int i = 0; i < 10; i++) {
                    products[i] = new Product( "" + i, i % 3 == 0 ? "few" : "many" );
                }

                kieSession.insert( "few" );
                kieSession.insert( "many" );

                for (int i = 0; i < 10; i++) {
                    kieSession.insert( products[i] );
                }

                kieSession.fireAllRules();

                for (int i = 0; i < 10; i++) {
                    if ( !products[i].getCategory().equals( products[i].getDescription() ) ) {
                        return false;
                    }
                }
                return true;
            }
        }, drls );
    }

    private String getRule(final String ruleName, final String stringFactCondition,
            final boolean withSharing, final boolean withSubnetwork, final String accumulateResultFilter) {
        final String sharingCondition = withSharing ? "" : ", firings not contains \"" + ruleName + "\"";
        final String accumulateWithSubnetwork = withSubnetwork ?
                "  $n : " + accumulateResultFilter + " from accumulate (\n" +
                        "    $s_1 : String( this == $s ) and\n" +
                        "    $p_1 : Product( category == $s_1 )\n" +
                        "    ;count($p_1))\n"
                : "";

        return "import " + Product.class.getCanonicalName() + ";\n" +
                "rule " + ruleName + " no-loop when\n" +
                "  $s : String(" + stringFactCondition + ")\n" +
                "  $p : Product( category == $s " + sharingCondition + ")\n" +
                accumulateWithSubnetwork +
                "then\n" +
                "  $p.getFirings().add(\"" + ruleName +"\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);\n" +
                "end\n";
    }

    public static class Product {
        private final String id;
        private final String category;

        private List<String> firings = new ArrayList<String>();

        private String description = "";

        public Product( final String id, final String category ) {
            this.id = id;
            this.category = category;
        }

        public String getId() {
            return id;
        }

        public List<String> getFirings() {
            return firings;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public void appendDescription( final String append ) {
            description += append;
        }
    }
}