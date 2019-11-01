package org.optaplanner.benchmark.config.report;

import java.util.Locale;

import org.junit.Test;
import org.optaplanner.benchmark.config.ranking.SolverRankingType;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BenchmarkReportConfigTest {

    @Test
    public void inheritBenchmarkReportConfig() {
        BenchmarkReportConfig inheritedReportConfig = new BenchmarkReportConfig();
        inheritedReportConfig.setLocale(Locale.CANADA);
        inheritedReportConfig.setSolverRankingType(SolverRankingType.TOTAL_RANKING);
        inheritedReportConfig.setSolverRankingComparatorClass(TotalScoreSolverRankingComparator.class);
        inheritedReportConfig.setSolverRankingWeightFactoryClass(TotalRankSolverRankingWeightFactory.class);

        BenchmarkReportConfig reportConfig = new BenchmarkReportConfig(inheritedReportConfig);

        assertThat(reportConfig.getLocale()).isEqualTo(inheritedReportConfig.getLocale());
        assertThat(reportConfig.getSolverRankingType()).isEqualTo(inheritedReportConfig.getSolverRankingType());
        assertThat(reportConfig.getSolverRankingComparatorClass()).isEqualTo(inheritedReportConfig.getSolverRankingComparatorClass());
        assertThat(reportConfig.getSolverRankingWeightFactoryClass()).isEqualTo(inheritedReportConfig.getSolverRankingWeightFactoryClass());
    }

    @Test
    public void buildWithSolverRankingTypeAndSolverRankingComparatorClass() {
        BenchmarkReportConfig config = new BenchmarkReportConfig();
        config.setSolverRankingType(SolverRankingType.TOTAL_RANKING);
        config.setSolverRankingComparatorClass(TotalScoreSolverRankingComparator.class);

        PlannerBenchmarkResult result = mock(PlannerBenchmarkResult.class);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> config.buildBenchmarkReport(result))
                .withMessageContaining("solverRankingType").withMessageContaining("solverRankingComparatorClass");
    }

    @Test
    public void buildWithSolverRankingTypeAndSolverRankingWeightFactoryClass() {
        BenchmarkReportConfig config = new BenchmarkReportConfig();
        config.setSolverRankingType(SolverRankingType.TOTAL_RANKING);
        config.setSolverRankingWeightFactoryClass(TotalRankSolverRankingWeightFactory.class);

        PlannerBenchmarkResult result = mock(PlannerBenchmarkResult.class);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> config.buildBenchmarkReport(result))
                .withMessageContaining("solverRankingType").withMessageContaining("solverRankingWeightFactoryClass");
    }

    @Test
    public void buildWithSolverRankingComparatorClassAndSolverRankingWeightFactoryClass() {
        BenchmarkReportConfig config = new BenchmarkReportConfig();
        config.setSolverRankingComparatorClass(TotalScoreSolverRankingComparator.class);
        config.setSolverRankingWeightFactoryClass(TotalRankSolverRankingWeightFactory.class);

        PlannerBenchmarkResult result = mock(PlannerBenchmarkResult.class);

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> config.buildBenchmarkReport(result))
                .withMessageContaining("solverRankingComparatorClass").withMessageContaining("solverRankingWeightFactoryClass");
    }
}
