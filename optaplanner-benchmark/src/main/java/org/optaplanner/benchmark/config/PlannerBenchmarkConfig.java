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

package org.optaplanner.benchmark.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.ranking.SolverBenchmarkRankingWeightFactory;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.optaplanner.benchmark.impl.ranking.SolverBenchmarkRankingType;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverBenchmarkRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverBenchmarkRankingComparator;
import org.optaplanner.benchmark.impl.ranking.WorstScoreSolverBenchmarkRankingComparator;
import org.optaplanner.core.config.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XStreamAlias("plannerBenchmark")
public class PlannerBenchmarkConfig {

    public static final String PARALLEL_BENCHMARK_COUNT_AUTO = "AUTO";
    /**
     * @see Runtime#availableProcessors()
     */
    public static final String AVAILABLE_PROCESSOR_COUNT = "availableProcessorCount";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private String name = null;
    private File benchmarkDirectory = null;

    private String parallelBenchmarkCount = null;
    private Long warmUpTimeMillisSpend = null;
    private Long warmUpSecondsSpend = null;
    private Long warmUpMinutesSpend = null;
    private Long warmUpHoursSpend = null;

    private Locale benchmarkReportLocale = null;
    private SolverBenchmarkRankingType solverBenchmarkRankingType = null;
    private Class<Comparator<SolverBenchmark>> solverBenchmarkRankingComparatorClass = null;
    private Class<SolverBenchmarkRankingWeightFactory> solverBenchmarkRankingWeightFactoryClass = null;

    @XStreamAlias("inheritedSolverBenchmark")
    private SolverBenchmarkConfig inheritedSolverBenchmarkConfig = null;

    @XStreamImplicit(itemFieldName = "solverBenchmark")
    private List<SolverBenchmarkConfig> solverBenchmarkConfigList = null;

    private Boolean benchmarkHistoryReportEnabled = null;

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

    public Locale getBenchmarkReportLocale() {
        return benchmarkReportLocale;
    }

    public void setBenchmarkReportLocale(Locale benchmarkReportLocale) {
        this.benchmarkReportLocale = benchmarkReportLocale;
    }

    public SolverBenchmarkRankingType getSolverBenchmarkRankingType() {
        return solverBenchmarkRankingType;
    }

    public void setSolverBenchmarkRankingType(SolverBenchmarkRankingType solverBenchmarkRankingType) {
        this.solverBenchmarkRankingType = solverBenchmarkRankingType;
    }

    public Class<Comparator<SolverBenchmark>> getSolverBenchmarkRankingComparatorClass() {
        return solverBenchmarkRankingComparatorClass;
    }

    public void setSolverBenchmarkRankingComparatorClass(Class<Comparator<SolverBenchmark>> solverBenchmarkRankingComparatorClass) {
        this.solverBenchmarkRankingComparatorClass = solverBenchmarkRankingComparatorClass;
    }

    public Class<SolverBenchmarkRankingWeightFactory> getSolverBenchmarkRankingWeightFactoryClass() {
        return solverBenchmarkRankingWeightFactoryClass;
    }

    public void setSolverBenchmarkRankingWeightFactoryClass(Class<SolverBenchmarkRankingWeightFactory> solverBenchmarkRankingWeightFactoryClass) {
        this.solverBenchmarkRankingWeightFactoryClass = solverBenchmarkRankingWeightFactoryClass;
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

    public Boolean getBenchmarkHistoryReportEnabled() {
        return benchmarkHistoryReportEnabled;
    }

    public void setBenchmarkHistoryReportEnabled(Boolean benchmarkHistoryReportEnabled) {
        this.benchmarkHistoryReportEnabled = benchmarkHistoryReportEnabled;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public PlannerBenchmark buildPlannerBenchmark() {
        validate();
        generateSolverBenchmarkConfigNames();
        inherit();

        DefaultPlannerBenchmark plannerBenchmark = new DefaultPlannerBenchmark();
        plannerBenchmark.setName(name);
        plannerBenchmark.setBenchmarkDirectory(benchmarkDirectory);
        plannerBenchmark.setParallelBenchmarkCount(resolveParallelBenchmarkCount());
        plannerBenchmark.setWarmUpTimeMillisSpend(calculateWarmUpTimeMillisSpendTotal());
        plannerBenchmark.getBenchmarkReport().setLocale(
                benchmarkReportLocale == null ? Locale.getDefault() : benchmarkReportLocale);
        plannerBenchmark.getBenchmarkHistoryReport().setLocale(
                benchmarkReportLocale == null ? Locale.getDefault() : benchmarkReportLocale);
        supplySolverBenchmarkRanking(plannerBenchmark);

        List<SolverBenchmark> solverBenchmarkList = new ArrayList<SolverBenchmark>(solverBenchmarkConfigList.size());
        List<ProblemBenchmark> unifiedProblemBenchmarkList = new ArrayList<ProblemBenchmark>();
        plannerBenchmark.setUnifiedProblemBenchmarkList(unifiedProblemBenchmarkList);
        for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
            SolverBenchmark solverBenchmark = solverBenchmarkConfig.buildSolverBenchmark(plannerBenchmark);
            solverBenchmarkList.add(solverBenchmark);
        }
        plannerBenchmark.setSolverBenchmarkList(solverBenchmarkList);
        boolean benchmarkHistoryReportEnabled_ = benchmarkHistoryReportEnabled == null ? true
                : benchmarkHistoryReportEnabled;
        plannerBenchmark.setBenchmarkHistoryReportEnabled(benchmarkHistoryReportEnabled_);
        return plannerBenchmark;
    }

    protected void validate() {
        final String nameRegex = "^[\\w\\d _\\-\\.\\(\\)]+$";
        if (name != null && !name.matches(nameRegex)) {
            throw new IllegalStateException("The plannerBenchmark name (" + name
                    + ") is invalid because it does not follow the nameRegex (" + nameRegex + ")" +
                    " which might cause an illegal filename.");
        }
        if (solverBenchmarkConfigList == null || solverBenchmarkConfigList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> in the <plannerBenchmark> configuration.");
        }
    }

    protected void generateSolverBenchmarkConfigNames() {
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

    protected void inherit() {
        if (inheritedSolverBenchmarkConfig != null) {
            for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
                solverBenchmarkConfig.inherit(inheritedSolverBenchmarkConfig);
            }
        }
    }

    protected void supplySolverBenchmarkRanking(DefaultPlannerBenchmark plannerBenchmark) {
        if (solverBenchmarkRankingType != null && solverBenchmarkRankingComparatorClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverBenchmarkRankingType (" + solverBenchmarkRankingType
                    + ") and a solverBenchmarkRankingComparatorClass ("
                    + solverBenchmarkRankingComparatorClass.getName() + ") at the same time.");
        } else if (solverBenchmarkRankingType != null && solverBenchmarkRankingWeightFactoryClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverBenchmarkRankingType (" + solverBenchmarkRankingType
                    + ") and a solverBenchmarkRankingWeightFactoryClass ("
                    + solverBenchmarkRankingWeightFactoryClass.getName() + ") at the same time.");
        } else if (solverBenchmarkRankingComparatorClass != null && solverBenchmarkRankingWeightFactoryClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverBenchmarkRankingComparatorClass (" + solverBenchmarkRankingComparatorClass.getName()
                    + ") and a solverBenchmarkRankingWeightFactoryClass ("
                    + solverBenchmarkRankingWeightFactoryClass.getName() + ") at the same time.");
        }
        Comparator<SolverBenchmark> solverBenchmarkRankingComparator = null;
        SolverBenchmarkRankingWeightFactory solverBenchmarkRankingWeightFactory = null;
        if (solverBenchmarkRankingType != null) {
            switch (solverBenchmarkRankingType) {
                case TOTAL_SCORE:
                    solverBenchmarkRankingComparator = new TotalScoreSolverBenchmarkRankingComparator();
                    break;
                case WORST_SCORE:
                    solverBenchmarkRankingComparator = new WorstScoreSolverBenchmarkRankingComparator();
                    break;
                case TOTAL_RANKING:
                    solverBenchmarkRankingWeightFactory = new TotalRankSolverBenchmarkRankingWeightFactory();
                    break;
                default:
                    throw new IllegalStateException("The solverBenchmarkRankingType ("
                            + solverBenchmarkRankingType + ") is not implemented.");
            }
        }
        if (solverBenchmarkRankingComparatorClass != null) {
            solverBenchmarkRankingComparator = ConfigUtils.newInstance(this,
                    "solverBenchmarkRankingComparatorClass", solverBenchmarkRankingComparatorClass);
        }
        if (solverBenchmarkRankingWeightFactoryClass != null) {
            try {
                solverBenchmarkRankingWeightFactory = solverBenchmarkRankingWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("solverBenchmarkComparatorFactoryClass ("
                        + solverBenchmarkRankingWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("solverBenchmarkComparatorFactoryClass ("
                        + solverBenchmarkRankingWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        }
        if (solverBenchmarkRankingComparator != null) {
            plannerBenchmark.setSolverBenchmarkRankingComparator(solverBenchmarkRankingComparator);
        } else if (solverBenchmarkRankingWeightFactory != null) {
            plannerBenchmark.setSolverBenchmarkRankingWeightFactory(solverBenchmarkRankingWeightFactory);
        } else {
            plannerBenchmark.setSolverBenchmarkRankingComparator(new TotalScoreSolverBenchmarkRankingComparator());
        }
    }

    protected int resolveParallelBenchmarkCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedParallelBenchmarkCount;
        if (parallelBenchmarkCount == null) {
            resolvedParallelBenchmarkCount = 1;
        } else if (parallelBenchmarkCount.equals(PARALLEL_BENCHMARK_COUNT_AUTO)) {
            // TODO Tweak it based on experience
            if (availableProcessorCount <= 2) {
                resolvedParallelBenchmarkCount = 1;
            } else if (availableProcessorCount <= 4) {
                resolvedParallelBenchmarkCount = 2;
            } else {
                resolvedParallelBenchmarkCount = (availableProcessorCount / 2) + 1;
            }
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
                    + ") that is lower than 1.");
        }
        if (resolvedParallelBenchmarkCount > availableProcessorCount) {
            logger.warn("Because the resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                    + ") is higher than the availableProcessorCount (" + availableProcessorCount
                    + "), it is reduced to availableProcessorCount.");
            resolvedParallelBenchmarkCount = availableProcessorCount;
        }
        return resolvedParallelBenchmarkCount;
    }

    protected long calculateWarmUpTimeMillisSpendTotal() {
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
