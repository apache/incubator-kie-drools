/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.pickedmovetypebestscore;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationStatisticPoint;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepMeasurement;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepStatisticPoint;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreStatisticPoint;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.solution.mutation.MutationCounter;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;

@XStreamAlias("pickedMoveTypeBestScoreDiffSingleStatistic")
public class PickedMoveTypeBestScoreDiffSingleStatistic extends SingleStatistic<PickedMoveTypeBestScoreDiffStatisticPoint> {

    @XStreamOmitField
    private PickedMoveTypeBestScoreDiffSingleStatisticListener listener;

    @XStreamOmitField
    private List<PickedMoveTypeBestScoreDiffStatisticPoint> pointList;

    public PickedMoveTypeBestScoreDiffSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        super(singleBenchmarkResult, SingleStatisticType.PICKED_MOVE_TYPE_BEST_SCORE_DIFF);
        listener = new PickedMoveTypeBestScoreDiffSingleStatisticListener();
        pointList = new ArrayList<PickedMoveTypeBestScoreDiffStatisticPoint>();
    }

    public List<PickedMoveTypeBestScoreDiffStatisticPoint> getPointList() {
        return pointList;
    }

    @Override
    public void setPointList(List<PickedMoveTypeBestScoreDiffStatisticPoint> pointList) {
        this.pointList = pointList;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void open(Solver solver) {
        ((DefaultSolver) solver).addPhaseLifecycleListener(listener);
    }

    public void close(Solver solver) {
        ((DefaultSolver) solver).removePhaseLifecycleListener(listener);
    }

    private class PickedMoveTypeBestScoreDiffSingleStatisticListener extends PhaseLifecycleListenerAdapter {

        private Score oldBestScore = null;

        @Override
        public void phaseStarted(AbstractPhaseScope phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldBestScore = phaseScope.getBestScore();
            }
        }

        @Override
        public void phaseEnded(AbstractPhaseScope phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldBestScore = null;
            }
        }

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }

        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            if (stepScope.getBestScoreImproved()) {
                long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
                // TODO add support for CompositeMove's
                String moveType = stepScope.getStep().getClass().getSimpleName();
                Score newBestScore = stepScope.getScore();
                Score bestScoreDiff = newBestScore.subtract(oldBestScore);
                oldBestScore = newBestScore;
                pointList.add(new PickedMoveTypeBestScoreDiffStatisticPoint(
                        timeMillisSpent, moveType, bestScoreDiff));
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return PickedMoveTypeBestScoreDiffStatisticPoint.buildCsvLine("timeMillisSpent", "moveType", "bestScoreDiff");
    }

    @Override
    protected PickedMoveTypeBestScoreDiffStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new PickedMoveTypeBestScoreDiffStatisticPoint(Long.valueOf(csvLine.get(0)),
                csvLine.get(1), scoreDefinition.parseScore(csvLine.get(2)));
    }

}
