package org.drools.compiler.command;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class PropagationListTest {

    @Test @Ignore
    public void test() {
        final int OBJECT_NR = 1000000;
        final int THREAD_NR = 8;

        Executor executor = Executors.newFixedThreadPool(THREAD_NR, new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        });

        long[] results = new long[10];

        for (int counter = 0; counter < results.length;) {

            final Checker checker = new Checker(THREAD_NR);
            final PropagationList propagationList = new SynchronizedPropagationList(null);
            CompletionService<Boolean> ecs = new ExecutorCompletionService<Boolean>(executor);

            long start = System.nanoTime();

            for (int i = 0; i < THREAD_NR; i++) {
                ecs.submit(getTask(OBJECT_NR, checker, propagationList, i));
            }

            try {
                Thread.sleep(1L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < THREAD_NR * 20; i++) {
                //System.out.println("FLUSHING!");
                propagationList.flush();
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            boolean success = true;
            for (int i = 0; i < THREAD_NR; i++) {
                try {
                    success = ecs.take().get() && success;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            propagationList.flush();

            results[counter++] = System.nanoTime() - start;

            System.out.println("Threads DONE!");
        }

        analyzeResults(results);
    }

    private void analyzeResults(long[] results) {
        long min = results[0];
        long max = results[0];
        long total = results[0];
        for (int i = 0; i < results.length; i++) {
            if (results[i] < min) min = results[i];
            if (results[i] > max) max = results[i];
            total += results[i];
        }
        System.out.println("min = " + min);
        System.out.println("max = " + max);
        System.out.println("avg = " + ((total - min - max) / (results.length - 2)));
    }

    private Callable<Boolean> getTask(final int OBJECT_NR, final Checker checker, final PropagationList propagationList, final int i) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                for (int j = 0; j < OBJECT_NR; j++) {
                    propagationList.addEntry(new TestEntry(checker, i, j));
                }
                return true;
            }
        };
    }

    public static class TestEntry extends PropagationEntry.AbstractPropagationEntry {

        final Checker checker;
        final int i;
        final int j;

        public TestEntry(Checker checker, int i, int j) {
            this.checker = checker;
            this.i = i;
            this.j = j;
        }

        @Override
        public void execute(InternalWorkingMemory wm) {
            checker.check(this);
        }

        @Override
        public String toString() {
            return "[" + i + ", " + j + "]";
        }
    }

    public static class Checker {
        private final int[] counters;

        public Checker(int nr) {
            counters = new int[nr];
        }

        public void check(TestEntry entry) {
            if (counters[entry.i] == entry.j) {
                if (entry.j % 10000 == 0) {
                    //System.out.println("[" + entry.i + ", " + entry.j / 10000 + "]");
                }
                counters[entry.i]++;
            } else {
                throw new RuntimeException("ERROR for thread " + entry.i + " expected " + counters[entry.i] + " but was " + entry.j);
            }
        }
    }
}
