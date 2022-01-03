package org.optaplanner.benchmark.impl.statistic.bestscore;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class BestScoreSubSingleStatisticTest
        extends AbstractSubSingleStatisticTest<BestScoreStatisticPoint, BestScoreSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, BestScoreSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return BestScoreSubSingleStatistic::new;
    }

    @Override
    protected List<BestScoreStatisticPoint> getInputPoints() {
        return Collections.singletonList(new BestScoreStatisticPoint(Long.MAX_VALUE, SimpleScore.of(Integer.MAX_VALUE)));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<BestScoreStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> s.getScore().equals(SimpleScore.of(Integer.MAX_VALUE)), "Scores do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
