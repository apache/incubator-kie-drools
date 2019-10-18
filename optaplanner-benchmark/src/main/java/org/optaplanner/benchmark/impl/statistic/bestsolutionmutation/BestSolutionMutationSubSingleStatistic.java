/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import java.util.List;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemBasedSubSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.solution.mutation.MutationCounter;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class BestSolutionMutationSubSingleStatistic<Solution_>
        extends ProblemBasedSubSingleStatistic<Solution_, BestSolutionMutationStatisticPoint> {

    private BestSolutionMutationSubSingleStatisticListener listener;

    public BestSolutionMutationSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, ProblemStatisticType.BEST_SOLUTION_MUTATION);
        listener = new BestSolutionMutationSubSingleStatisticListener();
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    @Override
    public void open(Solver<Solution_> solver) {
        DefaultSolver<Solution_> defaultSolver = (DefaultSolver<Solution_>) solver;
        InnerScoreDirectorFactory<Solution_> scoreDirectorFactory
                = (InnerScoreDirectorFactory<Solution_>) defaultSolver.getScoreDirectorFactory();
        SolutionDescriptor<Solution_> solutionDescriptor = scoreDirectorFactory.getSolutionDescriptor();
        listener.setMutationCounter(new MutationCounter<>(solutionDescriptor));
        solver.addEventListener(listener);
    }

    @Override
    public void close(Solver<Solution_> solver) {
        solver.removeEventListener(listener);
    }

    private class BestSolutionMutationSubSingleStatisticListener implements SolverEventListener<Solution_> {

        private MutationCounter<Solution_> mutationCounter;

        private Solution_ oldBestSolution = null;

        private void setMutationCounter(MutationCounter<Solution_> mutationCounter) {
            if (this.mutationCounter != null) {
                throw new IllegalStateException("Impossible state: mutationCounter (" + this.mutationCounter
                        + ") is not null.");
            }
            this.mutationCounter = mutationCounter;
        }

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<Solution_> event) {
            int mutationCount;
            Solution_ newBestSolution = event.getNewBestSolution();
            if (oldBestSolution == null) {
                mutationCount = 0;
            } else {
                mutationCount = mutationCounter.countMutations(oldBestSolution, newBestSolution);
            }
            pointList.add(new BestSolutionMutationStatisticPoint(
                    event.getTimeMillisSpent(), mutationCount));
            oldBestSolution = newBestSolution;
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return BestSolutionMutationStatisticPoint.buildCsvLine("timeMillisSpent", "mutationCount");
    }

    @Override
    protected BestSolutionMutationStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new BestSolutionMutationStatisticPoint(Long.parseLong(csvLine.get(0)),
                Integer.parseInt(csvLine.get(1)));
    }

}
