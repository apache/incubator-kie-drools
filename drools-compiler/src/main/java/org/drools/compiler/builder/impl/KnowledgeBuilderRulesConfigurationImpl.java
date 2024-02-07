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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule.CompilationCache;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.compiler.rule.builder.util.AccumulateUtil;
import org.drools.core.BaseConfiguration;
import org.drools.drl.parser.DrlParser;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.AccumulateFunctionOption;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.builder.conf.ExternaliseCanonicalModelLambdaOption;
import org.kie.internal.builder.conf.GroupDRLsInKieBasesByFolderOption;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.ParallelLambdaExternalizationOption;
import org.kie.internal.builder.conf.ParallelRulesBuildThresholdOption;
import org.kie.internal.builder.conf.ProcessStringEscapesOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.builder.conf.ReproducibleExecutableModelGenerationOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.builder.conf.TrimCellsInDTableOption;
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
public class KnowledgeBuilderRulesConfigurationImpl extends BaseConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
        implements
        KnowledgeBuilderConfiguration {
    private static final int                  DEFAULT_PARALLEL_RULES_BUILD_THRESHOLD = 10;

    public static final ConfigurationKey<KnowledgeBuilderRulesConfigurationImpl> KEY = new ConfigurationKey<>("Rule");

    private ParallelRulesBuildThresholdOption parallelRulesBuildThreshold = ParallelRulesBuildThresholdOption.get(DEFAULT_PARALLEL_RULES_BUILD_THRESHOLD);

    private Map<String, AccumulateFunction>   accumulateFunctions;

    private EvaluatorRegistry                 evaluatorRegistry;

    private boolean                           processStringEscapes                  = true;
    private boolean                           trimCellsInDTable                     = true;
    private boolean                           groupDRLsInKieBasesByFolder           = false;

    private boolean                           reproducibleExecutableModelGeneration = false;

    private boolean                           externaliseCanonicalModelLambda       = true;
    private boolean                           parallelLambdaExternalization         = true;

    private AlphaNetworkCompilerOption        alphaNetworkCompilerOption            = AlphaNetworkCompilerOption.DISABLED;

    private static final PropertySpecificOption DEFAULT_PROP_SPEC_OPT = PropertySpecificOption.ALWAYS;
    private PropertySpecificOption            propertySpecificOption  = DEFAULT_PROP_SPEC_OPT;

    private PrototypesOption                  prototypesOption  = PrototypesOption.DISABLED;

    private LanguageLevelOption               languageLevel           = DrlParser.DEFAULT_LANGUAGE_LEVEL;

    private CompilationCache                  compilationCache        = null;
    private static final Logger log = LoggerFactory.getLogger(KnowledgeBuilderRulesConfigurationImpl.class);


    /**
     * Programmatic properties file, added with lease precedence
     */
    public KnowledgeBuilderRulesConfigurationImpl(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig) {
        super(compConfig);
        init();
    }

    private void init() {

        setProperty( TrimCellsInDTableOption.PROPERTY_NAME,
                    getPropertyValue(TrimCellsInDTableOption.PROPERTY_NAME,
                                                       "true"));

        setProperty( GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME,
                    getPropertyValue(GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME,
                                                       "false"));

        setProperty(PropertySpecificOption.PROPERTY_NAME,
                    getPropertyValue(PropertySpecificOption.PROPERTY_NAME,
                                                       DEFAULT_PROP_SPEC_OPT.toString()));

        setProperty(LanguageLevelOption.PROPERTY_NAME,
                    getPropertyValue(LanguageLevelOption.PROPERTY_NAME,
                                                       DrlParser.DEFAULT_LANGUAGE_LEVEL.toString()));

        setProperty(ParallelRulesBuildThresholdOption.PROPERTY_NAME,
                    getPropertyValue(ParallelRulesBuildThresholdOption.PROPERTY_NAME, 
                                                        String.valueOf(DEFAULT_PARALLEL_RULES_BUILD_THRESHOLD)));

        this.accumulateFunctions = AccumulateUtil.buildAccumulateFunctionsMap(getProperties(), getFunctionFactoryClassLoader() );

        buildEvaluatorRegistry();

        setProperty(ProcessStringEscapesOption.PROPERTY_NAME,
                    getPropertyValue(ProcessStringEscapesOption.PROPERTY_NAME,
                                                       "true"));

        setProperty(AlphaNetworkCompilerOption.PROPERTY_NAME,
                    getPropertyValue(AlphaNetworkCompilerOption.PROPERTY_NAME,
                                                       "disabled"));

        setProperty(ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME,
                    getPropertyValue(ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME,"true"));

        setProperty(ParallelLambdaExternalizationOption.PROPERTY_NAME,
                    getPropertyValue(ParallelLambdaExternalizationOption.PROPERTY_NAME,"true"));

        setProperty(ReproducibleExecutableModelGenerationOption.PROPERTY_NAME,
                    getPropertyValue(ReproducibleExecutableModelGenerationOption.PROPERTY_NAME,"false"));
    }

    protected ClassLoader getFunctionFactoryClassLoader() {
        return getClassLoader();
    }

    public boolean setInternalProperty(String name, String value) {
        switch (name) {
            case PrototypesOption.PROPERTY_NAME: {
                setPrototypesOption(PrototypesOption.determinePrototypesOption(value));
                break;
            }
            case ProcessStringEscapesOption.PROPERTY_NAME: {
                setProcessStringEscapes(Boolean.parseBoolean(value));
                break;
            } case TrimCellsInDTableOption.PROPERTY_NAME: {
                setTrimCellsInDTable(Boolean.parseBoolean(value));
                break;
            } case GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME: {
                setGroupDRLsInKieBasesByFolder(Boolean.parseBoolean(value));
                break;
            } case PropertySpecificOption.PROPERTY_NAME: {
                try {
                    setPropertySpecificOption(PropertySpecificOption.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid value " + value + " for option " + PropertySpecificOption.PROPERTY_NAME);
                }
                break;
            } case LanguageLevelOption.PROPERTY_NAME: {
                try {
                    setLanguageLevel(LanguageLevelOption.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid value " + value + " for option " + LanguageLevelOption.PROPERTY_NAME);
                }
                break;
            } case ParallelRulesBuildThresholdOption.PROPERTY_NAME: {
                setParallelRulesBuildThreshold(Integer.parseInt(value));
                break;
            } case ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME: {
                setExternaliseCanonicalModelLambda(Boolean.parseBoolean(value));
                break;
            } case ParallelLambdaExternalizationOption.PROPERTY_NAME: {
                setParallelLambdaExternalization(Boolean.parseBoolean(value));
                break;
            } case ReproducibleExecutableModelGenerationOption.PROPERTY_NAME: {
                setReproducibleExecutableModelGeneration(Boolean.parseBoolean(value));
                break;
            } case AlphaNetworkCompilerOption.PROPERTY_NAME: {
                try {
                    setAlphaNetworkCompilerOption(AlphaNetworkCompilerOption.determineAlphaNetworkCompilerMode(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid value " + value + " for option " + AlphaNetworkCompilerOption.PROPERTY_NAME);
                }
                break;
            } default: {
                if (name.startsWith(AccumulateFunctionOption.PROPERTY_NAME)) {
                    addAccumulateFunction(name.substring(AccumulateFunctionOption.PROPERTY_NAME.length()),
                                          value);
                } else if (name.startsWith(EvaluatorOption.PROPERTY_NAME)) {
                    this.evaluatorRegistry.addEvaluatorDefinition(value);
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public String getInternalProperty(String name) {
        switch (name) {
            case ProcessStringEscapesOption.PROPERTY_NAME: {
                return String.valueOf(isProcessStringEscapes());
            } case TrimCellsInDTableOption.PROPERTY_NAME: {
                return String.valueOf(isTrimCellsInDTable());
            } case GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME: {
                return String.valueOf(isGroupDRLsInKieBasesByFolder());
            } case LanguageLevelOption.PROPERTY_NAME: {
                return "" + getLanguageLevel();
            } case ParallelRulesBuildThresholdOption.PROPERTY_NAME: {
                return String.valueOf(getParallelRulesBuildThreshold());
            } case ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME: {
                return String.valueOf(isExternaliseCanonicalModelLambda());
            } case ParallelLambdaExternalizationOption.PROPERTY_NAME: {
                return String.valueOf(isParallelLambdaExternalization());
            } case ReproducibleExecutableModelGenerationOption.PROPERTY_NAME: {
                return String.valueOf(isReproducibleExecutableModelGeneration());
            } default: {
                if (name.startsWith(AccumulateFunctionOption.PROPERTY_NAME)) {
                    int                index    = AccumulateFunctionOption.PROPERTY_NAME.length();
                    AccumulateFunction function = this.accumulateFunctions.get(name.substring(index));
                    return function != null ? function.getClass().getName() : null;
                } else if (name.startsWith(EvaluatorOption.PROPERTY_NAME)) {
                    String              key     = name.substring(name.lastIndexOf('.') + 1);
                    EvaluatorDefinition evalDef = this.evaluatorRegistry.getEvaluatorDefinition(key);
                    return evalDef != null ? evalDef.getClass().getName() : null;
                }
            }
        }
        return null;
    }

    public void addAccumulateFunction(String identifier, String className) {
        this.accumulateFunctions.put(identifier,
                                     AccumulateUtil.loadAccumulateFunction(getClassLoader(), identifier,
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

    private void buildEvaluatorRegistry() {
        this.evaluatorRegistry = new EvaluatorRegistry( getFunctionFactoryClassLoader() );
        Map<String, String> temp = new HashMap<>();
        getProperties().mapStartsWith(temp,
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

    public boolean isProcessStringEscapes() {
        return processStringEscapes;
    }

    public void setProcessStringEscapes(boolean processStringEscapes) {
        this.processStringEscapes = processStringEscapes;
    }

    public boolean isTrimCellsInDTable() {
        return trimCellsInDTable;
    }

    public void setTrimCellsInDTable( boolean trimCellsInDTable ) {
        this.trimCellsInDTable = trimCellsInDTable;
    }

    public boolean isGroupDRLsInKieBasesByFolder() {
        return groupDRLsInKieBasesByFolder;
    }

    public void setGroupDRLsInKieBasesByFolder( boolean groupDRLsInKieBasesByFolder ) {
        this.groupDRLsInKieBasesByFolder = groupDRLsInKieBasesByFolder;
    }

    public int getParallelRulesBuildThreshold() {
        return parallelRulesBuildThreshold.getParallelRulesBuildThreshold();
    }
    
    public void setParallelRulesBuildThreshold(int parallelRulesBuildThreshold) {
        this.parallelRulesBuildThreshold = ParallelRulesBuildThresholdOption.get(parallelRulesBuildThreshold);
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

    public PrototypesOption getPrototypesOption() {
        return prototypesOption;
    }

    public void setPrototypesOption(PrototypesOption prototypesOption) {
        this.prototypesOption = prototypesOption;
    }

    public boolean isExternaliseCanonicalModelLambda() {
        return externaliseCanonicalModelLambda;
    }

    public void setExternaliseCanonicalModelLambda(boolean externaliseCanonicalModelLambda) {
        this.externaliseCanonicalModelLambda = externaliseCanonicalModelLambda;
    }

    public boolean isParallelLambdaExternalization() {
        return parallelLambdaExternalization;
    }

    public void setParallelLambdaExternalization(boolean parallelLambdaExternalization) {
        this.parallelLambdaExternalization = parallelLambdaExternalization;
    }

    public boolean isReproducibleExecutableModelGeneration() {
        return reproducibleExecutableModelGeneration;
    }

    public void setReproducibleExecutableModelGeneration(boolean reproducibleExecutableModelGeneration) {
        this.reproducibleExecutableModelGeneration = reproducibleExecutableModelGeneration;
    }

    public AlphaNetworkCompilerOption getAlphaNetworkCompilerOption() {
        return alphaNetworkCompilerOption;
    }

    public void setAlphaNetworkCompilerOption(AlphaNetworkCompilerOption alphaNetworkCompilerOption) {
        this.alphaNetworkCompilerOption = alphaNetworkCompilerOption;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBuilderOption> T getOption(OptionKey<T> option) {
        switch ((option.name())) {
            case ProcessStringEscapesOption.PROPERTY_NAME: {
                return (T) (this.processStringEscapes ? ProcessStringEscapesOption.YES : ProcessStringEscapesOption.NO);
            }
            case TrimCellsInDTableOption.PROPERTY_NAME: {
                return (T) (this.trimCellsInDTable ? TrimCellsInDTableOption.ENABLED : TrimCellsInDTableOption.DISABLED);
            }
            case GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME: {
                return (T) (this.groupDRLsInKieBasesByFolder ? GroupDRLsInKieBasesByFolderOption.ENABLED : GroupDRLsInKieBasesByFolderOption.DISABLED);
            }
            case PropertySpecificOption.PROPERTY_NAME: {
                return (T) propertySpecificOption;
            }
            case LanguageLevelOption.PROPERTY_NAME: {
                return (T) languageLevel;
            }
            case ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME: {
                return (T) (externaliseCanonicalModelLambda ? ExternaliseCanonicalModelLambdaOption.ENABLED : ExternaliseCanonicalModelLambdaOption.DISABLED);
            }
            case ParallelLambdaExternalizationOption.PROPERTY_NAME: {
                return (T) (parallelLambdaExternalization ? ParallelLambdaExternalizationOption.ENABLED : ParallelLambdaExternalizationOption.DISABLED);
            }
            case ReproducibleExecutableModelGenerationOption.PROPERTY_NAME: {
                return (T) (reproducibleExecutableModelGeneration ? ReproducibleExecutableModelGenerationOption.ENABLED : ReproducibleExecutableModelGenerationOption.DISABLED);
            }
            case ParallelRulesBuildThresholdOption.PROPERTY_NAME: {
                return (T) parallelRulesBuildThreshold;
            }
            case AlphaNetworkCompilerOption.PROPERTY_NAME: {
                return (T) alphaNetworkCompilerOption;
            }
            default:
               return compConfig.getOption(option);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends MultiValueKieBuilderOption> T getOption(OptionKey<T> option,
                                                              String subKey) {
        switch (option.name()) {
            case AccumulateFunctionOption.PROPERTY_NAME: {
                return (T) AccumulateFunctionOption.get(subKey,
                                                        this.accumulateFunctions.get(subKey));
            }
            case EvaluatorOption.PROPERTY_NAME: {
                return (T) EvaluatorOption.get(subKey,
                                               this.evaluatorRegistry.getEvaluatorDefinition(subKey));
            }
            default:
                return compConfig.getOption(option, subKey);
        }
    }

    public <T extends MultiValueKieBuilderOption> Set<String> getOptionSubKeys(OptionKey<T> option) {
        switch(option.name()) {
            case AccumulateFunctionOption.PROPERTY_NAME: {
                return this.accumulateFunctions.keySet();
            }
            case EvaluatorOption.PROPERTY_NAME: {
                return this.evaluatorRegistry.keySet();
            }
            default:
                return compConfig.getOptionSubKeys(option);
        }
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        switch (option.propertyName()) {
            case AccumulateFunctionOption.PROPERTY_NAME: {
                this.accumulateFunctions.put(((AccumulateFunctionOption) option).getName(),
                                             ((AccumulateFunctionOption) option).getFunction());
                break;
            }
            case EvaluatorOption.PROPERTY_NAME: {
                this.evaluatorRegistry.addEvaluatorDefinition((EvaluatorDefinition) ((EvaluatorOption) option).getEvaluatorDefinition());
                break;
            }
            case ProcessStringEscapesOption.PROPERTY_NAME: {
                this.processStringEscapes = ((ProcessStringEscapesOption) option).isProcessStringEscapes();
                break;
            }
            case TrimCellsInDTableOption.PROPERTY_NAME: {
                setTrimCellsInDTable(((TrimCellsInDTableOption) option).isTrimCellsInDTable());
                break;
            }
            case GroupDRLsInKieBasesByFolderOption.PROPERTY_NAME: {
                setGroupDRLsInKieBasesByFolder(((GroupDRLsInKieBasesByFolderOption) option).isGroupDRLsInKieBasesByFolder());
                break;
            }
            case PropertySpecificOption.PROPERTY_NAME: {
                propertySpecificOption = (PropertySpecificOption) option;
                break;
            }
            case LanguageLevelOption.PROPERTY_NAME: {
                this.languageLevel = ((LanguageLevelOption) option);
                break;
            }
            case ExternaliseCanonicalModelLambdaOption.PROPERTY_NAME: {
                this.externaliseCanonicalModelLambda = ((ExternaliseCanonicalModelLambdaOption) option).isCanonicalModelLambdaExternalized();
                break;
            }
            case ParallelLambdaExternalizationOption.PROPERTY_NAME: {
                this.parallelLambdaExternalization = ((ParallelLambdaExternalizationOption) option).isLambdaExternalizationParallel();
                break;
            }
            case ReproducibleExecutableModelGenerationOption.PROPERTY_NAME: {
                this.reproducibleExecutableModelGeneration = ((ReproducibleExecutableModelGenerationOption) option).isReproducibleExecutableModelGeneration();
                break;
            }
            case ParallelRulesBuildThresholdOption.PROPERTY_NAME: {
                this.parallelRulesBuildThreshold = (ParallelRulesBuildThresholdOption)option;
                break;
            }
            case AlphaNetworkCompilerOption.PROPERTY_NAME: {
                this.alphaNetworkCompilerOption = (AlphaNetworkCompilerOption) option;
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
