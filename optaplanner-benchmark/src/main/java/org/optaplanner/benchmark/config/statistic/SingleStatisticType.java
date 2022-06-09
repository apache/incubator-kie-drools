package org.optaplanner.benchmark.config.statistic;

import javax.xml.bind.annotation.XmlEnum;

import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.benchmark.impl.statistic.subsingle.constraintmatchtotalbestscore.ConstraintMatchTotalBestScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.subsingle.constraintmatchtotalstepscore.ConstraintMatchTotalStepScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypestepscore.PickedMoveTypeStepScoreDiffSubSingleStatistic;

@XmlEnum
public enum SingleStatisticType implements StatisticType {
    CONSTRAINT_MATCH_TOTAL_BEST_SCORE,
    CONSTRAINT_MATCH_TOTAL_STEP_SCORE,
    PICKED_MOVE_TYPE_BEST_SCORE_DIFF,
    PICKED_MOVE_TYPE_STEP_SCORE_DIFF;

    public PureSubSingleStatistic buildPureSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        // Keep in sync with ProblemStatistic XStreamInclude list
        switch (this) {
            case CONSTRAINT_MATCH_TOTAL_BEST_SCORE:
                return new ConstraintMatchTotalBestScoreSubSingleStatistic(subSingleBenchmarkResult);
            case CONSTRAINT_MATCH_TOTAL_STEP_SCORE:
                return new ConstraintMatchTotalStepScoreSubSingleStatistic(subSingleBenchmarkResult);
            case PICKED_MOVE_TYPE_BEST_SCORE_DIFF:
                return new PickedMoveTypeBestScoreDiffSubSingleStatistic(subSingleBenchmarkResult);
            case PICKED_MOVE_TYPE_STEP_SCORE_DIFF:
                return new PickedMoveTypeStepScoreDiffSubSingleStatistic(subSingleBenchmarkResult);
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
