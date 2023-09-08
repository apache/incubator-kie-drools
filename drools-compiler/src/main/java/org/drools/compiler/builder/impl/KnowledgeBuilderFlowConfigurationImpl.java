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

import java.util.Set;

import org.drools.core.BaseConfiguration;
import org.kie.api.conf.ConfigurationKey;
import org.kie.api.conf.OptionKey;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
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
public class KnowledgeBuilderFlowConfigurationImpl extends BaseConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
        implements
        KnowledgeBuilderConfiguration {

    public static final ConfigurationKey<KnowledgeBuilderFlowConfigurationImpl> KEY = new ConfigurationKey<>("Flow");

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBuilderFlowConfigurationImpl.class);


    /**
     * Programmatic properties file, added with lease precedence
     */
    public KnowledgeBuilderFlowConfigurationImpl(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig) {
        super(compConfig);
    }

    public boolean setInternalProperty(String name, String value) {
        return false;
    }

    public String getInternalProperty(String name) {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T extends SingleValueKieBuilderOption> T getOption(OptionKey<T> option) {
        return compConfig.getOption(option);
    }

    @SuppressWarnings("unchecked")
    public <T extends MultiValueKieBuilderOption> T getOption(OptionKey<T> option,
                                                              String subKey) {
        return compConfig.getOption(option, subKey);
    }

    public <T extends MultiValueKieBuilderOption> Set<String> getOptionSubKeys(OptionKey<T> option) {
        return compConfig.getOptionSubKeys(option);
    }

    public <T extends KnowledgeBuilderOption> void setOption(T option) {
        compConfig.setOption(option);
    }

}
