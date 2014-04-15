/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.benchmark.api;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.impl.FreemarkerXmlPlannerBenchmarkFactory;
import org.optaplanner.benchmark.impl.XStreamXmlPlannerBenchmarkFactory;

/**
 * Builds {@link PlannerBenchmark} instances.
 * <p/>
 * Supports tweaking the configuration programmatically before a {@link PlannerBenchmark} instance is build.
 */
public abstract class PlannerBenchmarkFactory {

    // ************************************************************************
    // Static creation methods
    // ************************************************************************

    /**
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlResource(String benchmarkConfigResource) {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(benchmarkConfigResource);
        return plannerBenchmarkFactory;
    }

    /**
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlFile(File benchmarkConfigFile) {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(benchmarkConfigFile);
        return plannerBenchmarkFactory;
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlInputStream(InputStream in) {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(in);
        return plannerBenchmarkFactory;
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromXmlReader(Reader reader) {
        XStreamXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new XStreamXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(reader);
        return plannerBenchmarkFactory;
    }

    // ************************************************************************
    // Static creation methods with Freemarker
    // ************************************************************************

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource) {
        return createFromFreemarkerXmlResource(templateResource, null);
    }

    /**
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlResource(String templateResource, Object model) {
        FreemarkerXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new FreemarkerXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(templateResource, model);
        return plannerBenchmarkFactory;
    }

    /**
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile) {
        return createFromFreemarkerXmlFile(templateFile, null);
    }

    /**
     * @param templateFile never null
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlFile(File templateFile, Object model) {
        FreemarkerXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new FreemarkerXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(templateFile, model);
        return plannerBenchmarkFactory;
    }

    /**
     * @param templateIn never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn) {
        return createFromFreemarkerXmlInputStream(templateIn, null);
    }

    /**
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlInputStream(InputStream templateIn, Object model) {
        FreemarkerXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new FreemarkerXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(templateIn, model);
        return plannerBenchmarkFactory;
    }

    /**
     * @param templateReader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader) {
        return createFromFreemarkerXmlReader(templateReader, null);
    }

    /**
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkFactory createFromFreemarkerXmlReader(Reader templateReader, Object model) {
        FreemarkerXmlPlannerBenchmarkFactory plannerBenchmarkFactory = new FreemarkerXmlPlannerBenchmarkFactory();
        plannerBenchmarkFactory.configure(templateReader, model);
        return plannerBenchmarkFactory;
    }

    // ************************************************************************
    // Interface methods
    // ************************************************************************

    /**
     * Allows you to problematically change the {@link PlannerBenchmarkConfig} at runtime before building
     * the {@link PlannerBenchmark}.
     * @return never null
     */
    public abstract PlannerBenchmarkConfig getPlannerBenchmarkConfig();

    /**
     * Creates a new {@link PlannerBenchmark} instance.
     * @return never null
     */
    public abstract PlannerBenchmark buildPlannerBenchmark();

}
