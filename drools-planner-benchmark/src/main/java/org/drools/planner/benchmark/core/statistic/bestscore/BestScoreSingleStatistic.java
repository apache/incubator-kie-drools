/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.benchmark.core.statistic.bestscore;

import java.util.ArrayList;
import java.util.List;

import org.drools.planner.benchmark.core.statistic.AbstractSingleStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.event.BestSolutionChangedEvent;
import org.drools.planner.core.event.SolverEventListener;

public class BestScoreSingleStatistic extends AbstractSingleStatistic {

    private final BestScoreSingleStatisticListener listener = new BestScoreSingleStatisticListener();

    private List<BestScoreSingleStatisticPoint> pointList = new ArrayList<BestScoreSingleStatisticPoint>();

    public List<BestScoreSingleStatisticPoint> getPointList() {
        return pointList;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void open(Solver solver) {
        solver.addEventListener(listener);
    }

    public void close(Solver solver) {
        solver.removeEventListener(listener);
    }

    private class BestScoreSingleStatisticListener implements SolverEventListener {

        public void bestSolutionChanged(BestSolutionChangedEvent event) {
            pointList.add(new BestScoreSingleStatisticPoint(event));
        }

    }

}
