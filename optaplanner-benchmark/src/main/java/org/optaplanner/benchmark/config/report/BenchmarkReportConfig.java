/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.config.report;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Locale;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.benchmark.config.ranking.SolverRankingType;
import org.optaplanner.benchmark.impl.ranking.SolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalRankSolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.ranking.WorstScoreSolverRankingComparator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.AbstractConfig;
import org.optaplanner.core.config.util.ConfigUtils;

@XStreamAlias("benchmarkReport")
public class BenchmarkReportConfig extends AbstractConfig<BenchmarkReportConfig> {

    private Locale locale = null;
    private SolverRankingType solverRankingType = null;
    private Class<? extends Comparator<SolverBenchmarkResult>> solverRankingComparatorClass = null;
    private Class<? extends SolverRankingWeightFactory> solverRankingWeightFactoryClass = null;

    public BenchmarkReportConfig() {
    }

    public BenchmarkReportConfig(BenchmarkReportConfig inheritedConfig) {
        inherit(inheritedConfig);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public SolverRankingType getSolverRankingType() {
        return solverRankingType;
    }

    public void setSolverRankingType(SolverRankingType solverRankingType) {
        this.solverRankingType = solverRankingType;
    }

    public Class<? extends Comparator<SolverBenchmarkResult>> getSolverRankingComparatorClass() {
        return solverRankingComparatorClass;
    }

    public void setSolverRankingComparatorClass(Class<? extends Comparator<SolverBenchmarkResult>> solverRankingComparatorClass) {
        this.solverRankingComparatorClass = solverRankingComparatorClass;
    }

    public Class<? extends SolverRankingWeightFactory> getSolverRankingWeightFactoryClass() {
        return solverRankingWeightFactoryClass;
    }

    public void setSolverRankingWeightFactoryClass(Class<? extends SolverRankingWeightFactory> solverRankingWeightFactoryClass) {
        this.solverRankingWeightFactoryClass = solverRankingWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public BenchmarkReport buildBenchmarkReport(PlannerBenchmarkResult plannerBenchmark) {
        BenchmarkReport benchmarkReport = new BenchmarkReport(plannerBenchmark);
        benchmarkReport.setLocale(determineLocale());
        benchmarkReport.setTimezoneId(ZoneId.systemDefault());
        supplySolverRanking(benchmarkReport);
        return benchmarkReport;
    }

    public Locale determineLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }

    protected void supplySolverRanking(BenchmarkReport benchmarkReport) {
        if (solverRankingType != null && solverRankingComparatorClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingType (" + solverRankingType
                    + ") and a solverRankingComparatorClass (" + solverRankingComparatorClass.getName()
                    + ") at the same time.");
        } else if (solverRankingType != null && solverRankingWeightFactoryClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingType (" + solverRankingType
                    + ") and a solverRankingWeightFactoryClass (" + solverRankingWeightFactoryClass.getName()
                    + ") at the same time.");
        } else if (solverRankingComparatorClass != null && solverRankingWeightFactoryClass != null) {
            throw new IllegalStateException("The PlannerBenchmark cannot have"
                    + " a solverRankingComparatorClass (" + solverRankingComparatorClass.getName()
                    + ") and a solverRankingWeightFactoryClass (" + solverRankingWeightFactoryClass.getName()
                    + ") at the same time.");
        }
        Comparator<SolverBenchmarkResult> solverRankingComparator = null;
        SolverRankingWeightFactory solverRankingWeightFactory = null;
        if (solverRankingType != null) {
            switch (solverRankingType) {
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
                            + solverRankingType + ") is not implemented.");
            }
        }
        if (solverRankingComparatorClass != null) {
            solverRankingComparator = ConfigUtils.newInstance(this,
                    "solverRankingComparatorClass", solverRankingComparatorClass);
        }
        if (solverRankingWeightFactoryClass != null) {
            solverRankingWeightFactory = ConfigUtils.newInstance(this,
                    "solverRankingWeightFactoryClass", solverRankingWeightFactoryClass);
        }
        if (solverRankingComparator != null) {
            benchmarkReport.setSolverRankingComparator(solverRankingComparator);
        } else if (solverRankingWeightFactory != null) {
            benchmarkReport.setSolverRankingWeightFactory(solverRankingWeightFactory);
        } else {
            benchmarkReport.setSolverRankingComparator(new TotalScoreSolverRankingComparator());
        }
    }

    @Override
    public void inherit(BenchmarkReportConfig inheritedConfig) {
        locale = ConfigUtils.inheritOverwritableProperty(locale, inheritedConfig.getLocale());
        solverRankingType = ConfigUtils.inheritOverwritableProperty(solverRankingType,
                inheritedConfig.getSolverRankingType());
        solverRankingComparatorClass = ConfigUtils.inheritOverwritableProperty(solverRankingComparatorClass,
                inheritedConfig.getSolverRankingComparatorClass());
        solverRankingWeightFactoryClass = ConfigUtils.inheritOverwritableProperty(solverRankingWeightFactoryClass,
                inheritedConfig.getSolverRankingWeightFactoryClass());
    }

}
