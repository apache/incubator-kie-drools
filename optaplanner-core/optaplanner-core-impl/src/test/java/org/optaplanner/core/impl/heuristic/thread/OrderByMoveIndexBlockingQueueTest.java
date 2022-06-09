/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.heuristic.thread;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.heuristic.move.DummyMove;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OrderByMoveIndexBlockingQueueTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderByMoveIndexBlockingQueueTest.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @AfterEach
    void tearDown() throws InterruptedException {
        executorService.shutdownNow();
        if (!executorService.awaitTermination(1, TimeUnit.MILLISECONDS)) {
            LOGGER.warn("Thread pool didn't terminate within the timeout.");
        }
    }

    @Test
    void addMove() throws InterruptedException {
        // Capacity: 4 moves in circulation + 2 exception handling results
        OrderByMoveIndexBlockingQueue<TestdataSolution> queue = new OrderByMoveIndexBlockingQueue<>(4 + 2);

        queue.startNextStep(0);
        executorService.submit(() -> queue.addMove(0, 0, 0, new DummyMove("a0"), SimpleScore.of(-100)));
        executorService.submit(() -> queue.addMove(1, 0, 1, new DummyMove("a1"), SimpleScore.of(-1000)));
        executorService.submit(() -> queue.addMove(0, 0, 2, new DummyMove("a2"), SimpleScore.of(-200)));
        executorService.submit(() -> queue.addMove(1, 0, 3, new DummyMove("a3"), SimpleScore.of(-30)));
        assertResult("a0", -100, queue.take());
        assertResult("a1", -1000, queue.take());
        assertResult("a2", -200, queue.take());
        executorService.submit(() -> queue.addMove(1, 0, 5, new DummyMove("a5"), SimpleScore.of(-5)));
        executorService.submit(() -> queue.addMove(1, 0, 4, new DummyMove("a4"), SimpleScore.of(-4)));
        assertResult("a3", -30, queue.take());
        executorService.submit(() -> queue.addMove(1, 0, 9, new DummyMove("a9"), SimpleScore.of(-9)));
        assertResult("a4", -4, queue.take());
        assertResult("a5", -5, queue.take());
        executorService.submit(() -> queue.addMove(1, 0, 8, new DummyMove("a8"), SimpleScore.of(-8)));
        executorService.submit(() -> queue.addMove(0, 0, 6, new DummyMove("a6"), SimpleScore.of(-6)));
        executorService.submit(() -> queue.addMove(1, 0, 7, new DummyMove("a7"), SimpleScore.of(-7)));
        assertResult("a6", -6, queue.take());
        executorService.submit(() -> queue.addMove(1, 0, 10, new DummyMove("a10"), SimpleScore.of(-10)));

        queue.startNextStep(1);
        executorService.submit(() -> queue.addMove(0, 1, 0, new DummyMove("b0"), SimpleScore.of(0)));
        executorService.submit(() -> queue.addMove(1, 0, 11, new DummyMove("a11"), SimpleScore.of(-11)));
        assertResult("b0", 0, queue.take());
        executorService.submit(() -> queue.addMove(0, 1, 3, new DummyMove("b3"), SimpleScore.of(-3)));
        executorService.submit(() -> queue.addMove(0, 1, 1, new DummyMove("b1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addMove(0, 1, 2, new DummyMove("b2"), SimpleScore.of(-2)));
        assertResult("b1", -1, queue.take());
        executorService.submit(() -> queue.addMove(0, 1, 4, new DummyMove("b4"), SimpleScore.of(-4)));

        queue.startNextStep(2);
        executorService.submit(() -> queue.addMove(1, 2, 2, new DummyMove("c2"), SimpleScore.of(-2)));
        executorService.submit(() -> queue.addMove(1, 2, 1, new DummyMove("c1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addMove(1, 2, 0, new DummyMove("c0"), SimpleScore.of(0)));
        assertResult("c0", 0, queue.take());
        assertResult("c1", -1, queue.take());
        assertResult("c2", -2, queue.take());
    }

    @Test
    void addUndoableMove() throws InterruptedException {
        // Capacity: 4 moves in circulation + 2 exception handling results
        OrderByMoveIndexBlockingQueue<TestdataSolution> queue = new OrderByMoveIndexBlockingQueue<>(4 + 2);

        queue.startNextStep(0);
        executorService.submit(() -> queue.addUndoableMove(0, 0, 0, new DummyMove("a0")));
        executorService.submit(() -> queue.addUndoableMove(1, 0, 3, new DummyMove("a3")));
        executorService.submit(() -> queue.addMove(0, 0, 1, new DummyMove("a1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addUndoableMove(1, 0, 2, new DummyMove("a2")));
        assertResult("a0", false, queue.take());
        assertResult("a1", -1, queue.take());
        assertResult("a2", false, queue.take());

        queue.startNextStep(1);
        executorService.submit(() -> queue.addMove(0, 1, 1, new DummyMove("b1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addUndoableMove(1, 0, 4, new DummyMove("a4")));
        executorService.submit(() -> queue.addUndoableMove(1, 1, 0, new DummyMove("b0")));
        assertResult("b0", false, queue.take());
        assertResult("b1", -1, queue.take());
    }

    @Test
    void addExceptionThrown() throws InterruptedException, ExecutionException {
        // Capacity: 4 moves in circulation + 2 exception handling results
        OrderByMoveIndexBlockingQueue<TestdataSolution> queue = new OrderByMoveIndexBlockingQueue<>(4 + 2);

        queue.startNextStep(0);
        executorService.submit(() -> queue.addMove(0, 0, 1, new DummyMove("a1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addMove(1, 0, 0, new DummyMove("a0"), SimpleScore.of(0)));
        executorService.submit(() -> queue.addMove(0, 0, 2, new DummyMove("a2"), SimpleScore.of(-2)));
        executorService.submit(() -> queue.addMove(1, 0, 3, new DummyMove("a3"), SimpleScore.of(-3)));
        assertResult("a0", 0, queue.take());
        assertResult("a1", -1, queue.take());
        assertResult("a2", -2, queue.take());

        queue.startNextStep(1);

        CountDownLatch allPrecedingTasksFinished = new CountDownLatch(3);
        executorService.submit(() -> {
            queue.addMove(0, 1, 1, new DummyMove("b1"), SimpleScore.of(-1));
            allPrecedingTasksFinished.countDown();
        });
        executorService.submit(() -> {
            queue.addUndoableMove(1, 0, 4, new DummyMove("a4"));
            allPrecedingTasksFinished.countDown();
        });
        executorService.submit(() -> {
            queue.addUndoableMove(1, 1, 0, new DummyMove("b0"));
            allPrecedingTasksFinished.countDown();
        });
        allPrecedingTasksFinished.await();

        IllegalArgumentException exception = new IllegalArgumentException();
        Future<?> exceptionFuture = executorService.submit(() -> queue.addExceptionThrown(1, exception));
        exceptionFuture.get(); // Avoid random failing test when the task hasn't started yet or the next task finishes earlier
        executorService.submit(() -> queue.addMove(0, 1, 2, new DummyMove("b2"), SimpleScore.of(-2))).get();
        assertResult("b0", false, queue.take());
        assertResult("b1", -1, queue.take());
        assertThatThrownBy(queue::take).hasCause(exception);
    }

    @Test
    void addExceptionIsNotEatenIfNextStepStartsBeforeTaken() throws InterruptedException, ExecutionException {
        // Capacity: 4 moves in circulation + 2 exception handling results
        OrderByMoveIndexBlockingQueue<TestdataSolution> queue = new OrderByMoveIndexBlockingQueue<>(4 + 2);

        queue.startNextStep(0);
        executorService.submit(() -> queue.addMove(0, 0, 1, new DummyMove("a1"), SimpleScore.of(-1)));
        executorService.submit(() -> queue.addMove(1, 0, 0, new DummyMove("a0"), SimpleScore.of(0)));
        executorService.submit(() -> queue.addMove(0, 0, 2, new DummyMove("a2"), SimpleScore.of(-2)));
        executorService.submit(() -> queue.addMove(1, 0, 3, new DummyMove("a3"), SimpleScore.of(-3)));
        IllegalArgumentException exception = new IllegalArgumentException();
        Future<?> exceptionFuture = executorService.submit(() -> queue.addExceptionThrown(1, exception));
        assertThatThrownBy(() -> {
            assertResult("a0", 0, queue.take());
            assertResult("a1", -1, queue.take());
            assertResult("a2", -2, queue.take());

            exceptionFuture.get(); // Avoid random failing test when the task hasn't started yet
            queue.startNextStep(1);
        }).hasCause(exception);
    }

    private void assertResult(String moveCode, int score, OrderByMoveIndexBlockingQueue.MoveResult<TestdataSolution> result) {
        assertCode(moveCode, result.getMove());
        assertThat(result.getScore()).isEqualTo(SimpleScore.of(score));
    }

    private void assertResult(String moveCode, boolean doable,
            OrderByMoveIndexBlockingQueue.MoveResult<TestdataSolution> result) {
        assertCode(moveCode, result.getMove());
        assertThat(result.isMoveDoable()).isEqualTo(doable);
    }

}
