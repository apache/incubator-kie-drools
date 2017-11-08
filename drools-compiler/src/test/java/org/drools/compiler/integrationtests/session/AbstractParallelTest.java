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

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.utils.KieHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public abstract class AbstractParallelTest {

   protected final boolean enforcedJitting;
   protected final boolean serializeKieBase;

   @Parameterized.Parameters(name = "Enforced jitting={0}, Serialize KieBase={1}")
   public static List<Boolean[]> getTestParameters() {
      return Arrays.asList(
          new Boolean[]{false, false},
          new Boolean[]{false, true},
          new Boolean[]{true, false},
          new Boolean[]{true, true});
   }

   public AbstractParallelTest(final boolean enforcedJitting, final boolean serializeKieBase) {
      this.enforcedJitting = enforcedJitting;
      this.serializeKieBase = serializeKieBase;
   }

   public void parallelTest(int numberOfThreads, ParallelTestExecutor executor) throws InterruptedException {

      Callable<Boolean>[] tasks;

      final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads, new ThreadFactory() {
         @Override
         public Thread newThread(Runnable r) {
            final Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
         }
      });

      tasks = getTasks(numberOfThreads, executor);
      final CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executorService);

      try {
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

   public interface ParallelTestExecutor {
      public boolean execute(int counter) throws InterruptedException;
   }

   private Callable<Boolean>[] getTasks(int numberOfThreads, ParallelTestExecutor executor) {
      Callable<Boolean>[] tasks = new Callable[numberOfThreads];
      for (int i = 0; i < numberOfThreads; i++) {
         final int counter = i;
         tasks[counter] = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
               return executor.execute(counter);
            }
         };
      }
      return tasks;
   }

   private static synchronized KieHelper getKieHelper(String... drls) {
      KieHelper kieHelper = new KieHelper();
      for (String drl : drls) {
         kieHelper.addContent(drl, ResourceType.DRL);
      }
      return kieHelper;
   }

   protected synchronized KieBase getKieBase(String... drls) {
      KieBaseOption[] kieBaseOptions = (enforcedJitting) ? new KieBaseOption[]{ConstraintJittingThresholdOption.get(0)}
          : new KieBaseOption[]{};
      KieBase kieBase = getKieHelper(drls).build(kieBaseOptions);
      if (serializeKieBase) {
         kieBase = serializeAndDeserializeKieBase(kieBase);
      }
      return kieBase;
   }

   private KieBase serializeAndDeserializeKieBase(final KieBase kieBase) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream out = new ObjectOutputStream( baos );
         try {
            out.writeObject( kieBase );
            out.flush();
         } finally {
            out.close();
         }

         ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray() ) );
         try {
            return (KieBase) in.readObject();
         } finally {
            in.close();
         }

      } catch (IOException e) {
         throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   protected static void disposeSession(KieSession session) {
      if (session != null) {
         session.dispose();
      }
   }

   protected void checkList(int end, List list) {
      checkList(0, end, list);
   }

   protected void checkList(int start, int end, List list) {
      int expectedSize = end-start;
      checkList(start, end, list, expectedSize);
   }

   protected void checkList(int start, int end, List list, int expectedSize) {
      assertEquals(expectedSize, list.size());
      for (int i = start; i < end; i++) {
         assertTrue(list.contains(""+i));
      }
   }

   public static class BeanA {
      int seed;
      public BeanA() {
         this.seed = 1;
      }
      public BeanA(int seed) {
         this.seed = seed;
      }
      public int getSeed() {
         return this.seed;
      }
      public void setSeed(int seed) {
         this.seed = seed;
      }
   }

   public static class BeanB {
      int seed;
      public BeanB() {
         this.seed = 1;
      }
      public BeanB(int seed) {
         this.seed = seed;
      }
      public int getSeed() {
         return this.seed;
      }
      public void setSeed(int seed) {
         this.seed = seed;
      }
   }

}
