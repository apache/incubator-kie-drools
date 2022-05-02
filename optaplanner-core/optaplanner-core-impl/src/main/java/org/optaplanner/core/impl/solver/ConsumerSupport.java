/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.solver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

final class ConsumerSupport<Solution_, ProblemId_> implements AutoCloseable {

    private final ProblemId_ problemId;
    private final Consumer<? super Solution_> bestSolutionConsumer;
    private final Consumer<? super Solution_> finalBestSolutionConsumer;
    private final BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler;
    private final Semaphore activeConsumption = new Semaphore(1);
    private final BestSolutionHolder<Solution_> bestSolutionHolder;
    private final ExecutorService consumerExecutor = Executors.newSingleThreadExecutor();

    public ConsumerSupport(ProblemId_ problemId, Consumer<? super Solution_> bestSolutionConsumer,
            Consumer<? super Solution_> finalBestSolutionConsumer,
            BiConsumer<? super ProblemId_, ? super Throwable> exceptionHandler,
            BestSolutionHolder<Solution_> bestSolutionHolder) {
        this.problemId = problemId;
        this.bestSolutionConsumer = bestSolutionConsumer;
        this.finalBestSolutionConsumer = finalBestSolutionConsumer == null ? finalBestSolution -> {
        } : finalBestSolutionConsumer;
        this.exceptionHandler = exceptionHandler;
        this.bestSolutionHolder = bestSolutionHolder;
    }

    // Called on the Solver thread.
    void consumeIntermediateBestSolution(Solution_ bestSolution, BooleanSupplier isEveryProblemChangeProcessed) {
        /*
         * If the bestSolutionConsumer is not provided, the best solution is still set for the purpose of recording
         * problem changes.
         */
        bestSolutionHolder.set(bestSolution, isEveryProblemChangeProcessed);
        if (bestSolutionConsumer != null) {
            tryConsumeWaitingIntermediateBestSolution();
        }
    }

    // Called on the Solver thread after Solver#solve() returns.
    void consumeFinalBestSolution(Solution_ finalBestSolution) {
        try {
            // Wait for the previous consumption to complete.
            // As the solver has already finished, holding the solver thread is not an issue.
            activeConsumption.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted when waiting for the final best solution consumption.");
        }
        // Make sure the final best solution is consumed by the intermediate best solution consumer first.
        // Situation:
        // The consumer is consuming the last but one best solution. The final best solution is waiting for the consumer.
        if (bestSolutionConsumer != null) {
            scheduleIntermediateBestSolutionConsumption();
        }
        consumerExecutor.submit(() -> {
            try {
                finalBestSolutionConsumer.accept(finalBestSolution);
            } catch (Throwable throwable) {
                exceptionHandler.accept(problemId, throwable);
            } finally {
                // If there is no intermediate best solution consumer, complete the problem changes now.
                if (bestSolutionConsumer == null) {
                    bestSolutionHolder.take().completeProblemChanges();
                }
                // Cancel problem changes that arrived after the solver terminated.
                bestSolutionHolder.cancelPendingChanges();
                activeConsumption.release();
                disposeConsumerThread();
            }
        });
    }

    // Called both on the Solver thread and the Consumer thread.
    private void tryConsumeWaitingIntermediateBestSolution() {
        if (bestSolutionHolder.isEmpty()) {
            return; // There is no best solution to consume.
        }
        if (activeConsumption.tryAcquire()) {
            scheduleIntermediateBestSolutionConsumption().thenRunAsync(this::tryConsumeWaitingIntermediateBestSolution,
                    consumerExecutor);
        }
    }

    /**
     * Called both on the Solver thread and the Consumer thread.
     * Don't call without locking, otherwise multiple consumptions may be scheduled.
     */
    private CompletableFuture<Void> scheduleIntermediateBestSolutionConsumption() {
        return CompletableFuture.runAsync(() -> {
            BestSolutionContainingProblemChanges<Solution_> bestSolutionContainingProblemChanges = bestSolutionHolder.take();
            if (bestSolutionContainingProblemChanges != null) {
                try {
                    bestSolutionConsumer.accept(bestSolutionContainingProblemChanges.getBestSolution());
                    bestSolutionContainingProblemChanges.completeProblemChanges();
                } catch (Throwable throwable) {
                    if (exceptionHandler != null) {
                        exceptionHandler.accept(problemId, throwable);
                    }
                    bestSolutionContainingProblemChanges.completeProblemChangesExceptionally(throwable);
                } finally {
                    activeConsumption.release();
                }
            }
        }, consumerExecutor);
    }

    @Override
    public void close() {
        disposeConsumerThread();
        bestSolutionHolder.cancelPendingChanges();
    }

    private void disposeConsumerThread() {
        consumerExecutor.shutdownNow();
    }
}
