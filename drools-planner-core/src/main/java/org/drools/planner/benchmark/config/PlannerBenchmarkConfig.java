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

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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

    public static final String PARALLEL_BENCHMARK_COUNT_AUTO = "AUTO";
    /**
     * @see Runtime#availableProcessors()
     */
    public static final String AVAILABLE_PROCESSOR_COUNT = "availableProcessorCount";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private File benchmarkDirectory = null;
    private File benchmarkInstanceDirectory = null;
    private File outputSolutionFilesDirectory = null;
    private File statisticDirectory = null;
    private Comparator<SolverBenchmark> solverBenchmarkComparator = null;

    private String parallelBenchmarkCount = null;
    private Long warmUpTimeMillisSpend = null;
    private Long warmUpSecondsSpend = null;
    private Long warmUpMinutesSpend = null;
    private Long warmUpHoursSpend = null;

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

    /**
     * Using multiple parallel benchmarks can decrease the reliability of the results.
     * <p/>
     * If there aren't enough processors available, it will be decreased.
     * @return null, {@value #PARALLEL_BENCHMARK_COUNT_AUTO}
     * or a JavaScript calculation using {@value #AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getParallelBenchmarkCount() {
        return parallelBenchmarkCount;
    }

    public void setParallelBenchmarkCount(String parallelBenchmarkCount) {
        this.parallelBenchmarkCount = parallelBenchmarkCount;
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

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    private void validate() {
        if (solverBenchmarkConfigList == null || solverBenchmarkConfigList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> in the <plannerBenchmark> configuration.");
        }
    }
    
    private ExecutorService createExecutor() {
        int resolvedParallelBenchmarkCount = resolveParallelBenchmarkCount();
        if (resolvedParallelBenchmarkCount > Runtime.getRuntime().availableProcessors()) {
            logger.warn("Benchmarker will use more threads than there are CPUs. Results may be compromised.");
        } else if (resolvedParallelBenchmarkCount < 1) {
            logger.warn("Requested number of threads (" + resolvedParallelBenchmarkCount + ") is invalid.");
            resolvedParallelBenchmarkCount = 1;
        }
        logger.info("Benchmarking will use (" + resolvedParallelBenchmarkCount + ") threads.");
        return Executors.newFixedThreadPool(resolvedParallelBenchmarkCount);
    }
    
    protected int resolveParallelBenchmarkCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedParallelBenchmarkCount;
        if (parallelBenchmarkCount == null) {
            resolvedParallelBenchmarkCount = 1;
        } else if (parallelBenchmarkCount.equals(PARALLEL_BENCHMARK_COUNT_AUTO)) {
            resolvedParallelBenchmarkCount = (availableProcessorCount / 2) + 1;
        } else {
            String scriptLanguage = "JavaScript";
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName(scriptLanguage);
            scriptEngine.put(AVAILABLE_PROCESSOR_COUNT, availableProcessorCount);
            Object scriptResult;
            try {
                scriptResult = scriptEngine.eval(parallelBenchmarkCount);
            } catch (ScriptException e) {
                throw new IllegalArgumentException("The parallelBenchmarkCount (" + parallelBenchmarkCount
                        + ") is not " + PARALLEL_BENCHMARK_COUNT_AUTO + " and cannot be parsed in " + scriptLanguage
                        + " with the variables ([" + AVAILABLE_PROCESSOR_COUNT + "]).", e);
            }
            if (!(scriptResult instanceof Number)) {
                throw new IllegalArgumentException("The parallelBenchmarkCount (" + parallelBenchmarkCount
                        + ") is resolved to scriptResult (" + scriptResult + ") in " + scriptLanguage
                        + " and is not a Number.");
            }
            resolvedParallelBenchmarkCount = ((Number) scriptResult).intValue();
        }
        if (resolvedParallelBenchmarkCount < 1) {
            throw new IllegalArgumentException("The parallelBenchmarkCount (" + parallelBenchmarkCount
                    + ") resulted in a resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                    + ") that is lower then 1.");
        }
        if (resolvedParallelBenchmarkCount > availableProcessorCount) {
            logger.warn("Because the resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                    + ") is higher than the availableProcessorCount (" + availableProcessorCount
                    + "), it is reduced to availableProcessorCount.");
            resolvedParallelBenchmarkCount = availableProcessorCount;
        }
        return resolvedParallelBenchmarkCount;
    }

    private void generateSolverBenchmarkConfigNames() {
        Set<String> nameSet = new HashSet<String>(solverBenchmarkConfigList.size());
        Set<SolverBenchmarkConfig> noNameBenchmarkConfigSet
                = new LinkedHashSet<SolverBenchmarkConfig>(solverBenchmarkConfigList.size());
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

        ExecutorService executor = createExecutor();
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
