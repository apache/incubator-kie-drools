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
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.io.FilenameUtils;
import org.drools.planner.benchmark.core.PlannerBenchmarkResult;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.PlanningProblemIO;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.benchmark.core.XStreamPlanningProblemIO;
import org.drools.planner.benchmark.core.statistic.SolverStatistic;
import org.drools.planner.benchmark.core.statistic.SolverStatisticType;
import org.drools.planner.config.util.ConfigUtils;

@XStreamAlias("problemBenchmarks")
public class ProblemBenchmarksConfig {

    private Class<PlanningProblemIO> planningProblemIOClass = null;
    @XStreamImplicit(itemFieldName = "xstreamAnnotatedClass")
    private List<Class> xstreamAnnotatedClassList = null;

    @XStreamImplicit(itemFieldName = "inputSolutionFile")
    private List<File> inputSolutionFileList = null;
    
    @XStreamImplicit(itemFieldName = "solverStatisticType")
    private List<SolverStatisticType> solverStatisticTypeList = null;

    public Class<PlanningProblemIO> getPlanningProblemIOClass() {
        return planningProblemIOClass;
    }

    public void setPlanningProblemIOClass(Class<PlanningProblemIO> planningProblemIOClass) {
        this.planningProblemIOClass = planningProblemIOClass;
    }

    public List<Class> getXstreamAnnotatedClassList() {
        return xstreamAnnotatedClassList;
    }

    public void setXstreamAnnotatedClassList(List<Class> xstreamAnnotatedClassList) {
        this.xstreamAnnotatedClassList = xstreamAnnotatedClassList;
    }

    public List<File> getInputSolutionFileList() {
        return inputSolutionFileList;
    }

    public void setInputSolutionFileList(List<File> inputSolutionFileList) {
        this.inputSolutionFileList = inputSolutionFileList;
    }

    public List<SolverStatisticType> getSolverStatisticTypeList() {
        return solverStatisticTypeList;
    }

    public void setSolverStatisticTypeList(List<SolverStatisticType> solverStatisticTypeList) {
        this.solverStatisticTypeList = solverStatisticTypeList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public List<ProblemBenchmark> buildProblemBenchmarkList(
            List<ProblemBenchmark> unifiedProblemBenchmarkList, SolverBenchmark solverBenchmark) {
        validate(solverBenchmark);
        PlanningProblemIO planningProblemIO = buildPlanningProblemIO();
        List<ProblemBenchmark> problemBenchmarkList = new ArrayList<ProblemBenchmark>(
                inputSolutionFileList.size());
        for (File inputSolutionFile : inputSolutionFileList) {
            // 2 SolverBenchmarks containing equal PProblemBenchmarks should contain the same instance
            ProblemBenchmark newProblemBenchmark = buildProblemBenchmark(
                    planningProblemIO, inputSolutionFile);
            ProblemBenchmark problemBenchmark;
            int index = unifiedProblemBenchmarkList.indexOf(newProblemBenchmark);
            if (index < 0) {
                problemBenchmark = newProblemBenchmark;
                unifiedProblemBenchmarkList.add(problemBenchmark);
            } else {
                problemBenchmark = unifiedProblemBenchmarkList.get(index);
            }
            addPlannerBenchmarkResult(solverBenchmark, problemBenchmark);
            problemBenchmarkList.add(problemBenchmark);
        }
        return problemBenchmarkList;
    }

    private void validate(SolverBenchmark solverBenchmark) {
        if (inputSolutionFileList == null || inputSolutionFileList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <inputSolutionFile> for the solverBenchmark (" + solverBenchmark.getName()
                            + ") directly or indirectly by inheriting it.");
        }
    }

    private PlanningProblemIO buildPlanningProblemIO() {
        if (planningProblemIOClass != null && xstreamAnnotatedClassList != null) {
            throw new IllegalArgumentException("Cannot use planningProblemIOClass (" + planningProblemIOClass
                    + ") and xstreamAnnotatedClassList (" + xstreamAnnotatedClassList + ") together.");
        }
        if (planningProblemIOClass != null) {
            try {
                return planningProblemIOClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("planningProblemIOClass (" + planningProblemIOClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("planningProblemIOClass (" + planningProblemIOClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        } else {
            Class[] xstreamAnnotatedClasses;
            if (xstreamAnnotatedClassList != null) {
                xstreamAnnotatedClasses = xstreamAnnotatedClassList.toArray(new Class[xstreamAnnotatedClassList.size()]);
            } else {
                xstreamAnnotatedClasses = new Class[0];
            }
            return new XStreamPlanningProblemIO(xstreamAnnotatedClasses);
        }
    }

    private ProblemBenchmark buildProblemBenchmark(
            PlanningProblemIO planningProblemIO, File inputSolutionFile) {
        ProblemBenchmark problemBenchmark = new ProblemBenchmark();
        String name = FilenameUtils.getBaseName(inputSolutionFile.getName());
        problemBenchmark.setName(name);
        problemBenchmark.setPlanningProblemIO(planningProblemIO);
        problemBenchmark.setInputSolutionFile(inputSolutionFile);
        // outputSolutionFilesDirectory is set by DefaultPlannerBenchmark
        List<SolverStatistic> solverStatisticList = new ArrayList<SolverStatistic>(
                solverStatisticTypeList == null ? 0 : solverStatisticTypeList.size());
        if (solverStatisticTypeList != null) {
            for (SolverStatisticType solverStatisticType : solverStatisticTypeList) {
                solverStatisticList.add(solverStatisticType.create());
            }
        }
        problemBenchmark.setSolverStatisticList(solverStatisticList);
        problemBenchmark.setPlannerBenchmarkResultList(new ArrayList<PlannerBenchmarkResult>());
        return problemBenchmark;
    }

    private void addPlannerBenchmarkResult(
            SolverBenchmark solverBenchmark, ProblemBenchmark problemBenchmark) {
        PlannerBenchmarkResult result = new PlannerBenchmarkResult();
        result.setSolverBenchmark(solverBenchmark);
        solverBenchmark.getPlannerBenchmarkResultList().add(result);
        result.setProblemBenchmark(problemBenchmark);
        problemBenchmark.getPlannerBenchmarkResultList().add(result);
    }

    public void inherit(ProblemBenchmarksConfig inheritedConfig) {
        planningProblemIOClass = ConfigUtils.inheritOverwritableProperty(planningProblemIOClass,
                inheritedConfig.getPlanningProblemIOClass());
        xstreamAnnotatedClassList = ConfigUtils.inheritMergeableListProperty(xstreamAnnotatedClassList,
                inheritedConfig.getXstreamAnnotatedClassList());
        inputSolutionFileList = ConfigUtils.inheritMergeableListProperty(inputSolutionFileList,
                inheritedConfig.getInputSolutionFileList());
        solverStatisticTypeList = ConfigUtils.inheritMergeableListProperty(solverStatisticTypeList,
                inheritedConfig.getSolverStatisticTypeList());
    }

}
