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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.api.PlannerBenchmark;
import org.drools.planner.config.XmlSolverFactory;

import com.thoughtworks.xstream.XStream;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class XmlPlannerBenchmarkFactory {

    private XStream xStream;
    private PlannerBenchmarkConfig plannerBenchmarkConfig = null;

    public XmlPlannerBenchmarkFactory() {
        xStream = XmlSolverFactory.buildXstream();
        xStream.processAnnotations(PlannerBenchmarkConfig.class);
    }

    public void addXstreamAnnotations(Class... xstreamAnnotations) {
        xStream.processAnnotations(xstreamAnnotations);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public XmlPlannerBenchmarkFactory configure(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        if (in == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
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

    public XmlPlannerBenchmarkFactory configureFromTemplate(InputStream in) {
        return this.configureFromTemplate(in, null);
    }

    public XmlPlannerBenchmarkFactory configureFromTemplate(Reader reader) {
        return this.configureFromTemplate(reader, null);
    }

    public XmlPlannerBenchmarkFactory configureFromTemplate(InputStream in, Object model) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
            return configureFromTemplate(reader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support UTF-8 encoding.", e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(in);
        }
    }

    public XmlPlannerBenchmarkFactory configureFromTemplate(Reader reader, Object model) {
        Configuration cfg = new Configuration();
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            return this.configureFromTemplate(new Template("benchmarkTemplate.ftl", reader, cfg, "UTF-8"), model);
        } catch (IOException e) {
            throw new IllegalStateException("The template for a benchmark cannot be read.", e);
        }
    }

    public XmlPlannerBenchmarkFactory configureFromTemplate(Template template, Object model) {
        StringWriter out = new StringWriter();
        try {
            template.process(model, out);
            return this.configure(new StringReader(out.toString()));
        } catch (TemplateException e) {
            throw new IllegalStateException("There was a problem with the benchmark template.", e);
        } catch (IOException e) {
            throw new IllegalStateException("There was a problem writing the benchmark config from the template.", e);
        }
    }

    public XmlPlannerBenchmarkFactory configureFromTemplate(Template template) {
        return this.configureFromTemplate(template, null);
    }

    public XmlPlannerBenchmarkFactory configure(Reader reader) {
        plannerBenchmarkConfig = (PlannerBenchmarkConfig) xStream.fromXML(reader);
        return this;
    }

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
