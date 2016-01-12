/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.core.config.SolverConfigContext;

/**
 * @see PlannerBenchmarkFactory
 */
public class FreemarkerXmlPlannerBenchmarkFactory extends PlannerBenchmarkFactory {

    protected final SolverConfigContext solverConfigContext;
    private final XStreamXmlPlannerBenchmarkFactory xmlPlannerBenchmarkFactory;

    public FreemarkerXmlPlannerBenchmarkFactory() {
        this(new SolverConfigContext());
    }

    /**
     * @param solverConfigContext never null
     */
    public FreemarkerXmlPlannerBenchmarkFactory(SolverConfigContext solverConfigContext) {
        this.solverConfigContext = solverConfigContext;
        xmlPlannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory(solverConfigContext);
    }

    // ************************************************************************
    // Configure methods
    // ************************************************************************

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return this
     */
    public FreemarkerXmlPlannerBenchmarkFactory configure(String templateResource) {
        return configure(templateResource, null);
    }

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return this
     */
    public FreemarkerXmlPlannerBenchmarkFactory configure(String templateResource, Object model) {
        ClassLoader actualClassLoader = solverConfigContext.determineActualClassLoader();
        InputStream templateIn = actualClassLoader.getResourceAsStream(templateResource);
        if (templateIn == null) {
            String errorMessage = "The templateResource (" + templateResource
                    + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
            if (templateResource.startsWith("/")) {
                errorMessage += "\nAs from 6.1, a classpath resource should not start with a slash (/)."
                        + " A templateResource now adheres to ClassLoader.getResource(String)."
                        + " Remove the leading slash from the templateResource if you're upgrading from 6.0.";
            }
            throw new IllegalArgumentException(errorMessage);
        }
        return configure(templateIn, model);
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(File templateFile) {

        try {
            return configure(new FileInputStream(templateFile));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The templateFile (" + templateFile + ") was not found.", e);
        }
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(File templateFile, Object model) {
        try {
            return configure(new FileInputStream(templateFile), model);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The templateFile (" + templateFile + ") was not found.", e);
        }
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(InputStream templateIn) {
        return configure(templateIn, null);
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(InputStream templateIn, Object model) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(templateIn, "UTF-8");
            return configure(reader, model);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(templateIn);
        }
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(Reader templateReader) {
        return configure(templateReader, null);
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(Reader templateReader, Object model) {
        Configuration freemarkerCfg = new Configuration();
        freemarkerCfg.setDefaultEncoding("UTF-8");
        // Write each number according to Java language spec (as expected by XStream), so not formatted by locale
        freemarkerCfg.setNumberFormat("computer");
        // Write each date according to OSI standard (as expected by XStream)
        freemarkerCfg.setDateFormat("yyyy-mm-dd");
        // Write each datetime in format expected by XStream
        freemarkerCfg.setDateTimeFormat("yyyy-mm-dd HH:mm:ss.SSS z");
        // Write each time in format expected by XStream
        freemarkerCfg.setTimeFormat("HH:mm:ss.SSS");
        String templateFilename = "benchmarkTemplate.ftl";
        Template template;
        try {
            template = new Template(templateFilename, templateReader, freemarkerCfg, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Can not read template (" + templateFilename + ") from templateReader.", e);
        }
        return configure(template, model);
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(Template template) {
        return configure(template, null);
    }

    public FreemarkerXmlPlannerBenchmarkFactory configure(Template template, Object model) {
        StringWriter configWriter = new StringWriter();
        try {
            template.process(model, configWriter);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not write to configWriter.", e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Can not process Freemarker template to configWriter.", e);
        } finally {
            IOUtils.closeQuietly(configWriter);
        }
        StringReader configReader = new StringReader(configWriter.toString());
        xmlPlannerBenchmarkFactory.configure(configReader);
        return this;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public PlannerBenchmarkConfig getPlannerBenchmarkConfig() {
        return xmlPlannerBenchmarkFactory.getPlannerBenchmarkConfig();
    }

    public PlannerBenchmark buildPlannerBenchmark() {
        return xmlPlannerBenchmarkFactory.buildPlannerBenchmark();
    }

}
