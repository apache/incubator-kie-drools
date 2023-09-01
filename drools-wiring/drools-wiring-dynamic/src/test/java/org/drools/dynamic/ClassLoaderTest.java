/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.junit.Test;

public class ClassLoaderTest {

    @Test(timeout = 20000)
    public void testParallelClassLoading() {

        final int THREAD_COUNT = 100;

        final ProjectClassLoader projectClassLoader = ProjectClassLoader.createProjectClassLoader();
        final ClassLoader internalTypesClassLoader = (ClassLoader) projectClassLoader.makeClassLoader();

        projectClassLoader.setInternalClassLoader((ProjectClassLoader.InternalTypesClassLoader) internalTypesClassLoader);

        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            final List<Future> futures = new ArrayList<>();

            for (int i = 0; i < THREAD_COUNT; i++) {
                if (i % 2 == 0) {
                    futures.add(executorService.submit(() -> {
                        try {
                            Class.forName("nonexistant", true, projectClassLoader);
                        } catch (ClassNotFoundException e) {
                            //
                        }
                    }));
                } else {
                    futures.add(executorService.submit(() -> {
                        try {
                            Class.forName("nonexistant", true, internalTypesClassLoader);
                        } catch (ClassNotFoundException e) {
                            //
                        }
                    }));
                }
            }

            for (int i = 1; i <= THREAD_COUNT; i++) {
                try {
                    futures.get(i - 1).get();
                } catch (final InterruptedException | ExecutionException e) {
                    // Nothing
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }

}
