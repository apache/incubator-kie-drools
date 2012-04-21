/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.benchmark.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.drools.planner.benchmark.api.PlannerBenchmark;
import org.drools.planner.benchmark.core.DefaultPlannerBenchmark;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("plannerBenchmark")
public class PlannerBenchmarkConfig {
    
    private final Logger logger = LoggerFactory.getLogger(PlannerBenchmarkConfig.class);

    private File benchmarkDirectory = null;
    private File benchmarkInstanceDirectory = null;
    private File outputSolutionFilesDirectory = null;
    private File statisticDirectory = null;
    private Comparator<SolverBenchmark> solverBenchmarkComparator = null;

    private Long warmUpTimeMillisSpend = null;
    private Long warmUpSecondsSpend = null;
    private Long warmUpMinutesSpend = null;
    private Long warmUpHoursSpend = null;

    private String threadsUse = null;

    @XStreamAlias("inheritedSolverBenchmark")
    private SolverBenchmarkConfig inheritedSolverBenchmarkConfig = null;

    @XStreamImplicit(itemFieldName = "solverBenchmark")
    private List<SolverBenchmarkConfig> solverBenchmarkConfigList = null;

    public File getBenchmarkDirectory() {
        return benchmarkDirectory;
    }

    public void setBenchmarkDirectory(File benchmarkDirectory) {
        this.benchmarkDirectory = benchmarkDirectory;
    }

    public File getBenchmarkInstanceDirectory() {
        return benchmarkInstanceDirectory;
    }

    public void setBenchmarkInstanceDirectory(File benchmarkInstanceDirectory) {
        this.benchmarkInstanceDirectory = benchmarkInstanceDirectory;
    }

    public File getOutputSolutionFilesDirectory() {
        return outputSolutionFilesDirectory;
    }

    public void setOutputSolutionFilesDirectory(File outputSolutionFilesDirectory) {
        this.outputSolutionFilesDirectory = outputSolutionFilesDirectory;
    }

    public File getStatisticDirectory() {
        return statisticDirectory;
    }

    public void setStatisticDirectory(File statisticDirectory) {
        this.statisticDirectory = statisticDirectory;
    }

    public Comparator<SolverBenchmark> getSolverBenchmarkComparator() {
        return solverBenchmarkComparator;
    }

    public void setSolverBenchmarkComparator(Comparator<SolverBenchmark> solverBenchmarkComparator) {
        this.solverBenchmarkComparator = solverBenchmarkComparator;
    }

    public Long getWarmUpTimeMillisSpend() {
        return warmUpTimeMillisSpend;
    }

    public void setWarmUpTimeMillisSpend(Long warmUpTimeMillisSpend) {
        this.warmUpTimeMillisSpend = warmUpTimeMillisSpend;
    }

    public Long getWarmUpSecondsSpend() {
        return warmUpSecondsSpend;
    }

    public void setWarmUpSecondsSpend(Long warmUpSecondsSpend) {
        this.warmUpSecondsSpend = warmUpSecondsSpend;
    }

    public Long getWarmUpMinutesSpend() {
        return warmUpMinutesSpend;
    }

    public void setWarmUpMinutesSpend(Long warmUpMinutesSpend) {
        this.warmUpMinutesSpend = warmUpMinutesSpend;
    }

    public Long getWarmUpHoursSpend() {
        return warmUpHoursSpend;
    }

    public void setWarmUpHoursSpend(Long warmUpHoursSpend) {
        this.warmUpHoursSpend = warmUpHoursSpend;
    }

    public SolverBenchmarkConfig getInheritedSolverBenchmarkConfig() {
        return inheritedSolverBenchmarkConfig;
    }

    public void setInheritedSolverBenchmarkConfig(SolverBenchmarkConfig inheritedSolverBenchmarkConfig) {
        this.inheritedSolverBenchmarkConfig = inheritedSolverBenchmarkConfig;
    }

    public List<SolverBenchmarkConfig> getSolverBenchmarkConfigList() {
        return solverBenchmarkConfigList;
    }

    public void setSolverBenchmarkConfigList(List<SolverBenchmarkConfig> solverBenchmarkConfigList) {
        this.solverBenchmarkConfigList = solverBenchmarkConfigList;
    }

    public String getThreadsUse() {
        return threadsUse;
    }

    public void setThreadsUse(String threads) {
        threadsUse = threads;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    private void validate() {
        if (solverBenchmarkConfigList == null || solverBenchmarkConfigList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> in the <plannerBenchmark> configuration.");
        }
    }
    
    private ExecutorService getExecutor() {
        int threadCount = this.getExecutorThreadCount();
        logger.debug("Benchmarking will use (" + threadCount + ") threads.");
        return Executors.newFixedThreadPool(threadCount);
    }

    private int getExecutorThreadCount() {
        if (getThreadsUse() == null) {
            // no threads are requested; use just one
            return 1;
        } else if (getThreadsUse().equals("AUTO")) {
            // "AUTO" threads are requested; use everything possible, leave some for the operating system
            int availableThreads = Runtime.getRuntime().availableProcessors();
            int useThreads = Math.max(1, availableThreads - 2); // prevent 0
            return useThreads;
        } else {
            // otherwise, we expect a number
            try {
                int numThreads = Integer.valueOf(getThreadsUse());
                if (numThreads < 1) {
                    throw new IllegalStateException("Number of threads must not be smaller than 1.");
                }
                return numThreads;
            } catch (Exception ex) {
                throw new IllegalStateException("Requested (" + getThreadsUse() + ") threads. Please use a positive integer or 'AUTO'.");
            }
        }
    }

    private void generateSolverBenchmarkConfigNames() {
        Set<String> nameSet = new HashSet<String>(solverBenchmarkConfigList.size());
        Set<SolverBenchmarkConfig> noNameBenchmarkConfigSet = new LinkedHashSet<SolverBenchmarkConfig>(solverBenchmarkConfigList.size());
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

    private void inherit() {
        if (inheritedSolverBenchmarkConfig != null) {
            for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
                solverBenchmarkConfig.inherit(inheritedSolverBenchmarkConfig);
            }
        }
    }

    public PlannerBenchmark buildPlannerBenchmark() {
        validate();
        generateSolverBenchmarkConfigNames();
        inherit();

        DefaultPlannerBenchmark plannerBenchmark = new DefaultPlannerBenchmark();
        plannerBenchmark.setBenchmarkDirectory(benchmarkDirectory);
        plannerBenchmark.setBenchmarkInstanceDirectory(benchmarkInstanceDirectory);
        plannerBenchmark.setOutputSolutionFilesDirectory(outputSolutionFilesDirectory);
        plannerBenchmark.setStatisticDirectory(statisticDirectory);
        plannerBenchmark.setSolverBenchmarkComparator(solverBenchmarkComparator);
        plannerBenchmark.setWarmUpTimeMillisSpend(calculateWarmUpTimeMillisSpendTotal());

        ExecutorService executor = getExecutor();
        List<SolverBenchmark> solverBenchmarkList = new ArrayList<SolverBenchmark>(solverBenchmarkConfigList.size());
        List<ProblemBenchmark> unifiedProblemBenchmarkList = new ArrayList<ProblemBenchmark>();
        for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
            SolverBenchmark solverBenchmark = solverBenchmarkConfig.buildSolverBenchmark(
                    unifiedProblemBenchmarkList, executor);
            solverBenchmarkList.add(solverBenchmark);
        }
        plannerBenchmark.setSolverBenchmarkList(solverBenchmarkList);
        plannerBenchmark.setUnifiedProblemBenchmarkList(unifiedProblemBenchmarkList);
        return plannerBenchmark;
    }

    public Long calculateWarmUpTimeMillisSpendTotal() {
        if (warmUpTimeMillisSpend == null && warmUpSecondsSpend == null && warmUpMinutesSpend == null
                && warmUpHoursSpend == null) {
            return null;
        }
        long warmUpTimeMillisSpendTotal = 0L;
        if (warmUpTimeMillisSpend != null) {
            warmUpTimeMillisSpendTotal += warmUpTimeMillisSpend;
        }
        if (warmUpSecondsSpend != null) {
            warmUpTimeMillisSpendTotal += warmUpSecondsSpend * 1000L;
        }
        if (warmUpMinutesSpend != null) {
            warmUpTimeMillisSpendTotal += warmUpMinutesSpend * 60000L;
        }
        if (warmUpHoursSpend != null) {
            warmUpTimeMillisSpendTotal += warmUpHoursSpend * 3600000L;
        }
        return warmUpTimeMillisSpendTotal;
    }

}
