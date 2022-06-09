package org.optaplanner.benchmark.impl.report;

import java.time.ZoneId;
import java.util.Comparator;

import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.ranking.SolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.ranking.WorstScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.util.ConfigUtils;

public class BenchmarkReportFactory {

    private final BenchmarkReportConfig config;

    public BenchmarkReportFactory(BenchmarkReportConfig config) {
        this.config = config;
    }

    public BenchmarkReport buildBenchmarkReport(PlannerBenchmarkResult plannerBenchmark) {
        BenchmarkReport benchmarkReport = new BenchmarkReport(plannerBenchmark);
        benchmarkReport.setLocale(config.determineLocale());
        benchmarkReport.setTimezoneId(ZoneId.systemDefault());
        supplySolverRanking(benchmarkReport);
        return benchmarkReport;
    }

    protected void supplySolverRanking(BenchmarkReport benchmarkReport) {
        if (config.getSolverRankingType() != null && config.getSolverRankingComparatorClass() != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingType (" + config.getSolverRankingType()
                    + ") and a solverRankingComparatorClass (" + config.getSolverRankingComparatorClass().getName()
                    + ") at the same time.");
        } else if (config.getSolverRankingType() != null && config.getSolverRankingWeightFactoryClass() != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingType (" + config.getSolverRankingType()
                    + ") and a solverRankingWeightFactoryClass ("
                    + config.getSolverRankingWeightFactoryClass().getName() + ") at the same time.");
        } else if (config.getSolverRankingComparatorClass() != null && config.getSolverRankingWeightFactoryClass() != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingComparatorClass (" + config.getSolverRankingComparatorClass().getName()
                    + ") and a solverRankingWeightFactoryClass (" + config.getSolverRankingWeightFactoryClass().getName()
                    + ") at the same time.");
        }
        Comparator<SolverBenchmarkResult> solverRankingComparator = null;
        SolverRankingWeightFactory solverRankingWeightFactory = null;
        if (config.getSolverRankingType() != null) {
            switch (config.getSolverRankingType()) {
                case TOTAL_SCORE:
                    solverRankingComparator = new TotalScoreSolverRankingComparator();
                    break;
                case WORST_SCORE:
                    solverRankingComparator = new WorstScoreSolverRankingComparator();
                    break;
                case TOTAL_RANKING:
                    solverRankingWeightFactory = new TotalRankSolverRankingWeightFactory();
                    break;
                default:
                    throw new IllegalStateException("The solverRankingType ("
                            + config.getSolverRankingType() + ") is not implemented.");
            }
        }
        if (config.getSolverRankingComparatorClass() != null) {
            solverRankingComparator = ConfigUtils.newInstance(config,
                    "solverRankingComparatorClass", config.getSolverRankingComparatorClass());
        }
        if (config.getSolverRankingWeightFactoryClass() != null) {
            solverRankingWeightFactory = ConfigUtils.newInstance(config,
                    "solverRankingWeightFactoryClass", config.getSolverRankingWeightFactoryClass());
        }
        if (solverRankingComparator != null) {
            benchmarkReport.setSolverRankingComparator(solverRankingComparator);
        } else if (solverRankingWeightFactory != null) {
            benchmarkReport.setSolverRankingWeightFactory(solverRankingWeightFactory);
        } else {
            benchmarkReport.setSolverRankingComparator(new TotalScoreSolverRankingComparator());
        }
    }
}
