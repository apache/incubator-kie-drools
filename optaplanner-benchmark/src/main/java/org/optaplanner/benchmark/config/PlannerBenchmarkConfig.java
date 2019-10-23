/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.ConversionException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.solver.io.XStreamConfigReader;
import org.optaplanner.core.impl.solver.thread.DefaultSolverThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.ObjectUtils.*;

/**
 * To read it from XML, use {@link #createFromXmlResource(String)}.
 * To build a {@link PlannerBenchmarkFactory} with it, use {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}.
 */
@XStreamAlias("plannerBenchmark")
public class PlannerBenchmarkConfig {

    // ************************************************************************
    // Static creation methods: SolverConfig
    // ************************************************************************

    /**
     * @param solverConfig never null
     */
    public static PlannerBenchmarkConfig createFromSolverConfig(SolverConfig solverConfig) {
        return createFromSolverConfig(solverConfig, new File("local/benchmarkReport"));
    }

    /**
     * @param solverConfig never null
     * @param benchmarkDirectory never null
     */
    public static PlannerBenchmarkConfig createFromSolverConfig(SolverConfig solverConfig,
            File benchmarkDirectory) {
        PlannerBenchmarkConfig plannerBenchmarkConfig = new PlannerBenchmarkConfig();
        plannerBenchmarkConfig.setBenchmarkDirectory(benchmarkDirectory);
        SolverBenchmarkConfig solverBenchmarkConfig = new SolverBenchmarkConfig();
        // Defensive copy of solverConfig
        solverBenchmarkConfig.setSolverConfig(new SolverConfig(solverConfig));
        plannerBenchmarkConfig.setInheritedSolverBenchmarkConfig(solverBenchmarkConfig);
        plannerBenchmarkConfig.setSolverBenchmarkConfigList(Collections.singletonList(new SolverBenchmarkConfig()));
        return plannerBenchmarkConfig;
    }

    // ************************************************************************
    // Static creation methods: XML
    // ************************************************************************

    /**
     * Reads an XML benchmark configuration from the classpath.
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlResource(String benchmarkConfigResource) {
        return createFromXmlResource(benchmarkConfigResource, null);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     * @param benchmarkConfigResource never null, a classpath resource
     * as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlResource(String benchmarkConfigResource, ClassLoader classLoader) {
        ClassLoader actualClassLoader = classLoader != null ? classLoader : PlannerBenchmarkConfig.class.getClassLoader();
        try (InputStream in = actualClassLoader.getResourceAsStream(benchmarkConfigResource)) {
            if (in == null) {
                String errorMessage = "The benchmarkConfigResource (" + benchmarkConfigResource
                        + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
                if (benchmarkConfigResource.startsWith("/")) {
                    errorMessage += "\nA classpath resource should not start with a slash (/)."
                            + " A benchmarkConfigResource adheres to ClassLoader.getResource(String)."
                            + " Maybe remove the leading slash from the benchmarkConfigResource.";
                }
                throw new IllegalArgumentException(errorMessage);
            }
            return createFromXmlInputStream(in, classLoader);
        } catch (ConversionException e) {
            String lineNumber = e.get("line number");
            throw new IllegalArgumentException("Unmarshalling of benchmarkConfigResource (" + benchmarkConfigResource
                    + ") fails on line number (" + lineNumber + ")."
                    + (Objects.equals(e.get("required-type"), "java.lang.Class")
                    ? "\n  Maybe the classname on line number (" + lineNumber + ") is surrounded by whitespace, which is invalid."
                    : ""), e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigResource (" + benchmarkConfigResource + ") failed.", e);
        }
    }

    /**
     * Reads an XML benchmark configuration from the file system.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlFile(File benchmarkConfigFile) {
        return createFromXmlFile(benchmarkConfigFile, null);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     * @param benchmarkConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlFile(File benchmarkConfigFile, ClassLoader classLoader) {
        try (InputStream in = new FileInputStream(benchmarkConfigFile)) {
            return createFromXmlInputStream(in, classLoader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The benchmarkConfigFile (" + benchmarkConfigFile
                    + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigFile (" + benchmarkConfigFile + ") failed.", e);
        }
    }

    /**
     * @param in never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlInputStream(InputStream in) {
        return createFromXmlInputStream(in, null);
    }

    /**
     * @param in never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return createFromXmlReader(reader, classLoader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support the charset (" + StandardCharsets.UTF_8 + ").", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading failed.", e);
        }
    }

    /**
     * @param reader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlReader(Reader reader) {
        return createFromXmlReader(reader, null);
    }

    /**
     * @param reader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlReader(Reader reader, ClassLoader classLoader) {
        XStream xStream = XStreamConfigReader.buildXStreamPortable(classLoader, PlannerBenchmarkConfig.class);
        Object benchmarkConfigObject = xStream.fromXML(reader);
        if (!(benchmarkConfigObject instanceof PlannerBenchmarkConfig)) {
            throw new IllegalArgumentException("The " + PlannerBenchmarkConfig.class.getSimpleName()
                    + "'s XML root element resolves to a different type ("
                    + (benchmarkConfigObject == null ? null : benchmarkConfigObject.getClass().getSimpleName()) + ")."
                    + (benchmarkConfigObject instanceof SolverConfig ?
                    "\nMaybe use " + PlannerBenchmarkFactory.class.getSimpleName()
                    + ".createFromSolverConfigXmlResource() instead." : ""));
        }
        PlannerBenchmarkConfig benchmarkConfig = (PlannerBenchmarkConfig) benchmarkConfigObject;
        benchmarkConfig.setClassLoader(classLoader);
        return benchmarkConfig;
    }

    // ************************************************************************
    // Static creation methods: Freemarker XML
    // ************************************************************************

    /**
     * Reads a Freemarker XML benchmark configuration from the classpath.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource) {
        return createFromFreemarkerXmlResource(templateResource, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, ClassLoader classLoader) {
        return createFromFreemarkerXmlResource(templateResource, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, Object model) {
        return createFromFreemarkerXmlResource(templateResource, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, Object model, ClassLoader classLoader) {
        ClassLoader actualClassLoader = classLoader != null ? classLoader : PlannerBenchmarkConfig.class.getClassLoader();
        try (InputStream templateIn = actualClassLoader.getResourceAsStream(templateResource)) {
            if (templateIn == null) {
                String errorMessage = "The templateResource (" + templateResource
                        + ") does not exist as a classpath resource in the classLoader (" + actualClassLoader + ").";
                if (templateResource.startsWith("/")) {
                    errorMessage += "\nA classpath resource should not start with a slash (/)."
                            + " A templateResource adheres to ClassLoader.getResource(String)."
                            + " Maybe remove the leading slash from the templateResource.";
                }
                throw new IllegalArgumentException(errorMessage);
            }
            return createFromFreemarkerXmlInputStream(templateIn, model, classLoader);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the templateResource (" + templateResource + ") failed.", e);
        }
    }

    /**
     * Reads a Freemarker XML benchmark configuration from the file system.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromFreemarkerXmlResource(String)} instead.
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile) {
        return createFromFreemarkerXmlFile(templateFile, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     * @param templateFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, ClassLoader classLoader) {
        return createFromFreemarkerXmlFile(templateFile, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     * @param templateFile never null
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, Object model) {
        return createFromFreemarkerXmlFile(templateFile, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     * @param templateFile never null
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, Object model, ClassLoader classLoader) {
        try (FileInputStream templateIn = new FileInputStream(templateFile)) {
            return createFromFreemarkerXmlInputStream(templateIn, model, classLoader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The templateFile (" + templateFile + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the templateFile (" + templateFile + ") failed.", e);
        }
    }

    /**
     * @param templateIn never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn) {
        return createFromFreemarkerXmlInputStream(templateIn, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlInputStream(InputStream)}.
     * @param templateIn never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, ClassLoader classLoader) {
        return createFromFreemarkerXmlInputStream(templateIn, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlInputStream(InputStream)}.
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, Object model) {
        return createFromFreemarkerXmlInputStream(templateIn, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlInputStream(InputStream)}.
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, Object model, ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(templateIn, StandardCharsets.UTF_8)) {
            return createFromFreemarkerXmlReader(reader, model, classLoader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support the charset (" + StandardCharsets.UTF_8 + ").", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading failed.", e);
        }
    }

    /**
     * @param templateReader never null, gets closed
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader) {
        return createFromFreemarkerXmlReader(templateReader, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlReader(Reader)}.
     * @param templateReader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, ClassLoader classLoader) {
        return createFromFreemarkerXmlReader(templateReader, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlReader(Reader)}.
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, Object model) {
        return createFromFreemarkerXmlReader(templateReader, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlReader(Reader)}.
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     * null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, Object model, ClassLoader classLoader) {
        Configuration freemarkerConfiguration = new Configuration();
        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        // Write each number according to Java language spec (as expected by XStream), so not formatted by locale
        freemarkerConfiguration.setNumberFormat("computer");
        // Write each date according to OSI standard (as expected by XStream)
        freemarkerConfiguration.setDateFormat("yyyy-mm-dd");
        // Write each datetime in format expected by XStream
        freemarkerConfiguration.setDateTimeFormat("yyyy-mm-dd HH:mm:ss.SSS z");
        // Write each time in format expected by XStream
        freemarkerConfiguration.setTimeFormat("HH:mm:ss.SSS");
        Template template;
        try {
            template = new Template("benchmarkTemplate.ftl", templateReader, freemarkerConfiguration, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Can not read the Freemarker template from templateReader.", e);
        }
        String xmlContent;
        try (StringWriter xmlContentWriter = new StringWriter()) {
            template.process(model, xmlContentWriter);
            xmlContent = xmlContentWriter.toString();
        } catch (TemplateException | IOException e) {
            throw new IllegalArgumentException("Can not process the Freemarker template into xmlContentWriter.", e);
        }
        try (StringReader configReader = new StringReader(xmlContent)) {
            return createFromXmlReader(configReader, classLoader);
        }
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    public static final String PARALLEL_BENCHMARK_COUNT_AUTO = "AUTO";
    public static final Pattern VALID_NAME_PATTERN = Pattern.compile("(?U)^[\\w\\d _\\-\\.\\(\\)]+$");

    private static final Logger logger = LoggerFactory.getLogger(PlannerBenchmarkConfig.class);

    @XStreamOmitField
    private ClassLoader classLoader = null;

    private String name = null;
    private File benchmarkDirectory = null;

    private Class<? extends ThreadFactory> threadFactoryClass = null;
    private String parallelBenchmarkCount = null;
    private Long warmUpMillisecondsSpentLimit = null;
    private Long warmUpSecondsSpentLimit = null;
    private Long warmUpMinutesSpentLimit = null;
    private Long warmUpHoursSpentLimit = null;
    private Long warmUpDaysSpentLimit = null;

    @XStreamAlias("benchmarkReport")
    private BenchmarkReportConfig benchmarkReportConfig = null;

    @XStreamAlias("inheritedSolverBenchmark")
    private SolverBenchmarkConfig inheritedSolverBenchmarkConfig = null;

    @XStreamImplicit(itemFieldName = "solverBenchmarkBluePrint")
    private List<SolverBenchmarkBluePrintConfig> solverBenchmarkBluePrintConfigList = null;
    @XStreamImplicit(itemFieldName = "solverBenchmark")
    private List<SolverBenchmarkConfig> solverBenchmarkConfigList = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    /**
     * Create an empty benchmark config.
     */
    public PlannerBenchmarkConfig() {
    }

    /**
     * @param classLoader sometimes null
     */
    public PlannerBenchmarkConfig(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getBenchmarkDirectory() {
        return benchmarkDirectory;
    }

    public void setBenchmarkDirectory(File benchmarkDirectory) {
        this.benchmarkDirectory = benchmarkDirectory;
    }

    public Class<? extends ThreadFactory> getThreadFactoryClass() {
        return threadFactoryClass;
    }

    public void setThreadFactoryClass(Class<? extends ThreadFactory> threadFactoryClass) {
        this.threadFactoryClass = threadFactoryClass;
    }

    /**
     * Using multiple parallel benchmarks can decrease the reliability of the results.
     * <p>
     * If there aren't enough processors available, it will be decreased.
     * @return null, a number, {@value #PARALLEL_BENCHMARK_COUNT_AUTO} or a JavaScript calculation using
     * {@value org.optaplanner.core.config.util.ConfigUtils#AVAILABLE_PROCESSOR_COUNT}.
     */
    public String getParallelBenchmarkCount() {
        return parallelBenchmarkCount;
    }

    public void setParallelBenchmarkCount(String parallelBenchmarkCount) {
        this.parallelBenchmarkCount = parallelBenchmarkCount;
    }

    public Long getWarmUpMillisecondsSpentLimit() {
        return warmUpMillisecondsSpentLimit;
    }

    public void setWarmUpMillisecondsSpentLimit(Long warmUpMillisecondsSpentLimit) {
        this.warmUpMillisecondsSpentLimit = warmUpMillisecondsSpentLimit;
    }

    public Long getWarmUpSecondsSpentLimit() {
        return warmUpSecondsSpentLimit;
    }

    public void setWarmUpSecondsSpentLimit(Long warmUpSecondsSpentLimit) {
        this.warmUpSecondsSpentLimit = warmUpSecondsSpentLimit;
    }

    public Long getWarmUpMinutesSpentLimit() {
        return warmUpMinutesSpentLimit;
    }

    public void setWarmUpMinutesSpentLimit(Long warmUpMinutesSpentLimit) {
        this.warmUpMinutesSpentLimit = warmUpMinutesSpentLimit;
    }

    public Long getWarmUpHoursSpentLimit() {
        return warmUpHoursSpentLimit;
    }

    public void setWarmUpHoursSpentLimit(Long warmUpHoursSpentLimit) {
        this.warmUpHoursSpentLimit = warmUpHoursSpentLimit;
    }

    public Long getWarmUpDaysSpentLimit() {
        return warmUpDaysSpentLimit;
    }

    public void setWarmUpDaysSpentLimit(Long warmUpDaysSpentLimit) {
        this.warmUpDaysSpentLimit = warmUpDaysSpentLimit;
    }

    public BenchmarkReportConfig getBenchmarkReportConfig() {
        return benchmarkReportConfig;
    }

    public void setBenchmarkReportConfig(BenchmarkReportConfig benchmarkReportConfig) {
        this.benchmarkReportConfig = benchmarkReportConfig;
    }

    public SolverBenchmarkConfig getInheritedSolverBenchmarkConfig() {
        return inheritedSolverBenchmarkConfig;
    }

    public void setInheritedSolverBenchmarkConfig(SolverBenchmarkConfig inheritedSolverBenchmarkConfig) {
        this.inheritedSolverBenchmarkConfig = inheritedSolverBenchmarkConfig;
    }

    public List<SolverBenchmarkBluePrintConfig> getSolverBenchmarkBluePrintConfigList() {
        return solverBenchmarkBluePrintConfigList;
    }

    public void setSolverBenchmarkBluePrintConfigList(List<SolverBenchmarkBluePrintConfig> solverBenchmarkBluePrintConfigList) {
        this.solverBenchmarkBluePrintConfigList = solverBenchmarkBluePrintConfigList;
    }

    public List<SolverBenchmarkConfig> getSolverBenchmarkConfigList() {
        return solverBenchmarkConfigList;
    }

    public void setSolverBenchmarkConfigList(List<SolverBenchmarkConfig> solverBenchmarkConfigList) {
        this.solverBenchmarkConfigList = solverBenchmarkConfigList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    /**
     * Do not use this method, it is an internal method.
     * Use {@link PlannerBenchmarkFactory#buildPlannerBenchmark()} instead.
     * <p>
     * Will be removed in 8.0.
     * @param solverConfigContext never null
     * @return never null
     */
    public PlannerBenchmark buildPlannerBenchmark(SolverConfigContext solverConfigContext) {
        return buildPlannerBenchmark(solverConfigContext, new Object[0]);
    }

    /**
     * Do not use this method, it is an internal method.
     * Use {@link PlannerBenchmarkFactory#buildPlannerBenchmark(Object[])} instead.
     * <p>
     * Will be removed in 8.0.
     * @param solverConfigContext never null
     * @param extraProblems never null
     * @return never null
     */
    public <Solution_> PlannerBenchmark buildPlannerBenchmark(SolverConfigContext solverConfigContext,
            Solution_[] extraProblems) {
        validate();
        generateSolverBenchmarkConfigNames();
        List<SolverBenchmarkConfig> effectiveSolverBenchmarkConfigList = buildEffectiveSolverBenchmarkConfigList();

        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setName(name);
        plannerBenchmarkResult.setAggregation(false);
        int parallelBenchmarkCount = resolveParallelBenchmarkCount();
        plannerBenchmarkResult.setParallelBenchmarkCount(parallelBenchmarkCount);
        plannerBenchmarkResult.setWarmUpTimeMillisSpentLimit(defaultIfNull(calculateWarmUpTimeMillisSpentLimit(), 30L));
        plannerBenchmarkResult.setUnifiedProblemBenchmarkResultList(new ArrayList<>());
        plannerBenchmarkResult.setSolverBenchmarkResultList(new ArrayList<>(
                effectiveSolverBenchmarkConfigList.size()));
        for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
            solverBenchmarkConfig.buildSolverBenchmark(solverConfigContext, classLoader, plannerBenchmarkResult, extraProblems);
        }

        BenchmarkReportConfig benchmarkReportConfig_ = benchmarkReportConfig == null ? new BenchmarkReportConfig()
                : benchmarkReportConfig;
        BenchmarkReport benchmarkReport = benchmarkReportConfig_.buildBenchmarkReport(plannerBenchmarkResult);
        return new DefaultPlannerBenchmark(
                plannerBenchmarkResult, solverConfigContext, benchmarkDirectory,
                buildExecutorService(parallelBenchmarkCount), buildExecutorService(parallelBenchmarkCount),
                benchmarkReport);
    }

    private ExecutorService buildExecutorService(int parallelBenchmarkCount) {
        ThreadFactory threadFactory;
        if (threadFactoryClass != null) {
            threadFactory = ConfigUtils.newInstance(this, "threadFactoryClass", threadFactoryClass);
        } else {
            threadFactory = new DefaultSolverThreadFactory("BenchmarkThread");
        }
        return Executors.newFixedThreadPool(parallelBenchmarkCount, threadFactory);
    }

    protected void validate() {
        if (name != null) {
            if (!PlannerBenchmarkConfig.VALID_NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalStateException("The plannerBenchmark name (" + name
                        + ") is invalid because it does not follow the nameRegex ("
                        + PlannerBenchmarkConfig.VALID_NAME_PATTERN.pattern() + ")" +
                        " which might cause an illegal filename.");
            }
            if (!name.trim().equals(name)) {
                throw new IllegalStateException("The plannerBenchmark name (" + name
                        + ") is invalid because it starts or ends with whitespace.");
            }
        }
        if (ConfigUtils.isEmptyCollection(solverBenchmarkBluePrintConfigList)
                && ConfigUtils.isEmptyCollection(solverBenchmarkConfigList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <solverBenchmark> (or 1 <solverBenchmarkBluePrint>)"
                    + " in the <plannerBenchmark> configuration.");
        }
    }

    protected void generateSolverBenchmarkConfigNames() {
        if (solverBenchmarkConfigList != null) {
            Set<String> nameSet = new HashSet<>(solverBenchmarkConfigList.size());
            Set<SolverBenchmarkConfig> noNameBenchmarkConfigSet = new LinkedHashSet<>(solverBenchmarkConfigList.size());
            for (SolverBenchmarkConfig solverBenchmarkConfig : solverBenchmarkConfigList) {
                if (solverBenchmarkConfig.getName() != null) {
                    boolean unique = nameSet.add(solverBenchmarkConfig.getName());
                    if (!unique) {
                        throw new IllegalStateException("The benchmark name (" + solverBenchmarkConfig.getName()
                                + ") is used in more than 1 benchmark.");
                    }
                } else {
                    noNameBenchmarkConfigSet.add(solverBenchmarkConfig);
                }
            }
            int generatedNameIndex = 0;
            for (SolverBenchmarkConfig solverBenchmarkConfig : noNameBenchmarkConfigSet) {
                String generatedName = "Config_" + generatedNameIndex;
                while (nameSet.contains(generatedName)) {
                    generatedNameIndex++;
                    generatedName = "Config_" + generatedNameIndex;
                }
                solverBenchmarkConfig.setName(generatedName);
                generatedNameIndex++;
            }
        }
    }

    protected List<SolverBenchmarkConfig> buildEffectiveSolverBenchmarkConfigList() {
        List<SolverBenchmarkConfig> effectiveSolverBenchmarkConfigList = new ArrayList<>(0);
        if (solverBenchmarkConfigList != null) {
            effectiveSolverBenchmarkConfigList.addAll(solverBenchmarkConfigList);
        }
        if (solverBenchmarkBluePrintConfigList != null) {
            for (SolverBenchmarkBluePrintConfig solverBenchmarkBluePrintConfig : solverBenchmarkBluePrintConfigList) {
                effectiveSolverBenchmarkConfigList.addAll(
                        solverBenchmarkBluePrintConfig.buildSolverBenchmarkConfigList());
            }
        }
        if (inheritedSolverBenchmarkConfig != null) {
            for (SolverBenchmarkConfig solverBenchmarkConfig : effectiveSolverBenchmarkConfigList) {
                // Side effect: changes the unmarshalled solverBenchmarkConfig
                solverBenchmarkConfig.inherit(inheritedSolverBenchmarkConfig);
            }
        }
        return effectiveSolverBenchmarkConfigList;
    }

    protected int resolveParallelBenchmarkCount() {
        int availableProcessorCount = Runtime.getRuntime().availableProcessors();
        int resolvedParallelBenchmarkCount;
        if (parallelBenchmarkCount == null) {
            resolvedParallelBenchmarkCount = 1;
        } else if (parallelBenchmarkCount.equals(PARALLEL_BENCHMARK_COUNT_AUTO)) {
            resolvedParallelBenchmarkCount = resolveParallelBenchmarkCountAutomatically(availableProcessorCount);
        } else {
            resolvedParallelBenchmarkCount = ConfigUtils.resolveThreadPoolSizeScript(
                    "parallelBenchmarkCount", parallelBenchmarkCount, PARALLEL_BENCHMARK_COUNT_AUTO);
        }
        if (resolvedParallelBenchmarkCount < 1) {
            throw new IllegalArgumentException("The parallelBenchmarkCount (" + parallelBenchmarkCount
                    + ") resulted in a resolvedParallelBenchmarkCount (" + resolvedParallelBenchmarkCount
                    + ") that is lower than 1.");
        }
        if (resolvedParallelBenchmarkCount > availableProcessorCount) {
            logger.warn("Because the resolvedParallelBenchmarkCount ({}) is higher "
                    + "than the availableProcessorCount ({}), it is reduced to "
                    + "availableProcessorCount.", resolvedParallelBenchmarkCount, availableProcessorCount);
            resolvedParallelBenchmarkCount = availableProcessorCount;
        }
        return resolvedParallelBenchmarkCount;
    }

    protected int resolveParallelBenchmarkCountAutomatically(int availableProcessorCount) {
        // Tweaked based on experience
        if (availableProcessorCount <= 2) {
            return 1;
        } else if (availableProcessorCount <= 4) {
            return 2;
        } else {
            return (availableProcessorCount / 2) + 1;
        }
    }

    protected Long calculateWarmUpTimeMillisSpentLimit() {
        if (warmUpMillisecondsSpentLimit == null && warmUpSecondsSpentLimit == null
                && warmUpMinutesSpentLimit == null && warmUpHoursSpentLimit == null && warmUpDaysSpentLimit == null) {
            return null;
        }
        long warmUpTimeMillisSpentLimit = 0L;
        if (warmUpMillisecondsSpentLimit != null) {
            if (warmUpMillisecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpMillisecondsSpentLimit (" + warmUpMillisecondsSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpMillisecondsSpentLimit;
        }
        if (warmUpSecondsSpentLimit != null) {
            if (warmUpSecondsSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpSecondsSpentLimit (" + warmUpSecondsSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpSecondsSpentLimit * 1_000L;
        }
        if (warmUpMinutesSpentLimit != null) {
            if (warmUpMinutesSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpMinutesSpentLimit (" + warmUpMinutesSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpMinutesSpentLimit * 60_000L;
        }
        if (warmUpHoursSpentLimit != null) {
            if (warmUpHoursSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpHoursSpentLimit (" + warmUpHoursSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpHoursSpentLimit * 3_600_000L;
        }
        if (warmUpDaysSpentLimit != null) {
            if (warmUpDaysSpentLimit < 0L) {
                throw new IllegalArgumentException("The warmUpDaysSpentLimit (" + warmUpDaysSpentLimit
                        + ") cannot be negative.");
            }
            warmUpTimeMillisSpentLimit += warmUpDaysSpentLimit * 86_400_000L;
        }
        return warmUpTimeMillisSpentLimit;
    }

}
