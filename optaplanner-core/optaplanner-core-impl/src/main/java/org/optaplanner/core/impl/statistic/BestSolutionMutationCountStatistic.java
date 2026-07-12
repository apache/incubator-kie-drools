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

package org.optaplanner.core.impl.statistic;

import java.util.Map;
import java.util.WeakHashMap;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.solution.mutation.MutationCounter;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;

import io.micrometer.core.instrument.Metrics;

public class BestSolutionMutationCountStatistic<Solution_> implements SolverStatistic<Solution_> {

    private final Map<Solver<Solution_>, SolverEventListener<Solution_>> solverToEventListenerMap = new WeakHashMap<>();

    @Override
    public void unregister(Solver<Solution_> solver) {
        SolverEventListener<Solution_> listener = solverToEventListenerMap.remove(solver);
        if (listener != null) {
            solver.removeEventListener(listener);
        }
    }

    @Override
    public void register(Solver<Solution_> solver) {
        DefaultSolver<Solution_> defaultSolver = (DefaultSolver<Solution_>) solver;
        InnerScoreDirectorFactory<Solution_, ?> innerScoreDirectorFactory = defaultSolver.getScoreDirectorFactory();
        SolutionDescriptor<Solution_> solutionDescriptor = innerScoreDirectorFactory.getSolutionDescriptor();
        MutationCounter<Solution_> mutationCounter = new MutationCounter<>(solutionDescriptor);
        BestSolutionMutationCountStatisticListener<Solution_> listener =
                Metrics.gauge(SolverMetric.BEST_SOLUTION_MUTATION.getMeterId(),
                        defaultSolver.getSolverScope().getMonitoringTags(),
                        new BestSolutionMutationCountStatisticListener<>(mutationCounter),
                        BestSolutionMutationCountStatisticListener::getMutationCount);
        solverToEventListenerMap.put(solver, listener);
        solver.addEventListener(listener);
    }

    private static class BestSolutionMutationCountStatisticListener<Solution_> implements SolverEventListener<Solution_> {
        final MutationCounter<Solution_> mutationCounter;
        int mutationCount = 0;
        Solution_ oldBestSolution = null;

        public BestSolutionMutationCountStatisticListener(MutationCounter<Solution_> mutationCounter) {
            this.mutationCounter = mutationCounter;
        }

        public int getMutationCount() {
            return mutationCount;
        }

        @Override
        public void bestSolutionChanged(BestSolutionChangedEvent<Solution_> event) {
            Solution_ newBestSolution = event.getNewBestSolution();
            if (oldBestSolution == null) {
                mutationCount = 0;
            } else {
                mutationCount = mutationCounter.countMutations(oldBestSolution, newBestSolution);
            }
            oldBestSolution = newBestSolution;
        }
    }
}
