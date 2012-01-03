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
import org.drools.planner.benchmark.api.ProblemIO;
import org.drools.planner.benchmark.core.PlannerBenchmarkResult;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.benchmark.core.XStreamProblemIO;
import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.benchmark.core.statistic.ProblemStatisticType;
import org.drools.planner.config.util.ConfigUtils;

@XStreamAlias("problemBenchmarks")
public class ProblemBenchmarksConfig {

    private Class<ProblemIO> problemIOClass = null;
    @XStreamImplicit(itemFieldName = "xstreamAnnotatedClass")
    private List<Class> xstreamAnnotatedClassList = null;

    @XStreamImplicit(itemFieldName = "inputSolutionFile")
    private List<File> inputSolutionFileList = null;
    
    @XStreamImplicit(itemFieldName = "problemStatisticType")
    private List<ProblemStatisticType> problemStatisticTypeList = null;

    public Class<ProblemIO> getProblemIOClass() {
        return problemIOClass;
    }

    public void setProblemIOClass(Class<ProblemIO> problemIOClass) {
        this.problemIOClass = problemIOClass;
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

    public List<ProblemStatisticType> getProblemStatisticTypeList() {
        return problemStatisticTypeList;
    }

    public void setProblemStatisticTypeList(List<ProblemStatisticType> problemStatisticTypeList) {
        this.problemStatisticTypeList = problemStatisticTypeList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public List<ProblemBenchmark> buildProblemBenchmarkList(
            List<ProblemBenchmark> unifiedProblemBenchmarkList, SolverBenchmark solverBenchmark) {
        validate(solverBenchmark);
        ProblemIO problemIO = buildProblemIO();
        List<ProblemBenchmark> problemBenchmarkList = new ArrayList<ProblemBenchmark>(
                inputSolutionFileList.size());
        for (File inputSolutionFile : inputSolutionFileList) {
            // 2 SolverBenchmarks containing equal PProblemBenchmarks should contain the same instance
            ProblemBenchmark newProblemBenchmark = buildProblemBenchmark(
                    problemIO, inputSolutionFile);
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

    private ProblemIO buildProblemIO() {
        if (problemIOClass != null && xstreamAnnotatedClassList != null) {
            throw new IllegalArgumentException("Cannot use problemIOClass (" + problemIOClass
                    + ") and xstreamAnnotatedClassList (" + xstreamAnnotatedClassList + ") together.");
        }
        if (problemIOClass != null) {
            try {
                return problemIOClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("problemIOClass (" + problemIOClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("problemIOClass (" + problemIOClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
        } else {
            Class[] xstreamAnnotatedClasses;
            if (xstreamAnnotatedClassList != null) {
                xstreamAnnotatedClasses = xstreamAnnotatedClassList.toArray(new Class[xstreamAnnotatedClassList.size()]);
            } else {
                xstreamAnnotatedClasses = new Class[0];
            }
            return new XStreamProblemIO(xstreamAnnotatedClasses);
        }
    }

    private ProblemBenchmark buildProblemBenchmark(
            ProblemIO problemIO, File inputSolutionFile) {
        ProblemBenchmark problemBenchmark = new ProblemBenchmark();
        String name = FilenameUtils.getBaseName(inputSolutionFile.getName());
        problemBenchmark.setName(name);
        problemBenchmark.setProblemIO(problemIO);
        problemBenchmark.setInputSolutionFile(inputSolutionFile);
        // outputSolutionFilesDirectory is set by DefaultPlannerBenchmark
        List<ProblemStatistic> problemStatisticList = new ArrayList<ProblemStatistic>(
                problemStatisticTypeList == null ? 0 : problemStatisticTypeList.size());
        if (problemStatisticTypeList != null) {
            for (ProblemStatisticType problemStatisticType : problemStatisticTypeList) {
                problemStatisticList.add(problemStatisticType.create());
            }
        }
        problemBenchmark.setProblemStatisticList(problemStatisticList);
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
        problemIOClass = ConfigUtils.inheritOverwritableProperty(problemIOClass,
                inheritedConfig.getProblemIOClass());
        xstreamAnnotatedClassList = ConfigUtils.inheritMergeableListProperty(xstreamAnnotatedClassList,
                inheritedConfig.getXstreamAnnotatedClassList());
        inputSolutionFileList = ConfigUtils.inheritMergeableListProperty(inputSolutionFileList,
                inheritedConfig.getInputSolutionFileList());
        problemStatisticTypeList = ConfigUtils.inheritMergeableListProperty(problemStatisticTypeList,
                inheritedConfig.getProblemStatisticTypeList());
    }

}
