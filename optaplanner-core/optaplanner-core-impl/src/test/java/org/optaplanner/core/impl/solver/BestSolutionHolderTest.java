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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class BestSolutionHolderTest {

    @Test
    void setBestSolution() {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();
        assertThat(bestSolutionHolder.take()).isNull();

        TestdataSolution solution1 = TestdataSolution.generateSolution();
        TestdataSolution solution2 = TestdataSolution.generateSolution();

        bestSolutionHolder.set(solution1, () -> true);
        assertThat(bestSolutionHolder.take().getBestSolution()).isSameAs(solution1);
        assertThat(bestSolutionHolder.take()).isNull();

        bestSolutionHolder.set(solution1, () -> true);
        bestSolutionHolder.set(solution2, () -> false);
        assertThat(bestSolutionHolder.take().getBestSolution()).isSameAs(solution1);

        bestSolutionHolder.set(solution1, () -> true);
        bestSolutionHolder.set(solution2, () -> true);
        assertThat(bestSolutionHolder.take().getBestSolution()).isSameAs(solution2);
    }

    @Test
    void completeProblemChanges() {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();

        CompletableFuture<Void> problemChange1 = addProblemChange(bestSolutionHolder);
        bestSolutionHolder.set(TestdataSolution.generateSolution(), () -> true);
        CompletableFuture<Void> problemChange2 = addProblemChange(bestSolutionHolder);

        bestSolutionHolder.take().completeProblemChanges();
        assertThat(problemChange1).isCompleted();
        assertThat(problemChange2).isNotCompleted();

        CompletableFuture<Void> problemChange3 = addProblemChange(bestSolutionHolder);
        bestSolutionHolder.set(TestdataSolution.generateSolution(), () -> true);
        bestSolutionHolder.set(TestdataSolution.generateSolution(), () -> true);
        CompletableFuture<Void> problemChange4 = addProblemChange(bestSolutionHolder);

        bestSolutionHolder.take().completeProblemChanges();

        assertThat(problemChange2).isCompleted();
        assertThat(problemChange3).isCompleted();
        assertThat(problemChange4).isNotCompleted();
    }

    @Test
    void cancelPendingChanges_noChangesRetrieved() {
        BestSolutionHolder<TestdataSolution> bestSolutionHolder = new BestSolutionHolder<>();

        CompletableFuture<Void> problemChange = addProblemChange(bestSolutionHolder);
        bestSolutionHolder.set(TestdataSolution.generateSolution(), () -> true);

        bestSolutionHolder.cancelPendingChanges();

        BestSolutionContainingProblemChanges<TestdataSolution> bestSolution = bestSolutionHolder.take();
        bestSolution.completeProblemChanges();

        assertThat(problemChange).isCancelled();
    }

    private CompletableFuture<Void> addProblemChange(BestSolutionHolder<TestdataSolution> bestSolutionHolder) {
        Solver<TestdataSolution> solver = mock(Solver.class);
        ProblemChange<TestdataSolution> problemChange = mock(ProblemChange.class);
        CompletableFuture<Void> futureChange = bestSolutionHolder.addProblemChange(solver, problemChange);
        verify(solver, times(1)).addProblemChange(problemChange);
        return futureChange;
    }
}
