/*
 * Copyright 2014 JBoss Inc
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
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.core.config.solver.XmlSolverFactory;

public class BenchmarkResultIO {

    private static final String PLANNER_BENCHMARK_RESULT_FILENAME = "plannerBenchmarkResult.xml";

    private final XStream xStream;

    public BenchmarkResultIO() {
        xStream = XmlSolverFactory.buildXstream();
        xStream.processAnnotations(PlannerBenchmarkResult.class);
    }

    public void writePlannerBenchmarkResult(File benchmarkReportDirectory,
            PlannerBenchmarkResult plannerBenchmarkResult) {
        File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(plannerBenchmarkResultFile), "UTF-8");
            xStream.toXML(plannerBenchmarkResult, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Problem writing plannerBenchmarkResultFile: " + plannerBenchmarkResultFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public List<PlannerBenchmarkResult> readPlannerBenchmarkResultList(File benchmarkDirectory) {
        if (!benchmarkDirectory.exists()) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory
                    + ") does not exist.");
        }
        if (!benchmarkDirectory.isDirectory()) {
            throw new IllegalArgumentException("The benchmarkDirectory (" + benchmarkDirectory
                    + ") is not a directory.");
        }
        File[] benchmarkReportDirectories = benchmarkDirectory.listFiles((FileFilter) DirectoryFileFilter.INSTANCE);
        List<PlannerBenchmarkResult> plannerBenchmarkResultList = new ArrayList<PlannerBenchmarkResult>(
                benchmarkReportDirectories.length);
        for (File benchmarkReportDirectory : benchmarkReportDirectories) {
            File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
            if (plannerBenchmarkResultFile.exists()) {
                PlannerBenchmarkResult plannerBenchmarkResult = readPlannerBenchmarkResult(plannerBenchmarkResultFile);
                // TODO plannerBenchmarkResult.accumulateResults();
                plannerBenchmarkResultList.add(plannerBenchmarkResult);
            }
        }
        return plannerBenchmarkResultList;
    }

    protected PlannerBenchmarkResult readPlannerBenchmarkResult(File plannerBenchmarkResultFile) {
        if (!plannerBenchmarkResultFile.exists()) {
            throw new IllegalArgumentException("The plannerBenchmarkResultFile (" + plannerBenchmarkResultFile
                    + ") does not exist.");
        }
        PlannerBenchmarkResult plannerBenchmarkResult;
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(plannerBenchmarkResultFile), "UTF-8");
            plannerBenchmarkResult = (PlannerBenchmarkResult) xStream.fromXML(reader);
        } catch (XStreamException e) {
            throw new IllegalArgumentException(
                    "Problem reading plannerBenchmarkResultFile: " + plannerBenchmarkResultFile, e);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Problem reading plannerBenchmarkResultFile: " + plannerBenchmarkResultFile, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        plannerBenchmarkResult.setBenchmarkReportDirectory(plannerBenchmarkResultFile.getParentFile());
        restoreOmittedBidirectionalFields(plannerBenchmarkResult);
        return plannerBenchmarkResult;
    }

    private void restoreOmittedBidirectionalFields(PlannerBenchmarkResult plannerBenchmarkResult) {
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            solverBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setSolverBenchmarkResult(solverBenchmarkResult);
            }
        }
        for (ProblemBenchmarkResult problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            problemBenchmarkResult.setPlannerBenchmarkResult(plannerBenchmarkResult);
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                singleBenchmarkResult.setProblemBenchmarkResult(problemBenchmarkResult);
            }
            for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
                problemStatistic.setProblemBenchmarkResult(problemBenchmarkResult);
            }
        }
    }

}
