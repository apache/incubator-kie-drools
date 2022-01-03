package org.optaplanner.benchmark.impl.statistic.scorecalculationspeed;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class ScoreCalculationSpeedSubSingleStatisticTest
        extends
        AbstractSubSingleStatisticTest<ScoreCalculationSpeedStatisticPoint, ScoreCalculationSpeedSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, ScoreCalculationSpeedSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return ScoreCalculationSpeedSubSingleStatistic::new;
    }

    @Override
    protected List<ScoreCalculationSpeedStatisticPoint> getInputPoints() {
        return Collections.singletonList(new ScoreCalculationSpeedStatisticPoint(Long.MAX_VALUE, Long.MAX_VALUE));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<ScoreCalculationSpeedStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> s.getScoreCalculationSpeed() == Long.MAX_VALUE, "Score calculation speeds do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
