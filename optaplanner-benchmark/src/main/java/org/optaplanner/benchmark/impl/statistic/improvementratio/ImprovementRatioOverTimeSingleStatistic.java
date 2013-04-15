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

package org.optaplanner.benchmark.impl.statistic.improvementratio;

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

public class ImprovementRatioOverTimeSingleStatistic extends AbstractSingleStatistic {

    private final long timeMillisThresholdInterval;
    private long nextTimeMillisThreshold;

    private final ImprovementRatioOverTimeSingleStatisticListener listener = new ImprovementRatioOverTimeSingleStatisticListener();

    private final Map<Class<? extends Move>, List<ImprovementRatioOverTimeSingleStatisticPoint>> pointLists = new HashMap<Class<? extends Move>, List<ImprovementRatioOverTimeSingleStatisticPoint>>();

    public ImprovementRatioOverTimeSingleStatistic() {
        this(1000L);
    }

    public ImprovementRatioOverTimeSingleStatistic(long timeMillisThresholdInterval) {
        if (timeMillisThresholdInterval <= 0L) {
            throw new IllegalArgumentException("The timeMillisThresholdInterval (" + timeMillisThresholdInterval
                    + ") must be bigger than 0.");
        }
        this.timeMillisThresholdInterval = timeMillisThresholdInterval;
        this.nextTimeMillisThreshold = timeMillisThresholdInterval;
    }

    public Map<Class<? extends Move>, List<ImprovementRatioOverTimeSingleStatisticPoint>> getPointLists() {
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

    private class ImprovementRatioOverTimeSingleStatisticListener extends SolverPhaseLifecycleListenerAdapter {

        private final Map<Class<? extends Move>, Integer> improvementCounts = new HashMap<Class<? extends Move>, Integer>();
        private final Map<Class<? extends Move>, Integer> totalCounts = new HashMap<Class<? extends Move>, Integer>();

        private void increaseByOne(Map<Class<? extends Move>, Integer> where, Class<? extends Move> what) {
            if (!where.containsKey(what)) {
                where.put(what, 1);
            } else {
                where.put(what, where.get(what) + 1);
            }
        }

        private void addPoint(Class<? extends Move> where, ImprovementRatioOverTimeSingleStatisticPoint what) {
            if (!pointLists.containsKey(where)) {
                pointLists.put(where, new ArrayList<ImprovementRatioOverTimeSingleStatisticPoint>());
            }
            pointLists.get(where).add(what);
        }

        /*
         * TODO moves should be counted not only by their type, but also by the variable they change.
         * this way, ChangeMove on different planning variables are merged together.
         */
        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            // update the statistic
            Move moveBeingDone = stepScope.getStep();
            Class<? extends Move> moveType = moveBeingDone.getClass();
            increaseByOne(totalCounts, moveType);
            if (stepScope.getBestScoreImproved()) {
                increaseByOne(improvementCounts, moveType);
            }
            // find out if we should record the current state
            long timeMillisSpend = stepScope.getPhaseScope().calculateSolverTimeMillisSpend();
            if (timeMillisSpend < ImprovementRatioOverTimeSingleStatistic.this.nextTimeMillisThreshold) {
                return;
            }
            // record the state
            for (Map.Entry<Class<? extends Move>, Integer> entry : totalCounts.entrySet()) {
                Class<? extends Move> type = entry.getKey();
                int total = entry.getValue();
                int improved = improvementCounts.containsKey(type) ? improvementCounts.get(type) : 0;
                long ratio = improved * 100 / total;
                addPoint(type, new ImprovementRatioOverTimeSingleStatisticPoint(timeMillisSpend, ratio));
            }
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
