/*
 * Copyright 2010 JBoss Inc
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.PlannerBenchmark;
import org.drools.planner.config.XmlSolverConfigurer;

public class XmlSolverBenchmarker {

    private XStream xStream;
    private PlannerBenchmarkConfig plannerBenchmarkConfig = null;

    public XmlSolverBenchmarker() {
        xStream = XmlSolverConfigurer.buildXstream();
        xStream.processAnnotations(PlannerBenchmarkConfig.class);
    }

    public void addXstreamAnnotations(Class... xstreamAnnotations) {
        xStream.processAnnotations(xstreamAnnotations);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public XmlSolverBenchmarker configure(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
        }
        return configure(in);
    }

    public XmlSolverBenchmarker configure(InputStream in) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, "utf-8");
            return configure(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support utf-8 encoding.", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    public XmlSolverBenchmarker configure(Reader reader) {
        plannerBenchmarkConfig = (PlannerBenchmarkConfig) xStream.fromXML(reader);
        return this;
    }

    public PlannerBenchmark buildPlannerBenchmark() {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") is null," +
                    " call configure(...) first.");
        }
        return plannerBenchmarkConfig.buildPlannerBenchmark();
    }

    public void benchmark() {
        PlannerBenchmark plannerBenchmark = buildPlannerBenchmark();
        plannerBenchmark.benchmark();
    }

}
