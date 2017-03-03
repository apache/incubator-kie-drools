/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.xml.RulesSemanticModule;
import org.drools.compiler.kie.builder.impl.AbstractKieModule.CompilationCache;
import org.drools.compiler.rule.builder.DroolsCompilerComponentFactory;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import org.drools.core.base.evaluators.EvaluatorRegistry;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.ClassBuilderFactory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.ConfFileUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.xml.ChangeSetSemanticModule;
import org.drools.core.xml.DefaultSemanticModule;
import org.drools.core.xml.Handler;
import org.drools.core.xml.SemanticModule;
import org.drools.core.xml.SemanticModules;
import org.drools.core.xml.WrapperSemanticModule;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.AccumulateFunctionOption;
import org.kie.internal.builder.conf.ClassLoaderCacheOption;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.DefaultPackageNameOption;
import org.kie.internal.builder.conf.DumpDirOption;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.KBuilderSeverityOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.MultiValueKnowledgeBuilderOption;
import org.kie.internal.builder.conf.ProcessStringEscapesOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.builder.conf.SingleValueKnowledgeBuilderOption;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * This class configures the package compiler.
 * Dialects and their DialectConfigurations  are handled by the DialectRegistry
 * Normally you will not need to look at this class, unless you want to override the defaults.
 *
 * This class is not thread safe and it also contains state. Once it is created and used
 * in one or more PackageBuilders it should be considered immutable. Do not modify its
 * properties while it is being used by a PackageBuilder.
 *
 * drools.dialect.default = <String>
 * drools.accumulate.function.<function name> = <qualified class>
 * drools.evaluator.<ident> = <qualified class>
 * drools.dump.dir = <String>
 * drools.classLoaderCacheEnabled = true|false
 *
 * default dialect is java.
 * Available preconfigured Accumulate functions are:
 * drools.accumulate.function.average = org.kie.base.accumulators.AverageAccumulateFunction
 * drools.accumulate.function.max = org.kie.base.accumulators.MaxAccumulateFunction
 * drools.accumulate.function.min = org.kie.base.accumulators.MinAccumulateFunction
 * drools.accumulate.function.count = org.kie.base.accumulators.CountAccumulateFunction
 * drools.accumulate.function.sum = org.kie.base.accumulators.SumAccumulateFunction
 * 
 * drools.parser.processStringEscapes = true|false
 * 
 * 
 * drools.problem.severity.<ident> = ERROR|WARNING|INFO
 * 
 */
public class KnowledgeBuilderConfigurationImpl
        implements
        KnowledgeBuilderConfiguration {

    private Map<String, DialectConfiguration> dialectConfigurations;

    private DefaultDialectOption              defaultDialect;

    private ClassLoader                       classLoader;

    private ChainedProperties                 chainedProperties;

    private Map<String, AccumulateFunction>   accumulateFunctions;

    private EvaluatorRegistry                 evaluatorRegistry;

    private SemanticModules                   semanticModules;

    private File                              dumpDirectory;

    private boolean                           allowMultipleNamespaces = true;

    private boolean                           processStringEscapes    = true;

    private boolean                           classLoaderCache        = true;

    private static final PropertySpecificOption DEFAULT_PROP_SPEC_OPT = PropertySpecificOption.ALWAYS;
    private PropertySpecificOption            propertySpecificOption  = DEFAULT_PROP_SPEC_OPT;

    private String                            defaultPackageName;

    private Map<String, ResultSeverity>       severityMap;

    private DroolsCompilerComponentFactory    componentFactory;

    private ClassBuilderFactory               classBuilderFactory;

    private LanguageLevelOption               languageLevel           = DrlParser.DEFAULT_LANGUAGE_LEVEL;

    private CompilationCache                  compilationCache        = null;

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBuilderConfigurationImpl.class);

    public boolean isAllowMultipleNamespaces() {
        return allowMultipleNamespaces;
    }

    /**
     * By default multiple namespaces are allowed. If you set this to "false" it will
     * make it all happen in the "default" namespace (the first namespace you define).
     */
    public void setAllowMultipleNamespaces(boolean allowMultipleNamespaces) {
        this.allowMultipleNamespaces = allowMultipleNamespaces;
    }

    /**
     * Constructor that sets the parent class loader for the package being built/compiled
     * @param classLoaders
     */
    public KnowledgeBuilderConfigurationImpl(ClassLoader... classLoaders) {
        init(null,
                classLoaders);
    }

    /**
     * Programmatic properties file, added with lease precedence
     */
    public KnowledgeBuilderConfigurationImpl(Properties properties) {
        init(properties,
                (ClassLoader[]) null);
    }

    /**
     * Programmatic properties file, added with lease precedence
     */
    public KnowledgeBuilderConfigurationImpl(Properties properties,
            ClassLoader... classLoaders) {
        init(properties,
                classLoaders);
    }

    public KnowledgeBuilderConfigurationImpl() {
        init(null,
                (ClassLoader[]) null);
    }

    private void init(Properties properties,
            ClassLoader... classLoaders) {
        if (classLoaders != null && classLoaders.length > 1) {
            throw new RuntimeException("Multiple classloaders are no longer supported");
        }
        setClassLoader(classLoaders == null || classLoaders.length == 0 ? null : classLoaders[0]);
        init(properties);
    }

    private void init(Properties properties) {

        this.chainedProperties = new ChainedProperties("packagebuilder.conf",
                getClassLoader(),
                true);

        if (chainedProperties.getProperty("drools.dialect.java", null) == null) {
            // if it couldn't find a conf for java dialect using the project class loader
            // it means it could not load the conf file at all (very likely it is running in
            // an osgi environement) so try with the class loader of this class
            this.chainedProperties = new ChainedProperties("packagebuilder.conf",
                    getClass().getClassLoader(),
                    true);

            if (this.classLoader instanceof ProjectClassLoader) {
                ((ProjectClassLoader) classLoader).setDroolsClassLoader(getClass().getClassLoader());
            }
        }

        if (properties != null) {
            this.chainedProperties.addProperties(properties);
        }

        setProperty(ClassLoaderCacheOption.PROPERTY_NAME,
                    this.chainedProperties.getProperty(ClassLoaderCacheOption.PROPERTY_NAME,
                                                       "true"));

        setProperty(PropertySpecificOption.PROPERTY_NAME,
                    this.chainedProperties.getProperty(PropertySpecificOption.PROPERTY_NAME,
                                                       DEFAULT_PROP_SPEC_OPT.toString()));

        setProperty(LanguageLevelOption.PROPERTY_NAME,
                    this.chainedProperties.getProperty(LanguageLevelOption.PROPERTY_NAME,
                                                       DrlParser.DEFAULT_LANGUAGE_LEVEL.toString()));

        this.dialectConfigurations = new HashMap<String, DialectConfiguration>();

        buildDialectConfigurationMap();

        buildAccumulateFunctionsMap();

        buildEvaluatorRegistry();

        buildDumpDirectory();

        buildSeverityMap();

        setProperty(ProcessStringEscapesOption.PROPERTY_NAME,
                    this.chainedProperties.getProperty(ProcessStringEscapesOption.PROPERTY_NAME,
                                                       "true"));

        setProperty(DefaultPackageNameOption.PROPERTY_NAME,
                    this.chainedProperties.getProperty(DefaultPackageNameOption.PROPERTY_NAME,
                                                       "defaultpkg"));

        this.componentFactory = new DroolsCompilerComponentFactory();

        this.classBuilderFactory = new ClassBuilderFactory();
    }

    private void buildSeverityMap() {
        this.severityMap = new HashMap<String, ResultSeverity>();
        Map<String, String> temp = new HashMap<String, String>();
        this.chainedProperties.mapStartsWith(temp,
                KBuilderSeverityOption.PROPERTY_NAME,
                true);

        int index = KBuilderSeverityOption.PROPERTY_NAME.length();
        for (Map.Entry<String, String> entry : temp.entrySet()) {
            String identifier = entry.getKey().trim().substring(index);
            this.severityMap.put(identifier,
                    KBuilderSeverityOption.get(identifier, entry.getValue()).getSeverity());
        }
    }

    public void setProperty(String name,
            String value) {
        name = name.trim();
        if (StringUtils.isEmpty(name)) {
            return;
        }

        if (name.equals(DefaultDialectOption.PROPERTY_NAME)) {
            setDefaultDialect(value);
        } else if (name.startsWith(AccumulateFunctionOption.PROPERTY_NAME)) {
            addAccumulateFunction(name.substring(AccumulateFunctionOption.PROPERTY_NAME.length()),
                    value);
        } else if (name.startsWith(EvaluatorOption.PROPERTY_NAME)) {
            this.evaluatorRegistry.addEvaluatorDefinition(value);
        } else if (name.equals(DumpDirOption.PROPERTY_NAME)) {
            buildDumpDirectory(value);
        } else if (name.equals(DefaultPackageNameOption.PROPERTY_NAME)) {
            setDefaultPackageName(value);
        } else if (name.equals(ProcessStringEscapesOption.PROPERTY_NAME)) {
            setProcessStringEscapes(Boolean.parseBoolean(value));
        } else if (name.equals(ClassLoaderCacheOption.PROPERTY_NAME)) {
            setClassLoaderCacheEnabled(Boolean.parseBoolean(value));
        } else if (name.startsWith(KBuilderSeverityOption.PROPERTY_NAME)) {
            String key = name.substring(name.lastIndexOf('.') + 1);
            this.severityMap.put(key, KBuilderSeverityOption.get(key, value).getSeverity());
        } else if (name.equals(PropertySpecificOption.PROPERTY_NAME)) {
            try {
                setPropertySpecificOption(PropertySpecificOption.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid value " + value + " for option " + PropertySpecificOption.PROPERTY_NAME);
            }
        } else if (name.equals(LanguageLevelOption.PROPERTY_NAME)) {
            try {
                setLanguageLevel(LanguageLevelOption.valueOf(value.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid value " + value + " for option " + LanguageLevelOption.PROPERTY_NAME);
            }
        }
    }

    public String getProperty(String name) {
        name = name.trim();
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        if (name.equals(DefaultDialectOption.PROPERTY_NAME)) {
            return getDefaultDialect();
        } else if (name.equals(DefaultPackageNameOption.PROPERTY_NAME)) {
            return getDefaultPackageName();
        } else if (name.startsWith(AccumulateFunctionOption.PROPERTY_NAME)) {
            int index = AccumulateFunctionOption.PROPERTY_NAME.length();
            AccumulateFunction function = this.accumulateFunctions.get(name.substring(index));
            return function != null ? function.getClass().getName() : null;
        } else if (name.startsWith(EvaluatorOption.PROPERTY_NAME)) {
            String key = name.substring(name.lastIndexOf('.') + 1);
            EvaluatorDefinition evalDef = this.evaluatorRegistry.getEvaluatorDefinition(key);
            return evalDef != null ? evalDef.getClass().getName() : null;
        } else if (name.equals(DumpDirOption.PROPERTY_NAME)) {
            return this.dumpDirectory != null ? this.dumpDirectory.toString() : null;
        } else if (name.equals(ProcessStringEscapesOption.PROPERTY_NAME)) {
            return String.valueOf(isProcessStringEscapes());
        } else if (name.equals(ClassLoaderCacheOption.PROPERTY_NAME)) {
            return String.valueOf(isClassLoaderCacheEnabled());
        } else if (name.startsWith(KBuilderSeverityOption.PROPERTY_NAME)) {
            String key = name.substring(name.lastIndexOf('.') + 1);
            ResultSeverity severity = this.severityMap.get(key);
            return severity.toString();
        } else if (name.equals(LanguageLevelOption.PROPERTY_NAME)) {
            return "" + getLanguageLevel();
        }
        return null;
    }

    public ChainedProperties getChainedProperties() {
        return this.chainedProperties;
    }

    private void buildDialectConfigurationMap() {
        //DialectRegistry registry = new DialectRegistry();
        Map<String, String> dialectProperties = new HashMap<String, String>();
        this.chainedProperties.mapStartsWith(dialectProperties,
                "drools.dialect",
                false);
        setDefaultDialect(dialectProperties.remove(DefaultDialectOption.PROPERTY_NAME));

        for (Map.Entry<String, String> entry : dialectProperties.entrySet()) {
            String str = entry.getKey();
            String dialectName = str.substring(str.lastIndexOf(".") + 1);
            String dialectClass = entry.getValue();
            addDialect(dialectName, dialectClass);
        }
    }

    public void addDialect(String dialectName,
            String dialectClass) {
        Class<?> cls = null;
        try {
            cls = getClassLoader().loadClass(dialectClass);
            DialectConfiguration dialectConf = (DialectConfiguration) cls.newInstance();
            dialectConf.init(this);
            addDialect(dialectName,
                    dialectConf);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dialect '" + dialectClass + ":" + dialectName + ":" + ((cls != null) ? cls.getName() : "null") + "'",
                    e);
        }
    }

    public void addDialect(String dialectName,
            DialectConfiguration dialectConf) {
        dialectConfigurations.put(dialectName,
                dialectConf);
    }

    public DialectCompiletimeRegistry buildDialectRegistry(ClassLoader rootClassLoader,
            KnowledgeBuilderConfigurationImpl pkgConf,
            PackageRegistry pkgRegistry,
            InternalKnowledgePackage pkg) {
        DialectCompiletimeRegistry registry = new DialectCompiletimeRegistry();
        for (DialectConfiguration conf : this.dialectConfigurations.values()) {
            Dialect dialect = conf.newDialect(rootClassLoader, pkgConf, pkgRegistry, pkg);
            registry.addDialect(dialect.getId(), dialect);
        }
        return registry;
    }

    public String getDefaultDialect() {
        return this.defaultDialect.getName();
    }

    public void setDefaultDialect(String defaultDialect) {
        this.defaultDialect = DefaultDialectOption.get(defaultDialect);
    }

    public DialectConfiguration getDialectConfiguration(String name) {
        return this.dialectConfigurations.get(name);
    }

    public void setDialectConfiguration(String name,
            DialectConfiguration configuration) {
        this.dialectConfigurations.put(name,
                configuration);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /** Use this to override the classLoader that will be used for the rules. */
    private void setClassLoader(ClassLoader classLoader) {
        this.classLoader = ProjectClassLoader.getClassLoader(classLoader,
                getClass(),
                isClassLoaderCacheEnabled());
    }

    public void addSemanticModule(SemanticModule module) {
        if (this.semanticModules == null) {
            initSemanticModules();
        }
        this.semanticModules.addSemanticModule(module);
    }

    public SemanticModules getSemanticModules() {
        if (this.semanticModules == null) {
            initSemanticModules();
        }
        return this.semanticModules;
    }

    public void initSemanticModules() {
        this.semanticModules = new SemanticModules();

        RulesSemanticModule ruleModule = new RulesSemanticModule("http://ddefault");

        this.semanticModules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.0", ruleModule));
        this.semanticModules.addSemanticModule(new WrapperSemanticModule("http://drools.org/drools-5.2", ruleModule));
        this.semanticModules.addSemanticModule(new ChangeSetSemanticModule());

        // split on each space
        String locations[] = this.chainedProperties.getProperty("semanticModules",
                "").split("\\s");

        // load each SemanticModule
        for (String moduleLocation : locations) {
            // trim leading/trailing spaces and quotes
            moduleLocation = moduleLocation.trim();
            if (moduleLocation.startsWith("\"")) {
                moduleLocation = moduleLocation.substring(1);
            }
            if (moduleLocation.endsWith("\"")) {
                moduleLocation = moduleLocation.substring(0,
                        moduleLocation.length() - 1);
            }
            if (!moduleLocation.equals("")) {
                loadSemanticModule(moduleLocation);
            }
        }
    }

    public void loadSemanticModule(String moduleLocation) {
        URL url = ConfFileUtils.getURL(moduleLocation,
                getClassLoader(),
                getClass());
        if (url == null) {
            throw new IllegalArgumentException(moduleLocation + " is specified but cannot be found.'");
        }

        Properties properties = ConfFileUtils.getProperties(url);
        if (properties == null) {
            throw new IllegalArgumentException(moduleLocation + " is specified but cannot be found.'");
        }

        loadSemanticModule(properties);
    }

    public void loadSemanticModule(Properties properties) {
        String uri = properties.getProperty("uri",
                null);
        if (uri == null || uri.trim().equals("")) {
            throw new RuntimeException("Semantic Module URI property must not be empty");
        }

        DefaultSemanticModule module = new DefaultSemanticModule(uri);

        for (Entry<Object, Object> entry : properties.entrySet()) {
            String elementName = (String) entry.getKey();

            //uri is processed above, so skip
            if ("uri".equals(elementName)) {
                continue;
            }

            if (elementName == null || elementName.trim().equals("")) {
                throw new RuntimeException("Element name must be specified for Semantic Module handler");
            }
            String handlerName = (String) entry.getValue();
            if (handlerName == null || handlerName.trim().equals("")) {
                throw new RuntimeException("Handler name must be specified for Semantic Module");
            }

            Handler handler = (Handler) ClassUtils.instantiateObject(handlerName,
                    getClassLoader());

            if (handler == null) {
                throw new RuntimeException("Unable to load Semantic Module handler '" + elementName + ":" + handlerName + "'");
            } else {
                module.addHandler(elementName,
                        handler);
            }
        }
        this.semanticModules.addSemanticModule(module);
    }

    private void buildAccumulateFunctionsMap() {
        this.accumulateFunctions = new HashMap<String, AccumulateFunction>();
        Map<String, String> temp = new HashMap<String, String>();
        this.chainedProperties.mapStartsWith(temp,
                AccumulateFunctionOption.PROPERTY_NAME,
                true);
        int index = AccumulateFunctionOption.PROPERTY_NAME.length();
        for (Map.Entry<String, String> entry : temp.entrySet()) {
            String identifier = entry.getKey().trim().substring(index);
            this.accumulateFunctions.put(identifier,
                    loadAccumulateFunction(identifier,
                            entry.getValue()));
        }
    }

    public void addAccumulateFunction(String identifier,
            String className) {
        this.accumulateFunctions.put(identifier,
                loadAccumulateFunction(identifier,
                        className));
    }

    public void addAccumulateFunction(String identifier,
            Class<? extends AccumulateFunction> clazz) {
        try {
            this.accumulateFunctions.put(identifier,
                    clazz.newInstance());
        } catch (InstantiationException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + clazz.getName(),
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + clazz.getName(),
                    e);
        }
    }

    public AccumulateFunction getAccumulateFunction(String identifier) {
        return this.accumulateFunctions.get(identifier);
    }

    // Used by droolsjbpm-tools
    public Collection<String> getAccumulateFunctionNames() {
        return this.accumulateFunctions.keySet();
    }

    @SuppressWarnings("unchecked")
    private AccumulateFunction loadAccumulateFunction(String identifier,
            String className) {
        try {
            Class<? extends AccumulateFunction> clazz = (Class<? extends AccumulateFunction>) getClassLoader().loadClass(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Class " + className + " not found",
                    e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Instantiation failed for class " + className,
                    e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error loading accumulate function for identifier " + identifier + ". Illegal access to class " + className,
                    e);
        }
    }

    private void buildEvaluatorRegistry() {
        this.evaluatorRegistry = new EvaluatorRegistry(getClassLoader());
        Map<String, String> temp = new HashMap<String, String>();
        this.chainedProperties.mapStartsWith(temp,
                EvaluatorOption.PROPERTY_NAME,
                true);
        for (Entry<String, String> e : temp.entrySet()) {
            String key = e.getKey();
            // filtering out unused properties, to avoid failing when an old packagebuilder.conf
            // file is present on the classpath that did define these (for example when parsing
            // a rule in Eclipse plugin using old runtime)
            if ("drools.evaluator.equality".equals(key)
                    || ("drools.evaluator.comparable".equals(key))) {
                continue;
            }
            this.evaluatorRegistry.addEvaluatorDefinition(e.getValue());
        }
    }

    /**
     * Returns the evaluator registry for this package builder configuration
     * @return
     */
    public EvaluatorRegistry getEvaluatorRegistry() {
        return this.evaluatorRegistry;
    }

    /**
     * Adds an evaluator definition class to the registry using the
     * evaluator class name. The class will be loaded and the corresponting
     * evaluator ID will be added to the registry. In case there exists
     * an implementation for that ID already, the new implementation will
     * replace the previous one.
     *
     * @param className the name of the class for the implementation definition.
     *                  The class must implement the EvaluatorDefinition interface.
     */
    public void addEvaluatorDefinition(String className) {
        this.evaluatorRegistry.addEvaluatorDefinition(className);
    }

    /**
     * Adds an evaluator definition class to the registry. In case there exists
     * an implementation for that evaluator ID already, the new implementation will
     * replace the previous one.
     *
     * @param def the evaluator definition to be added.
     */
    public void addEvaluatorDefinition(EvaluatorDefinition def) {
        this.evaluatorRegistry.addEvaluatorDefinition(def);
    }

    private void buildDumpDirectory() {
        String dumpStr = this.chainedProperties.getProperty(DumpDirOption.PROPERTY_NAME,
                null);
        buildDumpDirectory(dumpStr);
    }

    private void buildDumpDirectory(String dumpStr) {
        if (dumpStr != null) {
            setDumpDir(new File(dumpStr));
        }
    }

    public File getDumpDir() {
        return this.dumpDirectory;
    }

    public void setDumpDir(File dumpDir) {
        if (!dumpDir.isDirectory() || !dumpDir.canWrite() || !dumpDir.canRead()) {
            throw new RuntimeException("Drools dump directory is not accessible: " + dumpDir.toString());
        }
        this.dumpDirectory = dumpDir;
    }

    public boolean isProcessStringEscapes() {
        return processStringEscapes;
    }

    public void setProcessStringEscapes(boolean processStringEscapes) {
        this.processStringEscapes = processStringEscapes;
    }

    @Deprecated
    public boolean isClassLoaderCacheEnabled() {
        return classLoaderCache;
    }

    @Deprecated
    public void setClassLoaderCacheEnabled(boolean classLoaderCacheEnabled) {
        this.classLoaderCache = classLoaderCacheEnabled;
    }

    public String getDefaultPackageName() {
        return defaultPackageName;
    }

    public void setDefaultPackageName(String defaultPackageName) {
        this.defaultPackageName = defaultPackageName;
    }

    public DroolsCompilerComponentFactory getComponentFactory() {
        return componentFactory;
    }

    public void setComponentFactory(DroolsCompilerComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public ClassBuilderFactory getClassBuilderFactory() {
        return classBuilderFactory;
    }

    public void setClassBuilderFactory(ClassBuilderFactory classBuilderFactory) {
        this.classBuilderFactory = classBuilderFactory;
    }

    public LanguageLevelOption getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(LanguageLevelOption languageLevel) {
        this.languageLevel = languageLevel;
    }

    public PropertySpecificOption getPropertySpecificOption() {
        return propertySpecificOption;
    }

    public void setPropertySpecificOption(PropertySpecificOption propertySpecificOption) {
        this.propertySpecificOption = propertySpecificOption;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKnowledgeBuilderOption> T getOption(Class<T> option) {
        if (DefaultDialectOption.class.equals(option)) {
            return (T) this.defaultDialect;
        } else if (DumpDirOption.class.equals(option)) {
            return (T) DumpDirOption.get(this.dumpDirectory);
        } else if (ProcessStringEscapesOption.class.equals(option)) {
            return (T) (this.processStringEscapes ? ProcessStringEscapesOption.YES : ProcessStringEscapesOption.NO);
        } else if (DefaultPackageNameOption.class.equals(option)) {
            return (T) DefaultPackageNameOption.get(this.defaultPackageName);
        } else if (ClassLoaderCacheOption.class.equals(option)) {
            return (T) (this.classLoaderCache ? ClassLoaderCacheOption.ENABLED : ClassLoaderCacheOption.DISABLED);
        } else if (PropertySpecificOption.class.equals(option)) {
            return (T) propertySpecificOption;
        } else if (LanguageLevelOption.class.equals(option)) {
            return (T) languageLevel;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends MultiValueKnowledgeBuilderOption> T getOption(Class<T> option,
            String key) {
        if (AccumulateFunctionOption.class.equals(option)) {
            return (T) AccumulateFunctionOption.get(key,
                    this.accumulateFunctions.get(key));
        } else if (EvaluatorOption.class.equals(option)) {
            return (T) EvaluatorOption.get(key,
                    this.evaluatorRegistry.getEvaluatorDefinition(key));
        } else if (KBuilderSeverityOption.class.equals(option)) {

            return (T) KBuilderSeverityOption.get(key,
                    this.severityMap.get(key));
        }
        return null;
    }

    public <T extends MultiValueKnowledgeBuilderOption> Set<String> getOptionKeys(
            Class<T> option) {
        if (AccumulateFunctionOption.class.equals(option)) {
            return this.accumulateFunctions.keySet();
        } else if (EvaluatorOption.class.equals(option)) {
            return this.evaluatorRegistry.keySet();
        } else if (KBuilderSeverityOption.class.equals(option)) {
            return this.severityMap.keySet();
        }
        return null;
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        if (option instanceof DefaultDialectOption) {
            this.defaultDialect = (DefaultDialectOption) option;
        } else if (option instanceof AccumulateFunctionOption) {
            this.accumulateFunctions.put(((AccumulateFunctionOption) option).getName(),
                    ((AccumulateFunctionOption) option).getFunction());
        } else if (option instanceof DumpDirOption) {
            this.dumpDirectory = ((DumpDirOption) option).getDirectory();
        } else if (option instanceof EvaluatorOption) {
            this.evaluatorRegistry.addEvaluatorDefinition((EvaluatorDefinition) ((EvaluatorOption) option).getEvaluatorDefinition());
        } else if (option instanceof ProcessStringEscapesOption) {
            this.processStringEscapes = ((ProcessStringEscapesOption) option).isProcessStringEscapes();
        } else if (option instanceof DefaultPackageNameOption) {
            setDefaultPackageName(((DefaultPackageNameOption) option).getPackageName());
        } else if (option instanceof ClassLoaderCacheOption) {
            setClassLoaderCacheEnabled(((ClassLoaderCacheOption) option).isClassLoaderCacheEnabled());
        } else if (option instanceof KBuilderSeverityOption) {
            this.severityMap.put(((KBuilderSeverityOption) option).getName(), ((KBuilderSeverityOption) option).getSeverity());
        } else if (option instanceof PropertySpecificOption) {
            propertySpecificOption = (PropertySpecificOption) option;
        } else if (option instanceof LanguageLevelOption) {
            this.languageLevel = ((LanguageLevelOption) option);
        }
    }

    public CompilationCache getCompilationCache() {
        return compilationCache;
    }

    public void setCompilationCache(CompilationCache cache) {
        this.compilationCache = cache;
    }

    public boolean isPreCompiled() {
        return this.compilationCache != null;
    }

}
