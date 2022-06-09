package org.optaplanner.benchmark.config.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.config.ranking.SolverRankingType;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;

class BenchmarkReportConfigTest {

    @Test
    void inheritBenchmarkReportConfig() {
        BenchmarkReportConfig inheritedReportConfig = new BenchmarkReportConfig();
        inheritedReportConfig.setLocale(Locale.CANADA);
        inheritedReportConfig.setSolverRankingType(SolverRankingType.TOTAL_RANKING);
        inheritedReportConfig.setSolverRankingComparatorClass(TotalScoreSolverRankingComparator.class);
        inheritedReportConfig.setSolverRankingWeightFactoryClass(TotalRankSolverRankingWeightFactory.class);

        BenchmarkReportConfig reportConfig = new BenchmarkReportConfig(inheritedReportConfig);

        assertThat(reportConfig.getLocale()).isEqualTo(inheritedReportConfig.getLocale());
        assertThat(reportConfig.getSolverRankingType()).isEqualTo(inheritedReportConfig.getSolverRankingType());
        assertThat(reportConfig.getSolverRankingComparatorClass())
                .isEqualTo(inheritedReportConfig.getSolverRankingComparatorClass());
        assertThat(reportConfig.getSolverRankingWeightFactoryClass())
                .isEqualTo(inheritedReportConfig.getSolverRankingWeightFactoryClass());
    }
}
