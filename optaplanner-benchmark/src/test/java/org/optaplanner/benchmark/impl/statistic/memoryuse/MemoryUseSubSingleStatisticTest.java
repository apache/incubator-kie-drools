package org.optaplanner.benchmark.impl.statistic.memoryuse;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.assertj.core.api.SoftAssertions;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.AbstractSubSingleStatisticTest;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public final class MemoryUseSubSingleStatisticTest
        extends AbstractSubSingleStatisticTest<MemoryUseStatisticPoint, MemoryUseSubSingleStatistic<TestdataSolution>> {

    @Override
    protected Function<SubSingleBenchmarkResult, MemoryUseSubSingleStatistic<TestdataSolution>>
            getSubSingleStatisticConstructor() {
        return MemoryUseSubSingleStatistic::new;
    }

    @Override
    protected List<MemoryUseStatisticPoint> getInputPoints() {
        return Collections.singletonList(MemoryUseStatisticPoint.create(Long.MAX_VALUE));
    }

    @Override
    protected void runTest(SoftAssertions assertions, List<MemoryUseStatisticPoint> outputPoints) {
        assertions.assertThat(outputPoints)
                .hasSize(1)
                .first()
                .matches(s -> s.getUsedMemory() > 0, "Used memory not recorded.")
                .matches(s -> s.getMaxMemory() > 0, "Max memory not recorded.")
                .matches(s -> s.getTimeMillisSpent() == Long.MAX_VALUE, "Millis do not match.");
    }

}
