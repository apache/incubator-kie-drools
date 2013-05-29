package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.StockTick;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.SessionEntryPoint;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.conf.PhreakOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static java.util.Arrays.asList;

public class PhreakConcurrencyTest extends CommonTestMethodBase {

    private Executor executor;

    private static PhreakOption wasRunningPhreak;

    @BeforeClass
    public static void setPhreak() {
        wasRunningPhreak = preak;
        preak = PhreakOption.ENABLED;
    }

    @AfterClass
    public static void unsetPhreak() {
        preak = wasRunningPhreak;
    }

    @Before
    public void setUp() {
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });
    }

    @Test
    public void testMultipleConcurrentEPs() {
        final boolean PARALLEL = true;
        final int EP_NR = 10;

        StringBuilder sb = new StringBuilder();

        sb.append("import org.drools.compiler.StockTick;\n");
        for (int i = 0; i < EP_NR; i++) {
            sb.append("global java.util.List results").append(i).append(";\n");
        }
        sb.append("declare StockTick\n" +
                  "    @role( event )\n" +
                  "end\n");
        for (int i = 0; i < EP_NR; i++) {
            sb.append("rule \"R" + i +"\"\n" +
                      "when\n" +
                      "    $name : String( this.startsWith(\"A\") )\n" +
                      "    $st : StockTick( company == $name, price > 10 ) from entry-point EP" + i +"\n" +
                      "then\n" +
                      "    results" + i +".add( $st );\n" +
                      "end\n");
        }

        KnowledgeBase kbase = loadKnowledgeBaseFromString(sb.toString());
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        boolean success = true;
        if (PARALLEL) {

            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (int i = 0; i < EP_NR; i++) {
                ecs.submit(new EPManipulator(ksession, i));
            }

            for (int i = 0; i < EP_NR; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        } else {

            for (int i = 0; i < EP_NR; i++) {
                try {
                    success = new EPManipulator(ksession, i).call() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

        }

        assertTrue(success);

        assertEquals(EP_NR, ksession.fireAllRules());

        for (int i = 0; i < EP_NR; i++) {
            assertEquals(1, ((List) ksession.getGlobal("results" + i)).size());
        }

        ksession.dispose();
    }

    public static class EPManipulator implements Callable<Boolean> {

        private final KieSession ksession;
        private final int index;

        public EPManipulator(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            List results = new ArrayList();
            ksession.setGlobal("results" + index, results);

            ksession.insert("ACME" + index);

            SessionEntryPoint ep = ksession.getEntryPoint("EP" + index);

            for (int i = 0; i < 12; i++) {
                ep.insert(new StockTick(1, "ACME" + index, i - 50));
                ep.insert(new StockTick(2, "DROO" + index, i));
                ep.insert(new StockTick(3, "ACME" + index, i));
            }

            return true;
        }
    }

    @Test(timeout = 10000)
    public void testMultipleConcurrentEPs2() {
        String str = "global java.util.List results\n" +
                     "\n" +
                     "rule \"R0\" when\n" +
                     "    $s : String( ) from entry-point EP0\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP1\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP2\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"R1\" when\n" +
                     "    $s : String( ) from entry-point EP1\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP2\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP0\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n" +
                     "\n" +
                     "rule \"R2\" when\n" +
                     "    $s : String( ) from entry-point EP2\n" +
                     "    $i : Integer( toString().equals($s) ) from entry-point EP0\n" +
                     "    $l : Long( intValue() == $i ) from entry-point EP1\n" +
                     "then\n" +
                     "    results.add( $s );\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        boolean success = true;
        CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
        for (int i = 0; i < 3; i++) {
            ecs.submit(new EPManipulator2(ksession, i));
        }

        for (int i = 0; i < 3; i++) {
            try {
                success = ecs.take().get() && success;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        assertTrue(success);

        ksession.fireAllRules();
        System.out.println(results);
        assertEquals(3, results.size());
        for (String s : results) {
            assertEquals("2", s);
        }
    }

    public static class EPManipulator2 implements Callable<Boolean> {

        private final KieSession ksession;
        private final int index;

        public EPManipulator2(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            SessionEntryPoint ep = ksession.getEntryPoint("EP" + index);

            FactHandle[] fhs = new FactHandle[15];

            for (int j = 0; j < 3; j++) {
                for (int i = 0; i < 5; i++) {
                    fhs[i * 3] = ep.insert("" + i);
                    fhs[i * 3 + 1] = ep.insert(new Long(i));
                    fhs[i * 3 + 2] = ep.insert(new Integer(i));
                }

                for (int i = 0; i < 5; i++) {
                    if (i == index+j) continue;
                    ep.delete(fhs[i * 3]);
                    ep.delete(fhs[i * 3 + 1]);
                    ep.delete(fhs[i * 3 + 2]);
                }
            }

            return true;
        }
    }

    @Test
    public void testMultipleConcurrentEPs3() {
        String str = "global java.util.List results\n" +
                     "rule R1 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R1\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R2 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   eval(true)\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R2\");\n" +
                     "end\n" +
                     "\n" +
                     "rule R3 when\n" +
                     "   String( this == \"1\") from entry-point EP1\n" +
                     "   String( this == \"2\") from entry-point EP2\n" +
                     "   String( this == \"3\") from entry-point EP3\n" +
                     "   eval(true)\n" +
                     "   String( this == \"4\") from entry-point EP4\n" +
                     "   String( this == \"5\") from entry-point EP5\n" +
                     "   String( this == \"6\") from entry-point EP6\n" +
                     "   eval(true)\n" +
                     "   String( this == \"7\") from entry-point EP7\n" +
                     "   String( this == \"8\") from entry-point EP8\n" +
                     "   String( this == \"9\") from entry-point EP9\n" +
                     "then\n" +
                     "   results.add(\"R3\");\n" +
                     "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KieSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        EPManipulator3[] epManipulators = new EPManipulator3[9];
        for (int i = 0; i < 9; i++) {
            epManipulators[i] = new EPManipulator3(ksession, i+1);
        }

        for (int deleteIndex = 0; deleteIndex < 11; deleteIndex++) {
            boolean success = true;
            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);
            for (int i = 0; i < 9; i++) {
                ecs.submit(epManipulators[i].setDeleteIndex(deleteIndex % 10));
            }

            for (int i = 1; i < 10; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            assertTrue(success);

            ksession.fireAllRules();

            if (deleteIndex % 10 == 0) {
                assertEquals(3, results.size());
                assertTrue(results.containsAll(asList("R1", "R2", "R3")));
            } else {
                if (!results.isEmpty()) {
                    fail("Results should be empty with deleteIndex = " + deleteIndex + "; got " + results.size() + " items");
                }
            }

            results.clear();
        }
    }

    public static class EPManipulator3 implements Callable<Boolean> {

        private static final Random RANDOM = new Random(0);

        private final KieSession ksession;
        private final int index;

        private int deleteIndex;

        private FactHandle fh = null;

        public EPManipulator3(KieSession ksession, int index) {
            this.ksession = ksession;
            this.index = index;
        }

        public Boolean call() throws Exception {
            SessionEntryPoint ep = ksession.getEntryPoint("EP" + index);

            for (int i = 0; i < 100; i++) {
                Thread.sleep(RANDOM.nextInt(100));
                if (fh == null) {
                    fh = ep.insert("" + index);
                } else {
                    if ( RANDOM.nextInt(100) < 70 ) {
                        ep.delete(fh);
                        fh = null;
                    } else {
                        ep.update(fh, "" + index);
                    }
                }
            }

            if (index == deleteIndex) {
                if (fh != null) {
                    ep.delete(fh);
                    fh = null;
                }
            } else if (fh == null) {
                fh = ep.insert("" + index);
            }

            return true;
        }

        public EPManipulator3 setDeleteIndex(int deleteIndex) {
            this.deleteIndex = deleteIndex;
            return this;
        }
    }
}