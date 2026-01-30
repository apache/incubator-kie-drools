/*
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
package org.drools.core.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.kie.internal.concurrent.ExecutorProviderFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for ExecutorProviderImpl to verify proper shutdown behavior
 * and prevention of ClassLoader leaks.
 *
 * Note: These tests verify the shutdown mechanism works correctly.
 * In production, users should explicitly call ExecutorProviderFactory.shutdown()
 * when the application/container is being stopped to prevent ClassLoader leaks.
 */
public class ExecutorProviderImplTest {

    @Test
    public void testExecutorProviderShutdown() {
        // Get the executor service
        ExecutorService executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
        assertThat(executor).isNotNull();
        assertThat(executor.isShutdown()).as("Executor should not be shutdown initially").isFalse();

        // Explicitly shutdown the executor provider
        ExecutorProviderFactory.shutdown();

        // Verify the executor is shutdown
        assertThat(executor.isShutdown()).as("Executor should be shutdown after calling shutdown()").isTrue();
    }

    @Test
    public void testExecutorProviderShutdownIdempotent() {
        // Get the executor service
        ExecutorService executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
        assertThat(executor).isNotNull();

        // Call shutdown multiple times - should not throw exception
        ExecutorProviderFactory.shutdown();
        ExecutorProviderFactory.shutdown();
        ExecutorProviderFactory.shutdown();

        // Verify the executor is shutdown
        assertThat(executor.isShutdown()).as("Executor should remain shutdown").isTrue();
    }

    @Test
    public void testExecutorProviderTerminatesThreads() throws InterruptedException {
        // Get the executor and submit a task
        ExecutorService executor = ExecutorProviderFactory.getExecutorProvider().getExecutor();
        assertThat(executor).isNotNull();

        // Use CountDownLatch to ensure task has started before shutdown
        CountDownLatch taskStarted = new CountDownLatch(1);
        CountDownLatch taskInterrupted = new CountDownLatch(1);
        CountDownLatch keepRunning = new CountDownLatch(1); // Will never count down - blocks until interrupted

        // Submit a long-running task
        executor.submit(() -> {
            try {
                taskStarted.countDown(); // Signal that task has started
                keepRunning.await(); // Block indefinitely until interrupted
            } catch (InterruptedException e) {
                // Expected when shutdownNow is called
                taskInterrupted.countDown();
                Thread.currentThread().interrupt();
            }
        });

        // Wait for task to start (with timeout)
        boolean started = taskStarted.await(5, TimeUnit.SECONDS);
        assertThat(started).as("Task should start within 5 seconds").isTrue();

        // Explicitly shutdown the executor provider
        ExecutorProviderFactory.shutdown();

        // Verify the executor is shutdown
        assertThat(executor.isShutdown()).as("Executor should be shutdown").isTrue();

        // Verify task was interrupted
        boolean interrupted = taskInterrupted.await(5, TimeUnit.SECONDS);
        assertThat(interrupted).as("Task should be interrupted within 5 seconds").isTrue();

        // Wait for termination (should be quick with shutdownNow)
        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertThat(terminated).as("Executor should terminate within 5 seconds").isTrue();
    }
}
