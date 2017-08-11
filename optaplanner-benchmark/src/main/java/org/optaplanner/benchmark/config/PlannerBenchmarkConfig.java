/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.solver.thread.DefaultSolverThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.ObjectUtils.*;

@XStreamAlias("plannerBenchmark")
public class PlannerBenchmarkConfig {

    public static final String PARALLEL_BENCHMARK_COUNT_AUTO = "AUTO";
    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("(?U)^[\\w\\d _\\-\\.\\(\\)]+$");

    private static final Logger logger = LoggerFactory.getLogger(PlannerBenchmarkConfig.class);

    private String name = null;
    private File benchmarkDirectory = null;

    private Class<? extends ThreadFactory> threadFactoryClass = null;
    private String parallelBenchmarkCount = null;
    private Long warmUpMillisecondsSpentLimit = null;
    private Long warmUpSecondsSpentLimit = null;
    private Long warmUpMinutesSpentLimit = null;
    private Long warmUpHoursSpentLimit = null;
    private Long warmUpDaysSpentLimit = null;

    @XStreamAlias("benchmarkReport")
    private BenchmarkReportConfig benchmarkReportConfig = null;

    @XStreamAlias("inheritedSolverBenchmark")
    private SolverBenchmarkConfig inheritedSolverBenchmarkConfig = null;

    @XStreamImplicit(itemFieldName = "solverBenchmarkBluePrint")
    private List<SolverBenchmarkBluePrintConfig> solverBenchmarkBluePrintConfigList = null;
    @XStreamImplicit(itemFieldName = "solverBenchmark")
    private List<SolverBenchmarkConfig> solverBenchmarkConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getBenchmarkDirectory() {
        return benchmarkDirectory;
    }

    public void setBenchmarkDirectory(File benchmarkDirectory) {
        this.benchmarkDirectory = benchmarkDirectory;
    }

    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    public void setThreadFactoryClass(Class<? extends ThreadFactory> threadFactoryClass) {
        this.threadFactoryClass = threadFactoryClass;
    }

    /**
     * Using multiple parallel benchmarks can decrease the reliability of the results.
     * <p>
     * If there aren't enough processors available, it will be decreased.
     * @return null, a number, {@value #PARALLEL_BENCHMARK_COUNT_AUTO} or a JavaScript calculation using
     * {@value org.optaplanner.core.config.util.ConfigUtils#AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getParallelBenchmarkCount() {
        return parallelBenchmarkCount;
    }

    public void setParallelBenchmarkCount(String parallelBenchmarkCount) {
        this.parallelBenchmarkCount = parallelBenchmarkCount;
    }

    public Long getWarmUpMillisecondsSpentLimit() {
        return warmUpMillisecondsSpentLimit;
    }

    public void setWarmUpMillisecondsSpentLimit(Long warmUpMillisecondsSpentLimit) {
        this.warmUpMillisecondsSpentLimit = warmUpMillisecondsSpentLimit;
    }

    public Long getWarmUpSecondsSpentLimit() {
        return warmUpSecondsSpentLimit;
    }

    public void setWarmUpSecondsSpentLimit(Long warmUpSecondsSpentLimit) {
        this.warmUpSecondsSpentLimit = warmUpSecondsSpentLimit;
    }

    public Long getWarmUpMinutesSpentLimit() {
        return warmUpMinutesSpentLimit;
    }

    public void setWarmUpMinutesSpentLimit(Long warmUpMinutesSpentLimit) {
        this.warmUpMinutesSpentLimit = warmUpMinutesSpentLimit;
    }

    public Long getWarmUpHoursSpentLimit() {
        return warmUpHoursSpentLimit;
    }

    public void setWarmUpHoursSpentLimit(Long warmUpHoursSpentLimit) {
        this.warmUpHoursSpentLimit = warmUpHoursSpentLimit;
    }

    public Long getWarmUpDaysSpentLimit() {
        return warmUpDaysSpentLimit;
    }

    public void setWarmUpDaysSpentLimit(Long warmUpDaysSpentLimit) {
        this.warmUpDaysSpentLimit = warmUpDaysSpentLimit;
    }

    public BenchmarkReportConfig getBenchmarkReportConfig() {
        return benchmarkReportConfig;
    }

    public void setBenchmarkReportConfig(BenchmarkReportConfig benchmarkReportConfig) {
        this.benchmarkReportConfig = benchmarkReportConfig;
    }

    public SolverBenchmarkConfig getInheritedSolverBenchmarkConfig() {
        return inheritedSolverBenchmarkConfig;
    }

    public void setInheritedSolverBenchmarkConfig(SolverBenchmarkConfig inheritedSolverBenchmarkConfig) {
        this.inheritedSolverBenchmarkConfig = inheritedSolverBenchmarkConfig;
    }

    public List<SolverBenchmarkBluePrintConfig> getSolverBenchmarkBluePrintConfigList() {
        return solverBenchmarkBluePrintConfigList;
    }

    public void setSolverBenchmarkBluePrintConfigList(List<SolverBenchmarkBluePrintConfig> solverBenchmarkBluePrintConfigList) {
        this.solverBenchmarkBluePrintConfigList = solverBenchmarkBluePrintConfigList;
    }

    public List<SolverBenchmarkConfig> getSolverBenchmarkConfigList() {
        return solverBenchmarkConfigList;
    }

    public void setSolverBenchmarkConfigList(List<SolverBenchmarkConfig> solverBenchmarkConfigList) {
        this.solverBenchmarkConfigList = solverBenchmarkConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public PlannerBenchmark buildPlannerBenchmark() {
        return buildPlannerBenchmark(new SolverConfigContext());
    }

    public PlannerBenchmark buildPlannerBenchmark(SolverConfigContext solverConfigContext) {
        return buildPlannerBenchmark(solverConfigContext, new Object[0]);
    }

    public <Solution_> PlannerBenchmark buildPlannerBenchmark(SolverConfigContext solverConfigContext,
            Solution_[] extraProblems) {
        validate();
        generateSolverBenchmarkConfigNames();
        List<SolverBenchmarkConfig> effectiveSolverBenchmarkConfigList = buildEffectiveSolverBenchmarkConfigList();

        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setName(name);
        plannerBenchmarkResult.setAggregation(false);
        int parallelBenchmarkCount = resolveParallelBenchmarkCount();
        plannerBenchmarkResult.setParallelBenchmarkCount(parallelBenchmarkCount);
        plannerBenchmarkResult.setWarmUpTimeMillisSpentLimit(defaultIfNull(calculateWarmUpTimeMillisSpentLimit(), 30L));
        plannerBenchmarkResult.setUnifiedProblemBenchmarkResultList(new ArrayList<>());
        plannerBenchmarkResult.setSolverBenchmarkResultList(new ArrayList<>(
                effectiveSolverBenchmarkConfigList.size()));
        for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
            solverBenchmarkConfig.buildSolverBenchmark(solverConfigContext, plannerBenchmarkResult, extraProblems);
        }

        BenchmarkReportConfig benchmarkReportConfig_ = benchmarkReportConfig == null ? new BenchmarkReportConfig()
                : benchmarkReportConfig;
        BenchmarkReport benchmarkReport = benchmarkReportConfig_.buildBenchmarkReport(plannerBenchmarkResult);
        return new DefaultPlannerBenchmark(
                plannerBenchmarkResult, solverConfigContext, benchmarkDirectory,
                buildExecutorService(parallelBenchmarkCount), buildExecutorService(parallelBenchmarkCount),
                benchmarkReport);
    }

    private ExecutorService buildExecutorService(int parallelBenchmarkCount) {
        ThreadFactory threadFactory;
        if (threadFactoryClass != null) {
            threadFactory = ConfigUtils.newInstance(this, "threadFactoryClass", threadFactoryClass);
        } else {
            threadFactory = new DefaultSolverThreadFactory("BenchmarkThread");
        }
        return Executors.newFixedThreadPool(parallelBenchmarkCount, threadFactory);
    }

    protected void validate() {
        if (name != null) {
            if (!PlannerBenchmarkConfig.VALID_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalStateException("The plannerBenchmark name (" + name
                        + ") is invalid because it does not follow the nameRegex ("
                        + PlannerBenchmarkConfig.VALID_NAME_PATTERN.pattern() + ")" +
                        " which might cause an illegal filename.");
            }
            if (!name.trim().equals(name)) {
                throw new IllegalStateException("The plannerBenchmark name (" + name
                        + ") is invalid because it starts or ends with whitespace.");
            }
        }
        if (ConfigUtils.isEmptyCollection(solverBenchmarkBluePrintConfigList)
                && ConfigUtils.isEmptyCollection(solverBenchmarkConfigList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> (or 1 <solverBenchmarkBluePrint>)"
                    + " in the <plannerBenchmark> configuration.");
        }
    }

    protected void generateSolverBenchmarkConfigNames() {
        if (solverBenchmarkConfigList != null) {
            Set<String> nameSet = new HashSet<>(solverBenchmarkConfigList.size());
            Set<SolverBenchmarkConfig> noNameBenchmarkConfigSet = new LinkedHashSet<>(solverBenchmarkConfigList.size());
            for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
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
        if (solverBenchmarkConfigList != null) {
            effectiveSolverBenchmarkConfigList.addAll(solverBenchmarkConfigList);
        }
        if (solverBenchmarkBluePrintConfigList != null) {
            for (SolverBenchmarkBluePrintConfig solverBenchmarkBluePrintConfig : solverBenchmarkBluePrintConfigList) {
                effectiveSolverBenchmarkConfigList.addAll(
                        solverBenchmarkBluePrintConfig.buildSolverBenchmarkConfigList());
            }
        }
        if (inheritedSolverBenchmarkConfig != null) {
            for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
                // Side effect: changes the unmarshalled solverBenchmarkConfig
                solverBenchmarkConfig.inherit(inheritedSolverBenchmarkConfig);
            }
        }
        return effectiveSolverBenchmarkConfigList;
    }

    protected int resolveParallelBenchmarkCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedParallelBenchmarkCount;
        if (parallelBenchmarkCount == null) {
            resolvedParallelBenchmarkCount = 1;
        } else if (parallelBenchmarkCount.equals(PARALLEL_BENCHMARK_COUNT_AUTO)) {
            resolvedParallelBenchmarkCount = resolveParallelBenchmarkCountAutomatically(availableProcessorCount);
        } else {
            resolvedParallelBenchmarkCount = ConfigUtils.resolveThreadPoolSizeScript(
                    "parallelBenchmarkCount", parallelBenchmarkCount, PARALLEL_BENCHMARK_COUNT_AUTO);
        }
        if (resolvedParallelBenchmarkCount < 1) {
            throw new IllegalArgumentException("The parallelBenchmarkCount (" + parallelBenchmarkCount
                    + ") resulted in a resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                    + ") that is lower than 1.");
        }
        if (resolvedParallelBenchmarkCount > availableProcessorCount) {
            logger.warn("Because the resolvedParallelBenchmarkCount ({}) is higher "
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
        if (warmUpMillisecondsSpentLimit == null && warmUpSecondsSpentLimit == null
                && warmUpMinutesSpentLimit == null && warmUpHoursSpentLimit == null && warmUpDaysSpentLimit == null) {
            return null;
        }
        long warmUpTimeMillisSpentLimit = 0L;
        if (warmUpMillisecondsSpentLimit != null) {
            if (warmUpMillisecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpMillisecondsSpentLimit (" + warmUpMillisecondsSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpMillisecondsSpentLimit;
        }
        if (warmUpSecondsSpentLimit != null) {
            if (warmUpSecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpSecondsSpentLimit (" + warmUpSecondsSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpSecondsSpentLimit * 1_000L;
        }
        if (warmUpMinutesSpentLimit != null) {
            if (warmUpMinutesSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpMinutesSpentLimit (" + warmUpMinutesSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpMinutesSpentLimit * 60_000L;
        }
        if (warmUpHoursSpentLimit != null) {
            if (warmUpHoursSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpHoursSpentLimit (" + warmUpHoursSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpHoursSpentLimit * 3_600_000L;
        }
        if (warmUpDaysSpentLimit != null) {
            if (warmUpDaysSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpDaysSpentLimit (" + warmUpDaysSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpDaysSpentLimit * 86_400_000L;
        }
        return warmUpTimeMillisSpentLimit;
    }

}
