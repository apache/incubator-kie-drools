/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.improvingsteppercentage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.move.Move;
import org.optaplanner.core.impl.phase.event.SolverPhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.step.AbstractStepScope;
import org.optaplanner.core.impl.solver.DefaultSolver;

public class ImprovingStepPercentageSingleStatistic extends AbstractSingleStatistic {

    private final long timeMillisThresholdInterval;
    private long nextTimeMillisThreshold;

    private final ImprovingStepPercentageSingleStatisticListener listener = new ImprovingStepPercentageSingleStatisticListener();

    private final Map<Class<? extends Move>, List<ImprovingStepPercentageSingleStatisticPoint>> pointLists = new HashMap<Class<? extends Move>, List<ImprovingStepPercentageSingleStatisticPoint>>();

    public ImprovingStepPercentageSingleStatistic() {
        this(1000L);
    }

    public ImprovingStepPercentageSingleStatistic(long timeMillisThresholdInterval) {
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        this.nextTimeMillisThreshold = timeMillisThresholdInterval;
    }

    public Map<Class<? extends Move>, List<ImprovingStepPercentageSingleStatisticPoint>> getPointLists() {
        return pointLists;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void open(Solver solver) {
        ((DefaultSolver) solver).addSolverPhaseLifecycleListener(listener);
    }

    @Override
    public void close(Solver solver) {
        ((DefaultSolver) solver).removeSolverPhaseLifecycleListener(listener);
    }

    private class ImprovingStepPercentageSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        private final Map<Class<? extends Move>, Integer> improvementCounts = new HashMap<Class<? extends Move>, Integer>();
        private final Map<Class<? extends Move>, Integer> totalCounts = new HashMap<Class<? extends Move>, Integer>();

        private void increaseByOne(Map<Class<? extends Move>, Integer> where, Class<? extends Move> what) {
            if (!where.containsKey(what)) {
                where.put(what, 1);
            } else {
                where.put(what, where.get(what) + 1);
            }
        }

        private void addPoint(Class<? extends Move> where, ImprovingStepPercentageSingleStatisticPoint what) {
            if (!pointLists.containsKey(where)) {
                pointLists.put(where, new ArrayList<ImprovingStepPercentageSingleStatisticPoint>());
            }
            pointLists.get(where).add(what);
        }

        /*
         * TODO moves should be counted not only by their type, but also by the variable they change.
         * this way, ChangeMove on different planning variables are merged together.
         */
        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            // update the statistic
            Move step = stepScope.getStep();
            Class<? extends Move> moveType = step.getClass();
            increaseByOne(totalCounts, moveType);
            if (stepScope.getBestScoreImproved()) { // TODO FIXME name implies it's step improving, not best score improving
                increaseByOne(improvementCounts, moveType);
            }
            // find out if we should record the current state
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            if (timeMillisSpend < nextTimeMillisThreshold) {
                return;
            }
            // record the state
            for (Map.Entry<Class<? extends Move>, Integer> entry : totalCounts.entrySet()) {
                Class<? extends Move> moveClass = entry.getKey();
                double improved = improvementCounts.containsKey(moveClass) ? improvementCounts.get(moveClass) : 0.0;
                double total = entry.getValue();
                double ratio = improved / total;
                addPoint(moveClass, new ImprovingStepPercentageSingleStatisticPoint(timeMillisSpend, ratio));
            }
            improvementCounts.clear();
            totalCounts.clear();
            // figure out when the next recording should happen
            nextTimeMillisThreshold += timeMillisThresholdInterval;
            if (nextTimeMillisThreshold < timeMillisSpend) {
                nextTimeMillisThreshold = timeMillisSpend;
            }
        }

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }

    }

}
