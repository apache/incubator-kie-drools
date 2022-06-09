package org.optaplanner.benchmark.impl;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;

class DefaultPlannerBenchmarkFactoryTest {

    @Test
    void validNameWithUnderscoreAndSpace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid_name with space_and_underscore");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        new DefaultPlannerBenchmarkFactory(config).validate();
    }

    @Test
    void validNameWithJapanese() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Valid name (有効名 in Japanese)");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        benchmarkFactory.validate();
    }

    @Test
    void invalidNameWithSlash() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("slash/name");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalStateException().isThrownBy(benchmarkFactory::validate);
    }

    @Test
    void invalidNameWithSuffixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName("Suffixed with space ");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalStateException().isThrownBy(benchmarkFactory::validate);
    }

    @Test
    void invalidNameWithPrefixWhitespace() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setName(" prefixed with space");
        config.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalStateException().isThrownBy(benchmarkFactory::validate);
    }

    @Test
    void noSolverConfigs() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setSolverBenchmarkConfigList(null);
        config.setSolverBenchmarkBluePrintConfigList(null);
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalArgumentException().isThrownBy(benchmarkFactory::validate);
    }

    @Test
    void nonUniqueSolverConfigName() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        final String sbcName = "x";
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        sbc1.setName(sbcName);
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        sbc2.setName(sbcName);
        config.setSolverBenchmarkConfigList(Arrays.asList(sbc1, sbc2));
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalStateException().isThrownBy(benchmarkFactory::generateSolverBenchmarkConfigNames);
    }

    @Test
    void uniqueNamesGenerated() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        SolverBenchmarkConfig sbc1 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc2 = new SolverBenchmarkConfig();
        SolverBenchmarkConfig sbc3 = new SolverBenchmarkConfig();
        sbc3.setName("Config_1");
        List<SolverBenchmarkConfig> configs = Arrays.asList(sbc1, sbc2, sbc3);
        config.setSolverBenchmarkConfigList(configs);
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        benchmarkFactory.generateSolverBenchmarkConfigNames();
        assertThat(sbc3.getName()).isEqualTo("Config_1");
        TreeSet<String> names = new TreeSet<>();
        for (SolverBenchmarkConfig sc : configs) {
            names.add(sc.getName());
        }
        for (int i = 0; i < configs.size(); i++) {
            assertThat(names).contains("Config_" + i);
        }
    }

    @Test
    void resolveParallelBenchmarkCountAutomatically() {
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(new PlannerBenchmarkConfig());
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(-1)).isEqualTo(1);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(0)).isEqualTo(1);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(1)).isEqualTo(1);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(2)).isEqualTo(1);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(3)).isEqualTo(2);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(4)).isEqualTo(2);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(5)).isEqualTo(3);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(6)).isEqualTo(4);
        assertThat(benchmarkFactory.resolveParallelBenchmarkCountAutomatically(17)).isEqualTo(9);
    }

    @Test
    void parallelBenchmarkDisabledByDefault() {
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(new PlannerBenchmarkConfig());
        assertThat(benchmarkFactory.resolveParallelBenchmarkCount()).isEqualTo(1);
    }

    @Test
    void resolvedParallelBenchmarkCountNegative() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setParallelBenchmarkCount("-1");
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThatIllegalArgumentException().isThrownBy(benchmarkFactory::resolveParallelBenchmarkCount);
    }

    @Test
    void calculateWarmUpTimeMillisSpentLimit() {
        PlannerBenchmarkConfig config = new PlannerBenchmarkConfig();
        config.setWarmUpHoursSpentLimit(1L);
        config.setWarmUpMinutesSpentLimit(2L);
        config.setWarmUpSecondsSpentLimit(5L);
        config.setWarmUpMillisecondsSpentLimit(753L);
        DefaultPlannerBenchmarkFactory benchmarkFactory = new DefaultPlannerBenchmarkFactory(config);
        assertThat(benchmarkFactory.calculateWarmUpTimeMillisSpentLimit()).isEqualTo(3_725_753L);
    }
}
