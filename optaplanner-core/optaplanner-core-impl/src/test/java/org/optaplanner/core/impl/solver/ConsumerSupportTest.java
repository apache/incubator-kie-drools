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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class ConsumerSupportTest {

    private ConsumerSupport<TestdataSolution, Long> consumerSupport;

    @AfterEach
    void close() {
        consumerSupport.close();
    }

    @Test
    @Timeout(60)
    void skipAhead() throws InterruptedException {
        CountDownLatch consumptionStarted = new CountDownLatch(1);
        CountDownLatch consumptionPaused = new CountDownLatch(1);
        CountDownLatch consumptionCompleted = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        List<TestdataSolution> consumedSolutions = Collections.synchronizedList(new ArrayList<>());
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();
        consumerSupport = new ConsumerSupport<>(1L, testdataSolution -> {
            try {
                consumptionStarted.countDown();
                consumptionPaused.await();
                consumedSolutions.add(testdataSolution);
                if (testdataSolution.getEntityList().size() == 3) { // The last best solution.
                    consumptionCompleted.countDown();
                }
            } catch (InterruptedException e) {
                error.set(new IllegalStateException("Interrupted waiting.", e));
            }
        }, null, null, bestSolutionHolder);

        consumeIntermediateBestSolution(TestdataSolution.generateSolution(1, 1));
        consumptionStarted.await();
        // This solution should be skipped.
        consumeIntermediateBestSolution(TestdataSolution.generateSolution(2, 2));
        // This solution should never be skipped.
        consumeIntermediateBestSolution(TestdataSolution.generateSolution(3, 3));

        consumptionPaused.countDown();
        consumptionCompleted.await();
        assertThat(consumedSolutions).hasSize(2);
        assertThat(consumedSolutions.get(0).getEntityList()).hasSize(1);
        assertThat(consumedSolutions.get(1).getEntityList()).hasSize(3);

        if (error.get() != null) {
            fail("Exception during consumption.", error.get());
        }
    }

    @Test
    @Timeout(60)
    void problemChangesComplete_afterFinalBestSolutionIsConsumed() throws ExecutionException, InterruptedException {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();
        AtomicReference<TestdataSolution> finalBestSolutionRef = new AtomicReference<>();
        consumerSupport = new ConsumerSupport<>(1L, null,
                finalBestSolution -> finalBestSolutionRef.set(finalBestSolution), null, bestSolutionHolder);

        CompletableFuture<Void> futureProblemChange = addProblemChange(bestSolutionHolder);

        consumeIntermediateBestSolution(TestdataSolution.generateSolution());
        assertThat(futureProblemChange).isNotCompleted();
        TestdataSolution finalBestSolution = TestdataSolution.generateSolution();
        consumerSupport.consumeFinalBestSolution(finalBestSolution);
        futureProblemChange.get();
        assertThat(finalBestSolutionRef.get()).isSameAs(finalBestSolution);
        assertThat(futureProblemChange).isCompleted();
    }

    @Test
    @Timeout(60)
    void problemChangesCompleteExceptionally_afterExceptionInConsumer() {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();
        final String errorMessage = "Test exception";
        Consumer<TestdataSolution> errorneousConsumer = bestSolution -> {
            throw new RuntimeException(errorMessage);
        };
        consumerSupport = new ConsumerSupport<>(1L, errorneousConsumer, null, null, bestSolutionHolder);

        CompletableFuture<Void> futureProblemChange = addProblemChange(bestSolutionHolder);
        consumeIntermediateBestSolution(TestdataSolution.generateSolution());

        assertThatExceptionOfType(ExecutionException.class).isThrownBy(() -> futureProblemChange.get())
                .havingRootCause()
                .isInstanceOf(RuntimeException.class)
                .withMessage(errorMessage);
        assertThat(futureProblemChange).isCompletedExceptionally();
    }

    @Test
    @Timeout(60)
    void pendingProblemChangesAreCanceled_afterFinalBestSolutionIsConsumed() throws ExecutionException, InterruptedException {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();
        consumerSupport = new ConsumerSupport<>(1L, null, null,
                null, bestSolutionHolder);

        CompletableFuture<Void> futureProblemChange = addProblemChange(bestSolutionHolder);

        consumeIntermediateBestSolution(TestdataSolution.generateSolution());
        assertThat(futureProblemChange).isNotCompleted();

        CompletableFuture<Void> pendingProblemChange = addProblemChange(bestSolutionHolder);
        consumerSupport.consumeFinalBestSolution(TestdataSolution.generateSolution());
        futureProblemChange.get();
        assertThat(futureProblemChange).isCompleted();

        assertThatExceptionOfType(CancellationException.class).isThrownBy(() -> pendingProblemChange.get());
    }

    private CompletableFuture<Void> addProblemChange(BestSolutionHolder<TestdataSolution> bestSolutionHolder) {
        return bestSolutionHolder.addProblemChange(mock(Solver.class), mock(ProblemChange.class));
    }

    private void consumeIntermediateBestSolution(TestdataSolution bestSolution) {
        consumerSupport.consumeIntermediateBestSolution(bestSolution, () -> true);
    }
}
