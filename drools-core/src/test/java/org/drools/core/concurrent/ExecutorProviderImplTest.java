/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.kie.api.concurrent.KieExecutors.Pool;
import org.kie.internal.concurrent.ExecutorProviderFactory;

import static org.junit.Assert.*;


public class ExecutorProviderImplTest {
    @Test
    public void testMaxThread() {

        System.out.println("org.kie.api.concurrent.KieExecutors$Pool.SIZE = " + Pool.SIZE); // Runtime.getRuntime().availableProcessors()

        ThreadPoolExecutor executor = (ThreadPoolExecutor)ExecutorProviderFactory.getExecutorProvider().getExecutor();

        for (int i = 0; i < Pool.SIZE; i++) {
            executor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            });
        }

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
        }

        assertEquals(Pool.SIZE, executor.getActiveCount());

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }
}
