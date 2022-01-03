package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class BestSolutionMutationSubSingleStatisticTest
        extends
        AbstractSubSingleStatisticTest<BestSolutionMutationStatisticPoint, BestSolutionMutationSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, BestSolutionMutationSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return BestSolutionMutationSubSingleStatistic::new;
    }

    @Override
    protected List<BestSolutionMutationStatisticPoint> getInputPoints() {
        return Collections.singletonList(new BestSolutionMutationStatisticPoint(Long.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<BestSolutionMutationStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> s.getMutationCount() == Integer.MAX_VALUE, "Mutation counts do not match.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
