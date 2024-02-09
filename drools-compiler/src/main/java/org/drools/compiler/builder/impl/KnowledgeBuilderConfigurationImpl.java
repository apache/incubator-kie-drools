/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DialectConfiguration;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kie.builder.impl.InternalKieModule.CompilationCache;
import org.drools.compiler.rule.builder.ConstraintBuilder;
import org.drools.core.BaseConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.DefaultDialectOption;
import org.kie.internal.builder.conf.DefaultPackageNameOption;
import org.kie.internal.builder.conf.DumpDirOption;
import org.kie.internal.builder.conf.KBuilderSeverityOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * drools.parallelRulesBuildThreshold = <int>
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
public class KnowledgeBuilderConfigurationImpl extends BaseConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
        implements
        KnowledgeBuilderConfiguration {

    public static final String                DEFAULT_PACKAGE = "defaultpkg";

    public static final ConfigurationKey<KnowledgeBuilderConfigurationImpl> KEY = new ConfigurationKey<>("Base");

    private final Map<String, DialectConfiguration> dialectConfigurations = new HashMap<>();

    private DefaultDialectOption              defaultDialect = DefaultDialectOption.get("java");
    private File                              dumpDirectory;

    private String                            defaultPackageName;

    private Map<String, ResultSeverity>       severityMap;

    private CompilationCache                  compilationCache        = null;
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBuilderConfigurationImpl.class);

    /**
     * Programmatic properties file, added with lease precedence
     */
    public KnowledgeBuilderConfigurationImpl(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig) {
        super(compConfig);
        init();
    }

    private void init() {
        buildDialectConfigurationMap();

        buildDumpDirectory();

        buildSeverityMap();

        setProperty(DefaultPackageNameOption.PROPERTY_NAME,
                    getProperties().getProperty(DefaultPackageNameOption.PROPERTY_NAME,
                                                DEFAULT_PACKAGE));

    }

    protected ClassLoader getFunctionFactoryClassLoader() {
        return getClassLoader();
    }

    private void buildSeverityMap() {
        this.severityMap = new HashMap<>();
        Map<String, String> temp = new HashMap<>();
        getProperties().mapStartsWith(temp, KBuilderSeverityOption.PROPERTY_NAME, true);

        int index = KBuilderSeverityOption.PROPERTY_NAME.length();
        for (Map.Entry<String, String> entry : temp.entrySet()) {
            String identifier = entry.getKey().trim().substring(index);
            this.severityMap.put(identifier,
                                 KBuilderSeverityOption.get(identifier, entry.getValue()).getSeverity());
        }
    }

    public boolean setInternalProperty(String name, String value) {
        switch (name) {
            case DefaultDialectOption.PROPERTY_NAME: {
                setDefaultDialect(value);
                break;
            } case DumpDirOption.PROPERTY_NAME: {
                buildDumpDirectory(value);
                break;
            } case DefaultPackageNameOption.PROPERTY_NAME: {
                setDefaultPackageName(value);
                break;
            } default: {
                if (name.startsWith(KBuilderSeverityOption.PROPERTY_NAME)) {
                    String key = name.substring(name.lastIndexOf('.') + 1);
                    this.severityMap.put(key, KBuilderSeverityOption.get(key, value).getSeverity());
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public String getInternalProperty(String name) {
        switch (name) {
            case DefaultDialectOption.PROPERTY_NAME: {
                return getDefaultDialect();
            } case DefaultPackageNameOption.PROPERTY_NAME: {
                return getDefaultPackageName();
            } case DumpDirOption.PROPERTY_NAME: {
                return this.dumpDirectory != null ? this.dumpDirectory.toString() : null;
            } default: {
                if (name.startsWith(KBuilderSeverityOption.PROPERTY_NAME)) {
                    String key = name.substring(name.lastIndexOf('.') + 1);
                    ResultSeverity severity = this.severityMap.get(key);
                    return severity.toString();
                }
            }
        }
        return null;
    }

    private void buildDialectConfigurationMap() {
        DialectConfiguration mvel = ConstraintBuilder.get().createMVELDialectConfiguration(this);
        if (mvel != null) {
            mvel.init( this );
            dialectConfigurations.put( "mvel", mvel );
        }

        DialectConfiguration java = ConstraintBuilder.get().createJavaDialectConfiguration(this);
        java.init(this);
        dialectConfigurations.put("java", java);

        Map<String, String> dialectProperties = new HashMap<>();
        getProperties().mapStartsWith(dialectProperties, "drools.dialect", false);
        setDefaultDialect(dialectProperties.get(DefaultDialectOption.PROPERTY_NAME));
    }

    public void addDialect(String dialectName, DialectConfiguration dialectConf) {
        dialectConfigurations.put(dialectName, dialectConf);
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
        return this.defaultDialect.dialectName();
    }

    public void setDefaultDialect(String defaultDialect) {
        this.defaultDialect = DefaultDialectOption.get(defaultDialect);
    }

    public DialectConfiguration getDialectConfiguration(String name) {
        return this.dialectConfigurations.get(name);
    }

    public void setDialectConfiguration(String name, DialectConfiguration configuration) {
        this.dialectConfigurations.put(name, configuration);
    }

    private void buildDumpDirectory() {
        String dumpStr = getProperties().getProperty(DumpDirOption.PROPERTY_NAME,
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

    public String getDefaultPackageName() {
        return defaultPackageName;
    }

    public void setDefaultPackageName(String defaultPackageName) {
        this.defaultPackageName = defaultPackageName;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBuilderOption> T getOption(OptionKey<T> option) {
        switch ((option.name())) {
            case DefaultDialectOption.PROPERTY_NAME: {
                return (T) this.defaultDialect;
            }
            case DumpDirOption.PROPERTY_NAME: {
                return (T) DumpDirOption.get(this.dumpDirectory);
            }
            case DefaultPackageNameOption.PROPERTY_NAME: {
                return (T) DefaultPackageNameOption.get(this.defaultPackageName);
            }
            default:
                return compConfig.getOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends MultiValueKieBuilderOption> T getOption(OptionKey<T> option,
                                                              String subKey) {
        switch (option.name()) {
            case KBuilderSeverityOption.PROPERTY_NAME: {
                return (T) KBuilderSeverityOption.get(subKey,
                                                      this.severityMap.get(subKey));
            }
            default:
                return compConfig.getOption(option, subKey);
        }
    }

    public <T extends MultiValueKieBuilderOption> Set<String> getOptionSubKeys(OptionKey<T> option) {
        switch(option.name()) {
            case KBuilderSeverityOption.PROPERTY_NAME: {
                return this.severityMap.keySet();
            }
            default:
                return compConfig.getOptionSubKeys(option);
        }
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        switch (option.propertyName()) {
            case DefaultDialectOption.PROPERTY_NAME: {
                this.defaultDialect = (DefaultDialectOption) option;
                break;
            }
            case DumpDirOption.PROPERTY_NAME: {
                this.dumpDirectory = ((DumpDirOption) option).getDirectory();
                break;
            }
            case DefaultPackageNameOption.PROPERTY_NAME: {
                setDefaultPackageName(((DefaultPackageNameOption) option).getPackageName());
                break;
            }
            case KBuilderSeverityOption.PROPERTY_NAME: {
                this.severityMap.put(((KBuilderSeverityOption) option).getName(), ((KBuilderSeverityOption) option).getSeverity());
                break;
            }
            default:
                compConfig.setOption(option);
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
