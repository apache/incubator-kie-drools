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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class ConcurrentSessionsTest {

    interface KieSessionExecutor {
        boolean execute(KieSession kieSession, int counter);
    }

    private void parallelTest(int repetitions, int threadCount, final KieSessionExecutor kieSessionExecutor, String... drls) {
        for (int rep = 0; rep < repetitions; rep++) {
            KieHelper kieHelper = new KieHelper();
            for (String drl : drls) {
                kieHelper.addContent( drl, ResourceType.DRL );
            }
            final KieBase kieBase = kieHelper.build();

            ExecutorService executor = Executors.newFixedThreadPool( threadCount, new ThreadFactory() {
                public Thread newThread( Runnable r ) {
                    Thread t = new Thread( r );
                    t.setDaemon( true );
                    return t;
                }
            } );

            try {
                Callable<Boolean>[] tasks = new Callable[threadCount];

                for ( int i = 0; i < threadCount; i++ ) {
                    final int counter = i;
                    tasks[i] = new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return kieSessionExecutor.execute( kieBase.newKieSession(), counter );
                        }
                    };
                }

                CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>( executor );
                for ( Callable<Boolean> task : tasks ) {
                    ecs.submit( task );
                }

                int successCounter = 0;
                for ( int i = 0; i < threadCount; i++ ) {
                    try {
                        if ( ecs.take().get() ) {
                            successCounter++;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException( e );
                    }
                }

                assertEquals( threadCount, successCounter );
            } finally {
                executor.shutdown();
                try {
                    if ( !executor.awaitTermination( 5, TimeUnit.SECONDS ) ) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    @Test
    public void test1() {
        String drl = "rule R when String() then end";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                kieSession.insert( "test" );
                return kieSession.fireAllRules() == 1;
            }
        }, drl );
    }

    @Test
    public void test2() {
        String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "  $s : String( this == \"odd\" )\n" +
                "  $p : Product( category == $s, firings not contains \"R1\" )\n" +
                "then\n" +
                "  $p.getFirings().add(\"R1\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);\n" +
                "end\n";

        String drl2 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R2 when\n" +
                "  $s : String( this == \"pair\" )\n" +
                "  $p : Product( category == $s, firings not contains \"R2\" )\n" +
                "then\n" +
                "  $p.getFirings().add(\"R2\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);" +
                "end\n";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                Product[] products = new Product[10];
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
        }, drl1, drl2 );
    }

    @Test
    public void test3() {
        String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "  $s : String( this == \"odd\" )\n" +
                "  $p : Product( category == $s, firings not contains \"R1\" )\n" +
                "then\n" +
                "  $p.getFirings().add(\"R1\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);\n" +
                "end\n";

        String drl2 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R2 when\n" +
                "  $s : String( this == \"pair\" )\n" +
                "  $p : Product( category == $s, firings not contains \"R2\" )\n" +
                "then\n" +
                "  $p.getFirings().add(\"R2\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);" +
                "end\n";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                Product[] products = new Product[10];
                for (int i = 0; i < 10; i++) {
                    products[i] = new Product( "" + i, i % 2 == 0 ? "pair" : "odd" );
                }

                boolean pair = counter % 2 == 0;
                kieSession.insert( pair ? "pair" : "odd" );

                for (int i = 0; i < 10; i++) {
                    kieSession.insert( products[i] );
                }

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
        }, drl1, drl2 );
    }

    @Test
    public void test4() {
        String drl1 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "  $s : String()\n" +
                "  $p : Product( category == $s, firings not contains \"R1\" )" +
                "  $n : Number( intValue > 5 ) from accumulate (" +
                "    $s_1 : String( this == $s ) and" +
                "    $p_1 : Product( category == $s_1 )" +
                "    ;count($p_1))\n" +
                "then\n" +
                "  $p.getFirings().add(\"R1\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);\n" +
                "end\n";

        String drl2 =
                "import " + Product.class.getCanonicalName() + ";\n" +
                "rule R2 when\n" +
                "  $s : String()\n" +
                "  $p : Product( category == $s, firings not contains \"R2\" )" +
                "  $n : Number( intValue < 5 ) from accumulate (" +
                "    $s_1 : String( this == $s ) and" +
                "    $p_1 : Product( category == $s_1 )" +
                "    ;count($p_1))\n" +
                "then\n" +
                "  $p.getFirings().add(\"R2\");\n" +
                "  $p.appendDescription($s);\n" +
                "  update($p);" +
                "end\n";

        parallelTest( 10, 10, new KieSessionExecutor() {
            @Override
            public boolean execute( KieSession kieSession, int counter ) {
                Product[] products = new Product[10];
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
        }, drl1, drl2 );
    }

    public static class Product {
        private final String id;
        private final String category;

        private List<String> firings = new ArrayList<String>();

        private String description = "";

        public Product( String id, String category ) {
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

        public void appendDescription( String append ) {
            description += append;
        }
    }
}
