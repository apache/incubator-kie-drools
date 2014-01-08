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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.event.BestSolutionChangedEvent;
import org.optaplanner.core.impl.event.SolverEventListener;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class BestScoreSingleStatistic extends AbstractSingleStatistic<BestScoreSingleStatisticPoint> {

    private final BestScoreSingleStatisticListener listener = new BestScoreSingleStatisticListener();

    private List<BestScoreSingleStatisticPoint> pointList;

    public BestScoreSingleStatistic(SingleBenchmark singleBenchmark) {
        super(singleBenchmark, ProblemStatisticType.BEST_SCORE);
        pointList = new ArrayList<BestScoreSingleStatisticPoint>();
    }

    @Override
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
        writeCsvStatisticFile();
    }

    private class BestScoreSingleStatisticListener implements SolverEventListener {

        public void bestSolutionChanged(BestSolutionChangedEvent event) {
            pointList.add(new BestScoreSingleStatisticPoint(
                    event.getTimeMillisSpend(), event.getNewBestSolution().getScore()));
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected List<String> getCsvHeader() {
        return BestScoreSingleStatisticPoint.buildCsvLine("timeMillisSpend", "score");
    }

    @Override
    protected BestScoreSingleStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new BestScoreSingleStatisticPoint(Long.valueOf(csvLine.get(0)),
                scoreDefinition.parseScore(csvLine.get(1)));
    }

}
