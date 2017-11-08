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

package org.drools.compiler.integrationtests.session;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SharedSessionParallelTest extends AbstractParallelTest {

   public SharedSessionParallelTest(final boolean enforcedJitting, final boolean serializeKieBase) {
      super(enforcedJitting, serializeKieBase);
   }

   @Test
   public void testNoExceptions() throws InterruptedException {
      String drl = "rule R1 when String() then end";

      int repetitions = 100;
      int numberOfObjects = 1000;
      int countOfThreads = 100;

      for (int i = 0; i < repetitions; i++) {

         KieSession kieSession = getKieBase(drl).newKieSession();

         parallelTest(countOfThreads, new ParallelTestExecutor() {
            @Override
            public boolean execute(int counter) {
               try {
                  for (int j = 0; j < numberOfObjects; j++) {
                     kieSession.insert("test_" + numberOfObjects);
                  }
                  kieSession.fireAllRules();
                  return true;
               } catch (Exception ex) {
                  throw new RuntimeException(ex);
               }
            }
         });

         disposeSession(kieSession);
      }
   }

   @Test
   public void testCheckOneThreadOnly() throws InterruptedException {
      int threadCount = 100;
      List<String> list = new ArrayList<>();

      String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule R1 " +
          "when " +
          "    BeanA($n : seed) " +
          "then " +
          "    list.add(\"\" + $n);" +
          "end";

      KieSession kieSession = getKieBase(drl).newKieSession();
      CountDownLatch latch = new CountDownLatch(threadCount);

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) throws InterruptedException {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter));
            latch.countDown();

            if (counter == 0) {
               latch.await();
               return kieSession.fireAllRules() == threadCount;
            }
            return true;
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);

      assertEquals(threadCount, list.size());
      for (int i = 0; i < threadCount; i++) {
         assertTrue(list.contains(""+i));
      }
   }

   @Test
   public void testCorrectFirings() throws InterruptedException {
      int threadCount = 100;

      String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List globalList;\n" +
          "rule R1 " +
          "when " +
          "    BeanA($n : seed) " +
          "then " +
          "    globalList.add(\"\" + $n);" +
          "end";

      KieSession kieSession = getKieBase(drl).newKieSession();

      List<String> list = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            kieSession.setGlobal("globalList", list);
            kieSession.insert(new BeanA(counter));
            kieSession.fireAllRules();
            return true;
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      checkList(threadCount, list);
   }

   @Test
   public void testCorrectFirings2() throws InterruptedException {
      int threadCount = 100;

      String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule R1 " +
          "when " +
          "    BeanA($n : seed, seed == 0) " +
          "then " +
          "    list.add(\"\" + $n);" +
          "end";

      KieSession kieSession = getKieBase(drl).newKieSession();
      List<String> list = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter%2));
            kieSession.fireAllRules();
            return true;
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      assertTrue(list.contains("" + 0));
      assertFalse(list.contains("" + 1));
      int expectedListSize = ((threadCount-1) / 2) + 1;
      assertEquals(expectedListSize, list.size());
   }


   @Test
   public void testLongRunningRule() throws InterruptedException {
      int threadCount = 100;
      int seed = threadCount + 200;
      int objectCount = 1000;

      String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule longRunning " +
          "when " +
          "    $bean : BeanA($n : seed, seed > " + threadCount + ") " +
          "then " +
          "    modify($bean) { setSeed($n-1) };" +
          "    list.add(\"\" + $bean.getSeed());" +
          "    Thread.sleep(5);" +
          "end";

      String listDrl = "global java.util.List list2;\n" +
          "rule listRule " +
          "when " +
          "    BeanA($n : seed, seed < " + threadCount + ") " +
          "then " +
          "    list2.add(\"\" + $n);" +
          "end";

      KieSession kieSession = getKieBase(longRunningDrl, listDrl).newKieSession();

      CyclicBarrier barrier = new CyclicBarrier(threadCount);
      List<String> list = new ArrayList<>();
      List<String> list2 = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            try {
               if (counter == 0) {
                  kieSession.setGlobal("list", list);
                  kieSession.setGlobal("list2", list2);
                  kieSession.insert(new BeanA(seed));
                  barrier.await();
                  kieSession.fireAllRules();
                  return true;
               } else {
                  barrier.await();
                  Thread.sleep(100);
                  for (int i = 0; i < objectCount; i++) {
                     kieSession.insert(new BeanA(counter));
                  }
                  kieSession.fireAllRules();
                  return true;
               }
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      checkList(threadCount, seed, list);
      checkList(1, threadCount, list2, (threadCount-1)*objectCount);
   }

   @Test
   public void testLongRunningRule2() throws InterruptedException {
      int threadCount = 100;
      int seed = 1000;

      String waitingRule = "rule waitingRule " +
          "when " +
          "    String( this == \"wait\" ) " +
          "then " +
          "    Thread.sleep(10);" +
          "end";

      String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule longRunning " +
          "when " +
          "    $bean : BeanA($n : seed, seed > 0 ) " +
          "then " +
          "    modify($bean) { setSeed($n-1) };" +
          "    list.add(\"\" + $bean.getSeed());" +
          "end";

      KieSession kieSession = getKieBase(longRunningDrl, waitingRule).newKieSession();

      CyclicBarrier barrier = new CyclicBarrier(threadCount);
      List<String> list = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            try {
               if (counter == 0) {
                  kieSession.setGlobal("list", list);
                  kieSession.insert("wait");
                  kieSession.insert(new BeanA(seed));
                  barrier.await();
                  return kieSession.fireAllRules() == seed * threadCount + 1;
               } else {
                  barrier.await();
                  Thread.sleep(10);
                  kieSession.insert(new BeanA(seed));
                  return kieSession.fireAllRules() == 0;
               }
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      checkList(0, seed, list, seed * threadCount);
   }

   @Test
   public void testLongRunningRule3() throws InterruptedException {
      int threadCount = 10;
      int seed = threadCount + 50;
      int objectCount = 1000;

      String longRunningDrl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule longRunning " +
          "when " +
          "    $bean : BeanA($n : seed, seed > " + threadCount + ") " +
          "then " +
          "    modify($bean) { setSeed($n-1) };" +
          "    list.add(\"\" + $bean.getSeed());" +
          "end";

      String listDrl = "global java.util.List list2;\n" +
          "rule listRule " +
          "when " +
          "    BeanA($n : seed, seed < " + threadCount + ") " +
          "then " +
          "    list2.add(\"\" + $n);" +
          "end";

      KieSession kieSession = getKieBase(longRunningDrl, listDrl).newKieSession();

      CyclicBarrier barrier = new CyclicBarrier(threadCount);
      List<String> list = new ArrayList<>();
      List<String> list2 = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            try {
               if (counter % 2 == 0) {
                  kieSession.setGlobal("list", list);
                  kieSession.setGlobal("list2", list2);
                  kieSession.insert(new BeanA(seed));
                  barrier.await();
                  kieSession.fireAllRules();
                  return true;
               } else {
                  barrier.await();
                  Thread.sleep(100);
                  for (int i = 0; i < objectCount; i++) {
                     kieSession.insert(new BeanA(counter));
                  }
                  kieSession.fireAllRules();
                  return true;
               }
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);

      int listExpectedSize = (threadCount/2 + threadCount%2) * (seed-threadCount);
      int list2ExpectedSize = threadCount/2 * objectCount;
      for (int i = 0; i < threadCount; i++) {
         if (i % 2 == 1) {
            list2.contains("" + i);
         }
      }
      assertEquals(listExpectedSize, list.size());
      assertEquals(list2ExpectedSize, list2.size());
   }

   @Test
   public void testCountdownBean() throws InterruptedException {
      int threadCount = 100;
      int seed = 1000;

      String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule countdown " +
          "when " +
          "    $bean : BeanA($n : seed, seed >  0 ) " +
          "then " +
          "    modify($bean) { setSeed($n-1) };" +
          "    list.add(\"\" + $bean.getSeed());" +
          "end";

      KieSession kieSession = getKieBase(drl).newKieSession();
      CyclicBarrier barrier = new CyclicBarrier(threadCount);
      List<String> list = new ArrayList<>();
      BeanA bean = new BeanA(seed);

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            try {
               if (counter == 0) {
                  kieSession.setGlobal("list", list);
                  kieSession.insert(bean);
               }
               barrier.await();
               kieSession.fireAllRules();
               return true;
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      checkList(seed, list);
      assertEquals(0, bean.getSeed());
   }

   @Test
   public void testCountdownBean2() throws InterruptedException {
      int threadCount = 100;
      int seed = 1000;

      String drl = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule countdown " +
          "when " +
          "    $bean : BeanA($n : seed, seed >  0 ) " +
          "then " +
          "    modify($bean) { setSeed($n-1) };" +
          "    list.add(\"\" + $bean.getSeed());" +
          "end";

      KieSession kieSession = getKieBase(drl).newKieSession();
      List<String> list = new ArrayList<>();
      BeanA[] beans = new BeanA[threadCount];

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            BeanA bean = new BeanA(seed);
            beans[counter] = bean;
            try {
               if (counter == 0) {
                  kieSession.setGlobal("list", list);
               }
               kieSession.insert(bean);
               kieSession.fireAllRules();
               return true;
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);

      checkList(0, seed, list, seed * threadCount);
      for (BeanA bean : beans) {
         assertEquals(0, bean.getSeed());
      }
   }

   @Test
   public void testOneRulePerThread() throws InterruptedException {
      int threadCount = 1000;

      String[] drls = new String[threadCount];
      for (int i = 0; i < threadCount; i++) {
         drls[i] = "import " + BeanA.class.getCanonicalName() + ";\n" +
             "global java.util.List list;\n" +
             "rule R" + i + " " +
             "when " +
             "    $bean : BeanA( seed == " + i + " ) " +
             "then " +
             "    list.add(\"" + i + "\");" +
             "end";
      }

      KieSession kieSession = getKieBase(drls).newKieSession();
      List<String> list = new ArrayList<>();

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            kieSession.setGlobal("list", list);
            kieSession.insert(new BeanA(counter));
            kieSession.fireAllRules();
            return true;
         }
      };

      parallelTest(threadCount, exec);
      disposeSession(kieSession);
      checkList(threadCount, list);
   }
}
