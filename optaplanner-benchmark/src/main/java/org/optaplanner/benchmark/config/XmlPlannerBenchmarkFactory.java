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

package org.optaplanner.benchmark.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;

public class XmlPlannerBenchmarkFactory implements PlannerBenchmarkFactory {

    private XStream xStream;
    private PlannerBenchmarkConfig plannerBenchmarkConfig = null;

    public XmlPlannerBenchmarkFactory() {
        xStream = XmlSolverFactory.buildXstream();
        xStream.processAnnotations(PlannerBenchmarkConfig.class);
    }

    public XmlPlannerBenchmarkFactory(String resource) {
        this();
        configure(resource);
    }

    // ************************************************************************
    // Configure methods
    // ************************************************************************

    public void addXstreamAnnotations(Class... xstreamAnnotations) {
        xStream.processAnnotations(xstreamAnnotations);
    }

    public XmlPlannerBenchmarkFactory configure(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalArgumentException("Could not find resource (" + resource + ").");
        }
        return configure(in);
    }

    public XmlPlannerBenchmarkFactory configure(InputStream in) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
            return configure(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    public XmlPlannerBenchmarkFactory configure(Reader reader) {
        plannerBenchmarkConfig = (PlannerBenchmarkConfig) xStream.fromXML(reader);
        return this;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlannerBenchmarkConfig getPlannerBenchmarkConfig() {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") is null," +
                    " call configure(...) first.");
        }
        return plannerBenchmarkConfig;
    }

    public PlannerBenchmark buildPlannerBenchmark() {
        if (plannerBenchmarkConfig == null) {
            throw new IllegalStateException("The plannerBenchmarkConfig (" + plannerBenchmarkConfig + ") is null," +
                    " call configure(...) first.");
        }
        return plannerBenchmarkConfig.buildPlannerBenchmark();
    }

}
