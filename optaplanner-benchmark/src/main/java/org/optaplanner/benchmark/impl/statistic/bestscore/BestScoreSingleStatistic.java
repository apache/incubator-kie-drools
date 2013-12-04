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

package org.optaplanner.benchmark.impl.statistic.bestscore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.event.SolverEventListener;

public class BestScoreSingleStatistic extends AbstractSingleStatistic {

    private final BestScoreSingleStatisticListener listener = new BestScoreSingleStatisticListener();

    private List<BestScoreSingleStatisticPoint> pointList = new ArrayList<BestScoreSingleStatisticPoint>();

    public List<BestScoreSingleStatisticPoint> getPointList() {
        return pointList;
    }

    public void setPointList(List<BestScoreSingleStatisticPoint> pointList) {
        this.pointList = pointList;
    }

    public void writeCsvStatistic(File outputFile) {
        SingleStatisticCsv csv = new SingleStatisticCsv();
        for (BestScoreSingleStatisticPoint point : pointList) {
            long timeMillisSpend = point.getTimeMillisSpend();
            Score score = point.getScore();
            if (score != null) {
                csv.addPoint(timeMillisSpend, score.toString());
            }
        }
        csv.writeCsvSingleStatisticFile(outputFile);
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
            pointList.add(new BestScoreSingleStatisticPoint(
                    event.getTimeMillisSpend(), event.getNewBestSolution().getScore()));
        }

    }

}
