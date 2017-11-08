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
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ConcurrentBasesParallelTest extends AbstractParallelTest {

   public ConcurrentBasesParallelTest(final boolean enforcedJitting, final boolean serializeKieBase) {
      super(enforcedJitting, serializeKieBase);
   }

   @Test
   public void testDifferentRuleset1() throws InterruptedException {
      int numberOfThreads = 100;
      int numberOfObjects = 100;

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA( seed == " + counter + ") " +
                "then " +
                "end";

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               for (int i = 0; i < numberOfObjects; i++) {
                  session.insert(new BeanA(i));
               }
               return session.fireAllRules() == 1;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRuleset2() throws InterruptedException {
      int numberOfThreads = 100;

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA( seed == " + counter + ") " +
                "then " +
                "end";

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               for (int i = 0; i < numberOfThreads; i++) {
                  if (i != counter) {
                     session.insert(new BeanA(i));
                  }
               }
               return session.fireAllRules() == 0;

            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRuleset3() throws InterruptedException {
      int numberOfThreads = 100;

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            String rule = "import " + BeanA.class.getCanonicalName() + ";\n" +
                "global "+ AtomicInteger.class.getCanonicalName() + " result;\n" +
                "rule Rule_" + counter + " " +
                "when " +
                "    BeanA()" +
                "then " +
                "    result.set(" + counter + ");" +
                "end";

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               session.insert(new BeanA());
               AtomicInteger r = new AtomicInteger(0);
               session.setGlobal("result", r);
               assertEquals(1, session.fireAllRules());
               assertEquals(counter, r.get());
               return true;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRuleset4() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "global java.util.List list;\n" +
          "rule ${ruleName} " +
          "when " +
          "${className}()" +
          "then " +
          "    list.add(\"${className}\");" +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               session.insert(new BeanA());
               session.insert(new BeanB());
               List<String> list = new ArrayList<>();
               session.setGlobal("list", list);
               int rulesFired = session.fireAllRules();
               assertEquals(1, list.size());
               assertEquals(className, list.get(0));
               return rulesFired == 1;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRuleset5() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule ${ruleName} " +
          "when " +
          "${className}()" +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter % 2 == 0) {
                  session.insert(new BeanB());
               } else  {
                  session.insert(new BeanA());
               }
               int rulesFired = session.fireAllRules();
               assertEquals(0, rulesFired);
               return true;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRuleset6() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule ${ruleName} " +
          "when " +
          "${className}()" +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter % 2 == 0) {
                  session.insert(new BeanA());
               } else  {
                  session.insert(new BeanB());
               }
               int rulesFired = session.fireAllRules();
               assertEquals(1, rulesFired);
               return true;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRulesetNot() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule ${ruleName} " +
          "when " +
          "    not ${className}()" +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter % 2 == 0) {
                  session.insert(new BeanA());
               } else  {
                  session.insert(new BeanB());
               }
               int rulesFired = session.fireAllRules();
               assertEquals(0, rulesFired);
               return true;
            } finally {
               disposeSession(session);
            }

         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRulesetExists() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule ${ruleName} " +
          "when " +
          "    exists ${className}()" +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);

            KieBase base = getKieBase(rule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter % 2 == 0) {
                  session.insert(new BeanA());
               } else  {
                  session.insert(new BeanB());
               }
               int rulesFired = session.fireAllRules();
               assertEquals(1, rulesFired);
               return true;
            } finally {
               disposeSession(session);
            }

         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testDifferentRulesetSharedSubnetwork() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleTemplate = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule ${ruleName} " +
          "when " +
          "    $bean : ${className}() \n" +
          "then " +
          "end";

      String subnetworkRuleTemplate = "rule Rule_subnetwork " +
          "when " +
          "    $bean : ${className}() \n" +
          "    Number( doubleValue > 0) from" +
          "       accumulate ( BeanA() and $s : String(), count($s) )" +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            String className = (counter % 2 == 0) ? "BeanA" : "BeanB";
            String ruleName = "Rule_" + className + "_" + counter;
            String rule = ruleTemplate.replace("${ruleName}", ruleName).replace("${className}", className);
            String subnetworkRule = subnetworkRuleTemplate.replace("${className}", className);

            KieBase base = getKieBase(rule, subnetworkRule);
            KieSession session = null;

            try {
               session = base.newKieSession();
               session.insert("test");
               if (counter % 2 == 0) {
                  session.insert(new BeanA());
                  assertEquals(2, session.fireAllRules());
               } else  {
                  session.insert(new BeanB());
                  assertEquals(1, session.fireAllRules());
               }
               return true;
            } finally {
               disposeSession(session);
            }

         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testSameRuleset1() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "rule RuleA " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
          "then " +
          "end";

      String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule RuleB " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
          "then " +
          "end";


      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {

            KieBase base = getKieBase(ruleA, ruleB);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter %2 == 0) {
                  session.insert(new BeanA(counter));
                  return session.fireAllRules() == 1;
               } else {
                  return session.fireAllRules() == 0;
               }
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testSameRuleset2() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "rule RuleA " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
          "then " +
          "end";

      String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule RuleB " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            KieBase base = getKieBase(ruleA, ruleB);
            KieSession session = null;

            try {
               session = base.newKieSession();
               session.insert(new BeanA());
               session.insert(new BeanB());
               return session.fireAllRules() == 2;
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testSameRuleset3() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "rule RuleA " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanA(), count($bean)) " +
          "then " +
          "end";

      String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule RuleB " +
          "when " +
          "    $n : Number( doubleValue == 1 ) from accumulate($bean : BeanB(), count($bean)) " +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            KieBase base = getKieBase(ruleA, ruleB);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter %2 == 0) {
                  session.insert(new BeanA(counter));
               } else {
                  session.insert(new BeanB(counter));
               }
               return session.fireAllRules() == 1;
            } finally {
               disposeSession(session);
            }

         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testSameRuleset4() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "rule RuleNotA " +
          "when " +
          "    not BeanA() " +
          "then " +
          "end";

      String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule RuleNotB " +
          "when " +
          "    not BeanB() " +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            KieBase base = getKieBase(ruleA, ruleB);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter %2 == 0) {
                  session.insert(new BeanA(counter));
               } else {
                  session.insert(new BeanB(counter));
               }
               return session.fireAllRules() == 1;
            } finally {
               disposeSession(session);
            }

         }
      };

      parallelTest(numberOfThreads, exec);
   }

   @Test
   public void testSameRuleset5() throws InterruptedException {
      int numberOfThreads = 100;

      String ruleA = "import " + BeanA.class.getCanonicalName() + ";\n" +
          "rule RuleNotA " +
          "when " +
          "    not BeanA() " +
          "then " +
          "end";

      String ruleB = "import " + BeanB.class.getCanonicalName() + ";\n" +
          "rule RuleNotB " +
          "when " +
          "    not BeanB() " +
          "then " +
          "end";

      ParallelTestExecutor exec = new ParallelTestExecutor() {
         @Override
         public boolean execute(int counter) {
            KieBase base = getKieBase(ruleA, ruleB);
            KieSession session = null;

            try {
               session = base.newKieSession();
               if (counter %2 == 0) {
                  session.insert(new BeanA(counter));
                  session.insert(new BeanB(counter));
                  return session.fireAllRules() == 0;
               } else {
                  return session.fireAllRules() == 2;
               }
            } finally {
               disposeSession(session);
            }
         }
      };

      parallelTest(numberOfThreads, exec);
   }

   public void parallelTest(int numberOfThreads, ParallelTestExecutor executor) throws InterruptedException {

      Callable<Boolean>[] tasks = new Callable[numberOfThreads];

      final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
         }
      });

      try {
         for (int i = 0; i < numberOfThreads; i++) {
            final int counter = i;
            tasks[counter] = new Callable<Boolean>() {
               @Override
               public Boolean call() throws Exception {
                  return executor.execute(counter);
               }
            };
         }

         final CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executorService);

         for (Callable<Boolean> task : tasks) {
            completionService.submit(task);
         }

         int successCounter = 0;
         for (int i = 0; i < numberOfThreads; i++) {
            try {
               if (completionService.take().get()) {
                  successCounter ++;
               }
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
         }

         assertEquals(numberOfThreads, successCounter);

      } finally {
         executorService.shutdown();
         if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
         }
      }
   }
}
