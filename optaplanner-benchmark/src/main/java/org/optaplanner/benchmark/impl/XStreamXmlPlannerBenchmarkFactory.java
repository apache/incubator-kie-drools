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

package org.optaplanner.benchmark.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.extended.FileConverter;
import org.apache.commons.io.FilenameUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.impl.solver.XStreamXmlSolverFactory;

/**
 * @see PlannerBenchmarkFactory
 */
public class XStreamXmlPlannerBenchmarkFactory extends AbstractPlannerBenchmarkFactory {

    protected XStream xStream;

    public XStreamXmlPlannerBenchmarkFactory() {
        this(new SolverConfigContext());
    }

    /**
     * @param solverConfigContext never null
     */
    public XStreamXmlPlannerBenchmarkFactory(SolverConfigContext solverConfigContext) {
        super(solverConfigContext);
        xStream = XStreamXmlSolverFactory.buildXStream();
        ClassLoader actualClassLoader = solverConfigContext.determineActualClassLoader();
        xStream.setClassLoader(actualClassLoader);
        xStream.processAnnotations(PlannerBenchmarkConfig.class);
        xStream.registerConverter(new FileConverter() {
            @Override
            public String toString(Object obj) {
                // Write "/" path separators (even on Windows) for portability
                return FilenameUtils.separatorsToUnix(((File) obj).getPath());
            }
        });
    }

    // ************************************************************************
    // Configure methods
    // ************************************************************************

    /**
     * @param xStreamAnnotations never null
     * @see XStream#processAnnotations(Class[])
     */
    public void addXStreamAnnotations(Class... xStreamAnnotations) {
        xStream.processAnnotations(xStreamAnnotations);
        xStream.allowTypes(xStreamAnnotations);
    }

    public XStream getXStream() {
        return xStream;
    }

    /**
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return this
     */
    public XStreamXmlPlannerBenchmarkFactory configure(String benchmarkConfigResource) {
        ClassLoader actualClassLoader = solverConfigContext.determineActualClassLoader();
        try (InputStream in = actualClassLoader.getResourceAsStream(benchmarkConfigResource)) {
            if (in == null) {
                String errorMessage = "The benchmarkConfigResource (" + benchmarkConfigResource
                        + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
                if (benchmarkConfigResource.startsWith("/")) {
                    errorMessage += "\nAs from 6.1, a classpath resource should not start with a slash (/)."
                            + " A benchmarkConfigResource now adheres to ClassLoader.getResource(String)."
                            + " Remove the leading slash from the benchmarkConfigResource if you're upgrading from 6.0.";
                }
                throw new IllegalArgumentException(errorMessage);
            }
            return configure(in);
        } catch (ConversionException e) {
            throw new IllegalArgumentException("Unmarshalling of benchmarkConfigResource (" + benchmarkConfigResource
                    + ") fails.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigResource (" + benchmarkConfigResource + ") failed.", e);
        }
    }

    public XStreamXmlPlannerBenchmarkFactory configure(File benchmarkConfigFile) {
        try (InputStream in = new FileInputStream(benchmarkConfigFile)) {
            return configure(in);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The benchmarkConfigFile (" + benchmarkConfigFile
                    + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigFile (" + benchmarkConfigFile + ") failed.", e);
        }
    }

    public XStreamXmlPlannerBenchmarkFactory configure(InputStream in) {
        try (Reader reader = new InputStreamReader(in, "UTF-8")) {
            return configure(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading failed.", e);
        }
    }

    public XStreamXmlPlannerBenchmarkFactory configure(Reader reader) {
        plannerBenchmarkConfig = (PlannerBenchmarkConfig) xStream.fromXML(reader);
        return this;
    }

}
