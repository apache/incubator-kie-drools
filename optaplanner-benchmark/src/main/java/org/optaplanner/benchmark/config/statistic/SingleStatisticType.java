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

package org.optaplanner.benchmark.config.statistic;

import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.benchmark.impl.result.SolverProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalbestscore.ConstraintMatchTotalBestScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalstepscore.ConstraintMatchTotalStepScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypestepscore.PickedMoveTypeStepScoreDiffSubSingleStatistic;

public enum SingleStatisticType implements StatisticType {
    CONSTRAINT_MATCH_TOTAL_BEST_SCORE,
    CONSTRAINT_MATCH_TOTAL_STEP_SCORE,
    PICKED_MOVE_TYPE_BEST_SCORE_DIFF,
    PICKED_MOVE_TYPE_STEP_SCORE_DIFF;

    public PureSubSingleStatistic buildPureSingleStatistic(SolverProblemBenchmarkResult solverProblemBenchmarkResult) {
        // Keep in sync with ProblemStatistic XStreamInclude list
        switch (this) {
            case CONSTRAINT_MATCH_TOTAL_BEST_SCORE:
                return new ConstraintMatchTotalBestScoreSubSingleStatistic(solverProblemBenchmarkResult);
            case CONSTRAINT_MATCH_TOTAL_STEP_SCORE:
                return new ConstraintMatchTotalStepScoreSubSingleStatistic(solverProblemBenchmarkResult);
            case PICKED_MOVE_TYPE_BEST_SCORE_DIFF:
                return new PickedMoveTypeBestScoreDiffSubSingleStatistic(solverProblemBenchmarkResult);
            case PICKED_MOVE_TYPE_STEP_SCORE_DIFF:
                return new PickedMoveTypeStepScoreDiffSubSingleStatistic(solverProblemBenchmarkResult);
            default:
                throw new IllegalStateException("The singleStatisticType (" + this + ") is not implemented.");
        }
    }

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(name());
    }

    public boolean hasScoreLevels() {
        return this == CONSTRAINT_MATCH_TOTAL_BEST_SCORE
                || this == CONSTRAINT_MATCH_TOTAL_STEP_SCORE
                || this == PICKED_MOVE_TYPE_BEST_SCORE_DIFF
                || this == PICKED_MOVE_TYPE_STEP_SCORE_DIFF;
    }

}
