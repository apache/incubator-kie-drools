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

package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.ConversionException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;
import org.optaplanner.persistence.xstream.api.score.AbstractScoreXStreamConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkResultIO {

    private static final String PLANNER_BENCHMARK_RESULT_FILENAME = "plannerBenchmarkResult.xml";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final XStream xStream;

    public BenchmarkResultIO() {
        xStream = XStreamConfigReader.buildXStream();
        xStream.processAnnotations(PlannerBenchmarkResult.class);
        xStream.allowTypesByRegExp(new String[]{"org\\.optaplanner\\.\\w+\\.api\\..*"});
        xStream.allowTypesByRegExp(new String[]{"org\\.optaplanner\\.\\w+\\.impl\\..*"});
        AbstractScoreXStreamConverter.registerScoreConverters(xStream);
    }

    public void writePlannerBenchmarkResult(File benchmarkReportDirectory,
            PlannerBenchmarkResult plannerBenchmarkResult) {
        File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(plannerBenchmarkResultFile), "UTF-8")) {
            xStream.toXML(plannerBenchmarkResult, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed writing plannerBenchmarkResultFile (" + plannerBenchmarkResultFile + ").", e);
        }
    }

    public List<PlannerBenchmarkResult> readPlannerBenchmarkResultList(SolverConfigContext configContext, File benchmarkDirectory) {
        if (!benchmarkDirectory.exists() || !benchmarkDirectory.isDirectory()) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory
                    + ") does not exist or is not a directory.");
        }
        File[] benchmarkReportDirectories = benchmarkDirectory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        if (benchmarkReportDirectories == null) {
            throw new IllegalStateException("Unable to list the subdirectories in the benchmarkDirectory ("
                    + benchmarkDirectory.getAbsolutePath() + ").");
        }
        Arrays.sort(benchmarkReportDirectories);
        List<PlannerBenchmarkResult> plannerBenchmarkResultList = new ArrayList<>(benchmarkReportDirectories.length);
        for (File benchmarkReportDirectory : benchmarkReportDirectories) {
            File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
            if (plannerBenchmarkResultFile.exists()) {
                PlannerBenchmarkResult plannerBenchmarkResult = readPlannerBenchmarkResult(configContext, plannerBenchmarkResultFile);
                plannerBenchmarkResultList.add(plannerBenchmarkResult);
            }
        }
        return plannerBenchmarkResultList;
    }

    protected PlannerBenchmarkResult readPlannerBenchmarkResult(SolverConfigContext configContext, File plannerBenchmarkResultFile) {
        if (!plannerBenchmarkResultFile.exists()) {
            throw new IllegalArgumentException("The plannerBenchmarkResultFile (" + plannerBenchmarkResultFile
                    + ") does not exist.");
        }
        PlannerBenchmarkResult plannerBenchmarkResult;
        try (Reader reader = new InputStreamReader(new FileInputStream(plannerBenchmarkResultFile), "UTF-8")) {
            plannerBenchmarkResult = (PlannerBenchmarkResult) xStream.fromXML(reader);
        } catch (ConversionException e) {
            logger.warn("Failed reading plannerBenchmarkResultFile ({}).", plannerBenchmarkResultFile, e);
            // If the plannerBenchmarkResultFile's format has changed, the app should not crash entirely
            String benchmarkReportDirectoryName = plannerBenchmarkResultFile.getParentFile().getName();
            plannerBenchmarkResult = PlannerBenchmarkResult.createUnmarshallingFailedResult(
                    benchmarkReportDirectoryName);
        } catch (XStreamException | IOException e) {
            throw new IllegalArgumentException(
                    "Failed reading plannerBenchmarkResultFile (" + plannerBenchmarkResultFile + ").", e);
        }
        plannerBenchmarkResult.setBenchmarkReportDirectory(plannerBenchmarkResultFile.getParentFile());
        restoreOmittedBidirectionalFields(plannerBenchmarkResult);
        restoreOtherOmittedFields(configContext, plannerBenchmarkResult);
        return plannerBenchmarkResult;
    }

    private void restoreOmittedBidirectionalFields(PlannerBenchmarkResult plannerBenchmarkResult) {
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            problemBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            if (problemBenchmarkResult.getProblemStatisticList() == null) {
                problemBenchmarkResult.setProblemStatisticList(new ArrayList<>(0));
            }
            for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
                problemStatistic.setProblemBenchmarkResult(problemBenchmarkResult);
            }
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setProblemBenchmarkResult(problemBenchmarkResult);
            }
        }
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            solverBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setSolverBenchmarkResult(solverBenchmarkResult);
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult.getSubSingleBenchmarkResultList()) {
                    if (subSingleBenchmarkResult.getPureSubSingleStatisticList() == null) {
                        subSingleBenchmarkResult.setPureSubSingleStatisticList(new ArrayList<>(0));
                    }
                }
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult.getSubSingleBenchmarkResultList()) {
                    for (PureSubSingleStatistic pureSubSingleStatistic : subSingleBenchmarkResult.getPureSubSingleStatisticList()) {
                        pureSubSingleStatistic.setSubSingleBenchmarkResult(subSingleBenchmarkResult);
                    }
                }
            }
        }
    }

    private void restoreOtherOmittedFields(SolverConfigContext configContext, PlannerBenchmarkResult plannerBenchmarkResult) {
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            SolverConfig solverConfig = solverBenchmarkResult.getSolverConfig();
            solverBenchmarkResult.setScoreDefinition(
                    solverConfig.buildSolutionDescriptor(configContext).getScoreDefinition());
        }
    }

}
