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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
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

    public PlannerBenchmarkResult readPlannerBenchmarkResult(File benchmarkReportDirectory) {
        File plannerBenchmarkResultFile = new File(benchmarkReportDirectory, PLANNER_BENCHMARK_RESULT_FILENAME);
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(plannerBenchmarkResultFile), "UTF-8");
            return (PlannerBenchmarkResult) xStream.fromXML(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Problem reading plannerBenchmarkResultFile: " + plannerBenchmarkResultFile, e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}
