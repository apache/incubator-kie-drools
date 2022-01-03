package org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypestepscore;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class PickedMoveTypeStepScoreSubSingleStatisticTest
        extends
        AbstractSubSingleStatisticTest<PickedMoveTypeStepScoreDiffStatisticPoint, PickedMoveTypeStepScoreDiffSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, PickedMoveTypeStepScoreDiffSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return PickedMoveTypeStepScoreDiffSubSingleStatistic::new;
    }

    @Override
    protected List<PickedMoveTypeStepScoreDiffStatisticPoint> getInputPoints() {
        return Collections.singletonList(new PickedMoveTypeStepScoreDiffStatisticPoint(Long.MAX_VALUE, "SomeMoveType",
                SimpleScore.of(Integer.MAX_VALUE)));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<PickedMoveTypeStepScoreDiffStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> Objects.equals(s.getMoveType(), "SomeMoveType"), "Move types do not match.")
                .matches(s -> s.getStepScoreDiff().equals(SimpleScore.of(Integer.MAX_VALUE)), "Step score diffs do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
