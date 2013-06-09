/*
 * Copyright 2013 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.solution.mutation.MutationCounter;
import org.optaplanner.core.impl.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.event.SolverEventListener;
import org.optaplanner.core.impl.solution.Solution;

public class BestSolutionMutationSingleStatistic extends AbstractSingleStatistic {

    private BestSolutionMutationSingleStatisticListener listener = new BestSolutionMutationSingleStatisticListener();

    private List<BestSolutionMutationSingleStatisticPoint> pointList = new ArrayList<BestSolutionMutationSingleStatisticPoint>();

    public List<BestSolutionMutationSingleStatisticPoint> getPointList() {
        return pointList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void open(Solver solver) {
        SolutionDescriptor solutionDescriptor = solver.getScoreDirectorFactory().getSolutionDescriptor();
        listener.setMutationCounter(new MutationCounter(solutionDescriptor));
        solver.addEventListener(listener);
    }

    public void close(Solver solver) {
        solver.removeEventListener(listener);
    }

    private class BestSolutionMutationSingleStatisticListener implements SolverEventListener {

        private MutationCounter mutationCounter;

        private Solution oldBestSolution = null;

        private void setMutationCounter(MutationCounter mutationCounter) {
            if (this.mutationCounter != null) {
                throw new IllegalStateException("Impossible that mutationCounter (" + mutationCounter
                        + ") is not null.");
            }
            this.mutationCounter = mutationCounter;
        }

        public void bestSolutionChanged(BestSolutionChangedEvent event) {
            int mutationCount;
            Solution newBestSolution = event.getNewBestSolution();
            if (oldBestSolution == null) {
                mutationCount = 0;
            } else {
                mutationCount = mutationCounter.countMutations(oldBestSolution, newBestSolution);
            }
            pointList.add(new BestSolutionMutationSingleStatisticPoint(
                    event.getTimeMillisSpend(), mutationCount));
            oldBestSolution = newBestSolution;
        }

    }

}
