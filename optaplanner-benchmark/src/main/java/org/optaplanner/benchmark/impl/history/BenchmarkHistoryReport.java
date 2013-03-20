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

package org.optaplanner.benchmark.impl.history;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.report.WebsiteResourceUtils;

public class BenchmarkHistoryReport {

    protected final DefaultPlannerBenchmark plannerBenchmark;

    protected File benchmarkHistoryDirectory;
    protected Locale locale;

    protected File historyHtmlFile = null;
    protected File summaryDirectory = null;

    public BenchmarkHistoryReport(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
    }

    public File getBenchmarkHistoryDirectory() {
        return benchmarkHistoryDirectory;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public File getHistoryHtmlFile() {
        return historyHtmlFile;
    }

    public File getSummaryDirectory() {
        return summaryDirectory;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void writeHistory() {
        benchmarkHistoryDirectory = new File(plannerBenchmark.getBenchmarkDirectory(), "history");
        if (benchmarkHistoryDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(benchmarkHistoryDirectory);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Can not delete old benchmarkHistoryDirectory (" + benchmarkHistoryDirectory
                        + ").", e);
            }
        }
        benchmarkHistoryDirectory.mkdir();
        summaryDirectory = new File(benchmarkHistoryDirectory, "summary");
        summaryDirectory.mkdir();
        writeHistoryHtmlFile();
    }

    private void writeHistoryHtmlFile() {
        WebsiteResourceUtils.copyResourcesTo(benchmarkHistoryDirectory);

        historyHtmlFile = new File(benchmarkHistoryDirectory, "index.html");
        Configuration freemarkerCfg = new Configuration();
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setLocale(locale);
        freemarkerCfg.setClassForTemplateLoading(BenchmarkHistoryReport.class, "");

        String templateFilename = "benchmarkHistory.html.ftl";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("benchmarkHistoryReport", this);

        Writer writer = null;
        try {
            Template template = freemarkerCfg.getTemplate(templateFilename);
            writer = new OutputStreamWriter(new FileOutputStream(historyHtmlFile), "UTF-8");
            template.process(model, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read templateFilename (" + templateFilename
                    + ") or write historyHtmlFile (" + historyHtmlFile + ").", e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Can not process Freemarker templateFilename (" + templateFilename
                    + ") to historyHtmlFile (" + historyHtmlFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
