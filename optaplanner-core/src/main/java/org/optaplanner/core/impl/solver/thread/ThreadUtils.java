/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    public static void shutdownAwaitOrKill(ExecutorService executor, String logIndentation, String name) {
        // Intentionally clearing the interrupted flag so that awaitTermination() in step 3 works.
        if (Thread.interrupted()) {
            // 2a. If the current thread is interrupted, propagate interrupt signal to children by initiating
            // an abrupt shutdown.
            executor.shutdownNow();
        } else {
            // 2b. Otherwise, initiate an orderly shutdown of the executor. This allows partition solvers to finish
            // solving upon detecting the termination issued previously (step 1). Shutting down the executor
            // service is important because the JVM cannot exit until all non-daemon threads have terminated.
            executor.shutdown();
        }

        // 3. Finally, wait until the executor finishes shutting down.
        try {
            final int awaitingSeconds = 1;
            if (!executor.awaitTermination(awaitingSeconds, TimeUnit.SECONDS)) {
                // Some solvers refused to complete. Busy threads will be interrupted in the finally block.
                // We're only logging the error instead throwing an exception to prevent eating the original
                // exception.
                logger.error(
                        "{}{}'s ExecutorService didn't terminate within timeout ({} seconds).",
                        logIndentation, name,
                        awaitingSeconds);
            }
        } catch (InterruptedException e) {
            // Interrupted while waiting for thread pool termination. Busy threads will be interrupted
            // in the finally block.
            Thread.currentThread().interrupt();
            // If there is an original exception it will be eaten by this.
            throw new IllegalStateException("Thread pool termination was interrupted.", e);
        } finally {
            // Initiate an abrupt shutdown for the case when any of the previous measures failed.
            executor.shutdownNow();
        }
    }

    // ************************************************************************
    // Private constructor
    // ************************************************************************

    private ThreadUtils() {
    }

}
