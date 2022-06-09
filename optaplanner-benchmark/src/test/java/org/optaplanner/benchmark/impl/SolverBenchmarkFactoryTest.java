package org.optaplanner.benchmark.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.ProblemBenchmarksConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;

class SolverBenchmarkFactoryTest {

    @Test
    void validNameWithUnderscoreAndSpace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.setSubSingleCount(1);
        validateConfig(config);
    }

    @Test
    void validNameWithJapanese() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.setSubSingleCount(1);
        validateConfig(config);
    }

    @Test
    void invalidNameWithSlash() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("slash/name");
        config.setSubSingleCount(1);
        assertThatIllegalStateException().isThrownBy(() -> validateConfig(config));
    }

    @Test
    void invalidNameWithSuffixWhitespace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.setSubSingleCount(1);
        assertThatIllegalStateException().isThrownBy(() -> validateConfig(config));
    }

    @Test
    void invalidNameWithPrefixWhitespace() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName(" prefixed with space");
        config.setSubSingleCount(1);
        assertThatIllegalStateException().isThrownBy(() -> validateConfig(config));
    }

    @Test
    void validNonZeroSubSingleCount() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("name");
        config.setSubSingleCount(2);
        validateConfig(config);
    }

    @Test
    void validNullSubSingleCount() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("name");
        config.setSubSingleCount(null);
        validateConfig(config);
    }

    @Test
    void invalidZeroSubSingleCount() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("name");
        config.setSubSingleCount(0);
        assertThatIllegalStateException().isThrownBy(() -> validateConfig(config));
    }

    @Test
    void defaultStatisticsAreUsedIfNotPresent() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("name");
        config.setSubSingleCount(0);
        SolverBenchmarkFactory solverBenchmarkFactory = new SolverBenchmarkFactory(config);
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        assertThat(solverBenchmarkFactory.getSolverMetrics(problemBenchmarksConfig))
                .isEqualTo(List.of(SolverMetric.BEST_SCORE));
    }

    @Test
    void problemStatisticsAreUsedIfPresent() {
        SolverBenchmarkConfig config = new SolverBenchmarkConfig();
        config.setName("name");
        config.setSubSingleCount(0);
        SolverBenchmarkFactory solverBenchmarkFactory = new SolverBenchmarkFactory(config);
        ProblemBenchmarksConfig problemBenchmarksConfig = new ProblemBenchmarksConfig();
        problemBenchmarksConfig.setProblemStatisticTypeList(List.of(ProblemStatisticType.STEP_SCORE));
        problemBenchmarksConfig.setSingleStatisticTypeList(List.of(SingleStatisticType.CONSTRAINT_MATCH_TOTAL_BEST_SCORE));
        assertThat(solverBenchmarkFactory.getSolverMetrics(problemBenchmarksConfig))
                .isEqualTo(List.of(SolverMetric.STEP_SCORE, SolverMetric.CONSTRAINT_MATCH_TOTAL_BEST_SCORE));
    }

    private void validateConfig(SolverBenchmarkConfig config) {
        SolverBenchmarkFactory solverBenchmarkFactory = new SolverBenchmarkFactory(config);
        solverBenchmarkFactory.validate();
    }
}
