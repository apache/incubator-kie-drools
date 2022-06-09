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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.blueprint.SolverBenchmarkBluePrintConfig;
import org.optaplanner.benchmark.config.report.BenchmarkReportConfig;
import org.optaplanner.benchmark.impl.io.PlannerBenchmarkConfigIO;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.io.OptaPlannerXmlSerializationException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * To read it from XML, use {@link #createFromXmlResource(String)}.
 * To build a {@link PlannerBenchmarkFactory} with it, use {@link PlannerBenchmarkFactory#create(PlannerBenchmarkConfig)}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = PlannerBenchmarkConfig.XML_ELEMENT_NAME)
@XmlType(propOrder = {
        "name",
        "benchmarkDirectory",
        "threadFactoryClass",
        "parallelBenchmarkCount",
        "warmUpMillisecondsSpentLimit",
        "warmUpSecondsSpentLimit",
        "warmUpMinutesSpentLimit",
        "warmUpHoursSpentLimit",
        "warmUpDaysSpentLimit",
        "benchmarkReportConfig",
        "inheritedSolverBenchmarkConfig",
        "solverBenchmarkBluePrintConfigList",
        "solverBenchmarkConfigList"
})
public class PlannerBenchmarkConfig {
    public static final String SOLVER_NAMESPACE_PREFIX = "solver";
    public static final String XML_ELEMENT_NAME = "plannerBenchmark";
    public static final String XML_NAMESPACE = "https://www.optaplanner.org/xsd/benchmark";

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
     *
     * @param benchmarkConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlResource(String benchmarkConfigResource) {
        return createFromXmlResource(benchmarkConfigResource, null);
    }

    /**
     * As defined by {@link #createFromXmlResource(String)}.
     *
     * @param benchmarkConfigResource never null, a classpath resource
     *        as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlResource(String benchmarkConfigResource, ClassLoader classLoader) {
        ClassLoader actualClassLoader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
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
        } catch (OptaPlannerXmlSerializationException e) {
            throw new IllegalArgumentException("Unmarshalling of benchmarkConfigResource (" + benchmarkConfigResource
                    + ") fails.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigResource (" + benchmarkConfigResource + ") fails.",
                    e);
        }
    }

    /**
     * Reads an XML benchmark configuration from the file system.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromXmlResource(String)} instead.
     *
     * @param benchmarkConfigFile never null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlFile(File benchmarkConfigFile) {
        return createFromXmlFile(benchmarkConfigFile, null);
    }

    /**
     * As defined by {@link #createFromXmlFile(File)}.
     *
     * @param benchmarkConfigFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlFile(File benchmarkConfigFile, ClassLoader classLoader) {
        try (InputStream in = new FileInputStream(benchmarkConfigFile)) {
            return createFromXmlInputStream(in, classLoader);
        } catch (OptaPlannerXmlSerializationException e) {
            throw new IllegalArgumentException("Unmarshalling the benchmarkConfigFile (" + benchmarkConfigFile + ") fails.", e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The benchmarkConfigFile (" + benchmarkConfigFile + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the benchmarkConfigFile (" + benchmarkConfigFile + ") fails.", e);
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
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlInputStream(InputStream in, ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            return createFromXmlReader(reader, classLoader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support the charset (" + StandardCharsets.UTF_8 + ").", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading fails.", e);
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
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromXmlReader(Reader reader, ClassLoader classLoader) {
        PlannerBenchmarkConfigIO xmlIO = new PlannerBenchmarkConfigIO();
        Object benchmarkConfigObject = xmlIO.read(reader);
        if (!(benchmarkConfigObject instanceof PlannerBenchmarkConfig)) {
            throw new IllegalArgumentException("The " + PlannerBenchmarkConfig.class.getSimpleName()
                    + "'s XML root element resolves to a different type ("
                    + (benchmarkConfigObject == null ? null : benchmarkConfigObject.getClass().getSimpleName()) + ")."
                    + (benchmarkConfigObject instanceof SolverConfig
                            ? "\nMaybe use " + PlannerBenchmarkFactory.class.getSimpleName()
                                    + ".createFromSolverConfigXmlResource() instead."
                            : ""));
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
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource) {
        return createFromFreemarkerXmlResource(templateResource, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, ClassLoader classLoader) {
        return createFromFreemarkerXmlResource(templateResource, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, Object model) {
        return createFromFreemarkerXmlResource(templateResource, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlResource(String)}.
     *
     * @param templateResource never null, a classpath resource as defined by {@link ClassLoader#getResource(String)}
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlResource(String templateResource, Object model,
            ClassLoader classLoader) {
        ClassLoader actualClassLoader = classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader();
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
            throw new IllegalArgumentException("Reading the templateResource (" + templateResource + ") fails.", e);
        }
    }

    /**
     * Reads a Freemarker XML benchmark configuration from the file system.
     * <p>
     * Warning: this leads to platform dependent code,
     * it's recommend to use {@link #createFromFreemarkerXmlResource(String)} instead.
     *
     * @param templateFile never null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile) {
        return createFromFreemarkerXmlFile(templateFile, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, ClassLoader classLoader) {
        return createFromFreemarkerXmlFile(templateFile, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, Object model) {
        return createFromFreemarkerXmlFile(templateFile, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlFile(File)}.
     *
     * @param templateFile never null
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlFile(File templateFile, Object model, ClassLoader classLoader) {
        try (FileInputStream templateIn = new FileInputStream(templateFile)) {
            return createFromFreemarkerXmlInputStream(templateIn, model, classLoader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("The templateFile (" + templateFile + ") was not found.", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading the templateFile (" + templateFile + ") fails.", e);
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
     *
     * @param templateIn never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, ClassLoader classLoader) {
        return createFromFreemarkerXmlInputStream(templateIn, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlInputStream(InputStream)}.
     *
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, Object model) {
        return createFromFreemarkerXmlInputStream(templateIn, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlInputStream(InputStream)}.
     *
     * @param templateIn never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlInputStream(InputStream templateIn, Object model,
            ClassLoader classLoader) {
        try (Reader reader = new InputStreamReader(templateIn, StandardCharsets.UTF_8)) {
            return createFromFreemarkerXmlReader(reader, model, classLoader);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("This vm does not support the charset (" + StandardCharsets.UTF_8 + ").", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Reading fails.", e);
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
     *
     * @param templateReader never null, gets closed
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, ClassLoader classLoader) {
        return createFromFreemarkerXmlReader(templateReader, null, classLoader);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlReader(Reader)}.
     *
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, Object model) {
        return createFromFreemarkerXmlReader(templateReader, model, null);
    }

    /**
     * As defined by {@link #createFromFreemarkerXmlReader(Reader)}.
     *
     * @param templateReader never null, gets closed
     * @param model sometimes null
     * @param classLoader sometimes null, the {@link ClassLoader} to use for loading all resources and {@link Class}es,
     *        null to use the default {@link ClassLoader}
     * @return never null
     */
    public static PlannerBenchmarkConfig createFromFreemarkerXmlReader(Reader templateReader, Object model,
            ClassLoader classLoader) {
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_31);
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

    @XmlTransient
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

    @XmlElement(name = "benchmarkReport")
    private BenchmarkReportConfig benchmarkReportConfig = null;

    @XmlElement(name = "inheritedSolverBenchmark")
    private SolverBenchmarkConfig inheritedSolverBenchmarkConfig = null;

    @XmlElement(name = "solverBenchmarkBluePrint")
    private List<SolverBenchmarkBluePrintConfig> solverBenchmarkBluePrintConfigList = null;

    @XmlElement(name = "solverBenchmark")
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
     *
     * @return null, a number or {@value #PARALLEL_BENCHMARK_COUNT_AUTO}.
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
}
