package org.optaplanner.benchmark.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.config.SolverBenchmarkConfig;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.BenchmarkReportFactory;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.solver.thread.DefaultSolverThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see PlannerBenchmarkFactory
 */
public class DefaultPlannerBenchmarkFactory extends PlannerBenchmarkFactory {

    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("(?U)^[\\w\\d _\\-\\.\\(\\)]+$");
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPlannerBenchmarkFactory.class);

    protected final PlannerBenchmarkConfig plannerBenchmarkConfig;

    public DefaultPlannerBenchmarkFactory(PlannerBenchmarkConfig plannerBenchmarkConfig) {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") cannot be null.");
        }
        this.plannerBenchmarkConfig = plannerBenchmarkConfig;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @return never null
     */
    @Override
    public PlannerBenchmark buildPlannerBenchmark() {
        return buildPlannerBenchmark(new Object[0]);
    }

    @Override
    @SafeVarargs
    public final <Solution_> PlannerBenchmark buildPlannerBenchmark(Solution_... problems) {
        validate();
        generateSolverBenchmarkConfigNames();
        List<SolverBenchmarkConfig> effectiveSolverBenchmarkConfigList = buildEffectiveSolverBenchmarkConfigList();
        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setName(plannerBenchmarkConfig.getName());
        plannerBenchmarkResult.setAggregation(false);
        int parallelBenchmarkCount = resolveParallelBenchmarkCount();
        plannerBenchmarkResult.setParallelBenchmarkCount(parallelBenchmarkCount);
        plannerBenchmarkResult
                .setWarmUpTimeMillisSpentLimit(Objects.requireNonNullElse(calculateWarmUpTimeMillisSpentLimit(), 30L));
        plannerBenchmarkResult.setUnifiedProblemBenchmarkResultList(new ArrayList<>());
        plannerBenchmarkResult.setSolverBenchmarkResultList(new ArrayList<>(effectiveSolverBenchmarkConfigList.size()));
        for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
            SolverBenchmarkFactory solverBenchmarkFactory = new SolverBenchmarkFactory(solverBenchmarkConfig);
            solverBenchmarkFactory.buildSolverBenchmark(plannerBenchmarkConfig.getClassLoader(), plannerBenchmarkResult,
                    problems);
        }

        BenchmarkReportConfig benchmarkReportConfig_ =
                plannerBenchmarkConfig.getBenchmarkReportConfig() == null ? new BenchmarkReportConfig()
                        : plannerBenchmarkConfig.getBenchmarkReportConfig();
        BenchmarkReport benchmarkReport =
                new BenchmarkReportFactory(benchmarkReportConfig_).buildBenchmarkReport(plannerBenchmarkResult);
        return new DefaultPlannerBenchmark(plannerBenchmarkResult, plannerBenchmarkConfig.getBenchmarkDirectory(),
                buildExecutorService(parallelBenchmarkCount), buildExecutorService(parallelBenchmarkCount), benchmarkReport);
    }

    private ExecutorService buildExecutorService(int parallelBenchmarkCount) {
        ThreadFactory threadFactory;
        if (plannerBenchmarkConfig.getThreadFactoryClass() != null) {
            threadFactory = ConfigUtils.newInstance(plannerBenchmarkConfig, "threadFactoryClass",
                    plannerBenchmarkConfig.getThreadFactoryClass());
        } else {
            threadFactory = new DefaultSolverThreadFactory("BenchmarkThread");
        }
        return Executors.newFixedThreadPool(parallelBenchmarkCount, threadFactory);
    }

    protected void validate() {
        if (plannerBenchmarkConfig.getName() != null) {
            if (!VALID_NAME_PATTERN.matcher(plannerBenchmarkConfig.getName()).matches()) {
                throw new IllegalStateException("The plannerBenchmark name (" + plannerBenchmarkConfig.getName()
                        + ") is invalid because it does not follow the nameRegex ("
                        + VALID_NAME_PATTERN.pattern() + ")" +
                        " which might cause an illegal filename.");
            }
            if (!plannerBenchmarkConfig.getName().trim().equals(plannerBenchmarkConfig.getName())) {
                throw new IllegalStateException("The plannerBenchmark name (" + plannerBenchmarkConfig.getName()
                        + ") is invalid because it starts or ends with whitespace.");
            }
        }
        if (ConfigUtils.isEmptyCollection(plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList())
                && ConfigUtils.isEmptyCollection(plannerBenchmarkConfig.getSolverBenchmarkConfigList())) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> (or 1 <solverBenchmarkBluePrint>)"
                            + " in the <plannerBenchmark> configuration.");
        }
    }

    protected void generateSolverBenchmarkConfigNames() {
        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() != null) {
            Set<String> nameSet = new HashSet<>(plannerBenchmarkConfig.getSolverBenchmarkConfigList().size());
            Set<SolverBenchmarkConfig> noNameBenchmarkConfigSet =
                    new LinkedHashSet<>(plannerBenchmarkConfig.getSolverBenchmarkConfigList().size());
            for (SolverBenchmarkConfig solverBenchmarkConfig : plannerBenchmarkConfig.getSolverBenchmarkConfigList()) {
                if (solverBenchmarkConfig.getName() != null) {
                    boolean unique = nameSet.add(solverBenchmarkConfig.getName());
                    if (!unique) {
                        throw new IllegalStateException("The benchmark name (" + solverBenchmarkConfig.getName()
                                + ") is used in more than 1 benchmark.");
                    }
                } else {
                    noNameBenchmarkConfigSet.add(solverBenchmarkConfig);
                }
            }
            int generatedNameIndex = 0;
            for (SolverBenchmarkConfig solverBenchmarkConfig : noNameBenchmarkConfigSet) {
                String generatedName = "Config_" + generatedNameIndex;
                while (nameSet.contains(generatedName)) {
                    generatedNameIndex++;
                    generatedName = "Config_" + generatedNameIndex;
                }
                solverBenchmarkConfig.setName(generatedName);
                generatedNameIndex++;
            }
        }
    }

    protected List<SolverBenchmarkConfig> buildEffectiveSolverBenchmarkConfigList() {
        List<SolverBenchmarkConfig> effectiveSolverBenchmarkConfigList = new ArrayList<>(0);
        if (plannerBenchmarkConfig.getSolverBenchmarkConfigList() != null) {
            effectiveSolverBenchmarkConfigList.addAll(plannerBenchmarkConfig.getSolverBenchmarkConfigList());
        }
        if (plannerBenchmarkConfig.getSolverBenchmarkBluePrintConfigList() != null) {
            for (SolverBenchmarkBluePrintConfig solverBenchmarkBluePrintConfig : plannerBenchmarkConfig
                    .getSolverBenchmarkBluePrintConfigList()) {
                effectiveSolverBenchmarkConfigList.addAll(solverBenchmarkBluePrintConfig.buildSolverBenchmarkConfigList());
            }
        }
        if (plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig() != null) {
            for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
                // Side effect: changes the unmarshalled solverBenchmarkConfig
                solverBenchmarkConfig.inherit(plannerBenchmarkConfig.getInheritedSolverBenchmarkConfig());
            }
        }
        return effectiveSolverBenchmarkConfigList;
    }

    protected int resolveParallelBenchmarkCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedParallelBenchmarkCount;
        if (plannerBenchmarkConfig.getParallelBenchmarkCount() == null) {
            resolvedParallelBenchmarkCount = 1;
        } else if (plannerBenchmarkConfig.getParallelBenchmarkCount()
                .equals(PlannerBenchmarkConfig.PARALLEL_BENCHMARK_COUNT_AUTO)) {
            resolvedParallelBenchmarkCount = resolveParallelBenchmarkCountAutomatically(availableProcessorCount);
        } else {
            resolvedParallelBenchmarkCount = ConfigUtils.resolvePoolSize("parallelBenchmarkCount",
                    plannerBenchmarkConfig.getParallelBenchmarkCount(), PlannerBenchmarkConfig.PARALLEL_BENCHMARK_COUNT_AUTO);
        }
        if (resolvedParallelBenchmarkCount < 1) {
            throw new IllegalArgumentException(
                    "The parallelBenchmarkCount (" + plannerBenchmarkConfig.getParallelBenchmarkCount()
                            + ") resulted in a resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                            + ") that is lower than 1.");
        }
        if (resolvedParallelBenchmarkCount > availableProcessorCount) {
            LOGGER.warn("Because the resolvedParallelBenchmarkCount ({}) is higher "
                    + "than the availableProcessorCount ({}), it is reduced to "
                    + "availableProcessorCount.", resolvedParallelBenchmarkCount, availableProcessorCount);
            resolvedParallelBenchmarkCount = availableProcessorCount;
        }
        return resolvedParallelBenchmarkCount;
    }

    protected int resolveParallelBenchmarkCountAutomatically(int availableProcessorCount) {
        // Tweaked based on experience
        if (availableProcessorCount <= 2) {
            return 1;
        } else if (availableProcessorCount <= 4) {
            return 2;
        } else {
            return (availableProcessorCount / 2) + 1;
        }
    }

    protected Long calculateWarmUpTimeMillisSpentLimit() {
        if (plannerBenchmarkConfig.getWarmUpMillisecondsSpentLimit() == null
                && plannerBenchmarkConfig.getWarmUpSecondsSpentLimit() == null
                && plannerBenchmarkConfig.getWarmUpMinutesSpentLimit() == null
                && plannerBenchmarkConfig.getWarmUpHoursSpentLimit() == null
                && plannerBenchmarkConfig.getWarmUpDaysSpentLimit() == null) {
            return null;
        }
        long warmUpTimeMillisSpentLimit = 0L;
        if (plannerBenchmarkConfig.getWarmUpMillisecondsSpentLimit() != null) {
            if (plannerBenchmarkConfig.getWarmUpMillisecondsSpentLimit() < 0L) {
                throw new IllegalArgumentException(
                        "The warmUpMillisecondsSpentLimit (" + plannerBenchmarkConfig.getWarmUpMillisecondsSpentLimit()
                                + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += plannerBenchmarkConfig.getWarmUpMillisecondsSpentLimit();
        }
        if (plannerBenchmarkConfig.getWarmUpSecondsSpentLimit() != null) {
            if (plannerBenchmarkConfig.getWarmUpSecondsSpentLimit() < 0L) {
                throw new IllegalArgumentException(
                        "The warmUpSecondsSpentLimit (" + plannerBenchmarkConfig.getWarmUpSecondsSpentLimit()
                                + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += plannerBenchmarkConfig.getWarmUpSecondsSpentLimit() * 1_000L;
        }
        if (plannerBenchmarkConfig.getWarmUpMinutesSpentLimit() != null) {
            if (plannerBenchmarkConfig.getWarmUpMinutesSpentLimit() < 0L) {
                throw new IllegalArgumentException(
                        "The warmUpMinutesSpentLimit (" + plannerBenchmarkConfig.getWarmUpMinutesSpentLimit()
                                + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += plannerBenchmarkConfig.getWarmUpMinutesSpentLimit() * 60_000L;
        }
        if (plannerBenchmarkConfig.getWarmUpHoursSpentLimit() != null) {
            if (plannerBenchmarkConfig.getWarmUpHoursSpentLimit() < 0L) {
                throw new IllegalArgumentException(
                        "The warmUpHoursSpentLimit (" + plannerBenchmarkConfig.getWarmUpHoursSpentLimit()
                                + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += plannerBenchmarkConfig.getWarmUpHoursSpentLimit() * 3_600_000L;
        }
        if (plannerBenchmarkConfig.getWarmUpDaysSpentLimit() != null) {
            if (plannerBenchmarkConfig.getWarmUpDaysSpentLimit() < 0L) {
                throw new IllegalArgumentException(
                        "The warmUpDaysSpentLimit (" + plannerBenchmarkConfig.getWarmUpDaysSpentLimit()
                                + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += plannerBenchmarkConfig.getWarmUpDaysSpentLimit() * 86_400_000L;
        }
        return warmUpTimeMillisSpentLimit;
    }
}
