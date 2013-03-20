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
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.solution.ProblemIO;
import org.optaplanner.persistence.xstream.XStreamProblemIO;

@XStreamAlias("problemBenchmarks")
public class ProblemBenchmarksConfig {

    private Class<ProblemIO> problemIOClass = null;
    @XStreamImplicit(itemFieldName = "xstreamAnnotatedClass")
    private List<Class> xstreamAnnotatedClassList = null;
    private Boolean writeOutputSolutionEnabled = null;

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

    public Boolean getWriteOutputSolutionEnabled() {
        return writeOutputSolutionEnabled;
    }

    public void setWriteOutputSolutionEnabled(Boolean writeOutputSolutionEnabled) {
        this.writeOutputSolutionEnabled = writeOutputSolutionEnabled;
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

    public List<ProblemBenchmark> buildProblemBenchmarkList(DefaultPlannerBenchmark plannerBenchmark,
            SolverBenchmark solverBenchmark) {
        validate(solverBenchmark);
        ProblemIO problemIO = buildProblemIO();
        List<ProblemBenchmark> problemBenchmarkList = new ArrayList<ProblemBenchmark>(inputSolutionFileList.size());
        List<ProblemBenchmark> unifiedProblemBenchmarkList = plannerBenchmark.getUnifiedProblemBenchmarkList();
        for (File inputSolutionFile : inputSolutionFileList) {
            if (!inputSolutionFile.exists()) {
                throw new IllegalArgumentException("The inputSolutionFile (" + inputSolutionFile + ") does not exist.");
            }
            // 2 SolverBenchmarks containing equal ProblemBenchmarks should contain the same instance
            ProblemBenchmark newProblemBenchmark = buildProblemBenchmark(plannerBenchmark,
                    problemIO, inputSolutionFile);
            ProblemBenchmark problemBenchmark;
            int index = unifiedProblemBenchmarkList.indexOf(newProblemBenchmark);
            if (index < 0) {
                problemBenchmark = newProblemBenchmark;
                unifiedProblemBenchmarkList.add(problemBenchmark);
            } else {
                problemBenchmark = unifiedProblemBenchmarkList.get(index);
            }
            addSingleBenchmark(solverBenchmark, problemBenchmark);
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
            return ConfigUtils.newInstance(this, "problemIOClass", problemIOClass);
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

    private ProblemBenchmark buildProblemBenchmark(DefaultPlannerBenchmark plannerBenchmark,
            ProblemIO problemIO, File inputSolutionFile) {
        ProblemBenchmark problemBenchmark = new ProblemBenchmark(plannerBenchmark);
        String name = FilenameUtils.getBaseName(inputSolutionFile.getName());
        problemBenchmark.setName(name);
        problemBenchmark.setProblemIO(problemIO);
        problemBenchmark.setWriteOutputSolutionEnabled(
                writeOutputSolutionEnabled == null ? false : writeOutputSolutionEnabled);
        problemBenchmark.setInputSolutionFile(inputSolutionFile);
        // outputSolutionFilesDirectory is set by DefaultPlannerBenchmark
        List<ProblemStatistic> problemStatisticList = new ArrayList<ProblemStatistic>(
                problemStatisticTypeList == null ? 0 : problemStatisticTypeList.size());
        if (problemStatisticTypeList != null) {
            for (ProblemStatisticType problemStatisticType : problemStatisticTypeList) {
                problemStatisticList.add(problemStatisticType.create(problemBenchmark));
            }
        }
        problemBenchmark.setProblemStatisticList(problemStatisticList);
        problemBenchmark.setSingleBenchmarkList(new ArrayList<SingleBenchmark>());
        return problemBenchmark;
    }

    private void addSingleBenchmark(
            SolverBenchmark solverBenchmark, ProblemBenchmark problemBenchmark) {
        SingleBenchmark singleBenchmark = new SingleBenchmark(solverBenchmark, problemBenchmark);
        solverBenchmark.getSingleBenchmarkList().add(singleBenchmark);
        problemBenchmark.getSingleBenchmarkList().add(singleBenchmark);
    }

    public void inherit(ProblemBenchmarksConfig inheritedConfig) {
        problemIOClass = ConfigUtils.inheritOverwritableProperty(problemIOClass,
                inheritedConfig.getProblemIOClass());
        xstreamAnnotatedClassList = ConfigUtils.inheritMergeableListProperty(xstreamAnnotatedClassList,
                inheritedConfig.getXstreamAnnotatedClassList());
        writeOutputSolutionEnabled = ConfigUtils.inheritOverwritableProperty(writeOutputSolutionEnabled,
                inheritedConfig.getWriteOutputSolutionEnabled());
        inputSolutionFileList = ConfigUtils.inheritMergeableListProperty(inputSolutionFileList,
                inheritedConfig.getInputSolutionFileList());
        problemStatisticTypeList = ConfigUtils.inheritMergeableListProperty(problemStatisticTypeList,
                inheritedConfig.getProblemStatisticTypeList());
    }

}
