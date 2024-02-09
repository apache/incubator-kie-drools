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

import org.kie.api.conf.OptionsConfiguration;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

public class KnowledgeBuilderConfigurationFactories {
    public static ConfigurationFactory<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> baseConf = new ConfigurationFactory<>() {

        @Override
        public String type() {
            return "Base";
        }

        @Override
        public OptionsConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
                                             create(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig,
                                                    ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new KnowledgeBuilderConfigurationImpl(compConfig);
        }
    };

    public static ConfigurationFactory<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> ruleConf = new ConfigurationFactory<>() {

        @Override
        public String type() {
            return "Rule";
        }

        @Override
        public OptionsConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
        create(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig,
               ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new KnowledgeBuilderRulesConfigurationImpl(compConfig);
        }
    };

    public static ConfigurationFactory<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> flowConf = new ConfigurationFactory<>() {

        @Override
        public String type() {
            return "Flow";
        }

        @Override
        public OptionsConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
        create(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig,
               ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new KnowledgeBuilderFlowConfigurationImpl(compConfig);
        }
    };

}
