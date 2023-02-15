/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.compiler.xml.compiler;

import java.util.Properties;

import org.drools.compiler.builder.impl.CompositeBuilderConfiguration;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationFactories;
import org.drools.compiler.builder.impl.KnowledgeBuilderFactoryServiceImpl;
import org.kie.api.conf.OptionsConfiguration;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

public class SemanticKnowledgeBuilderFactoryService extends KnowledgeBuilderFactoryServiceImpl {

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration() {
        ClassLoader projClassLoader = getClassLoader(null);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        return new CompositeBuilderConfiguration(chained, projClassLoader,
                SemanticConfigurationFactory.INSTANCE,
                KnowledgeBuilderConfigurationFactories.ruleConf,
                KnowledgeBuilderConfigurationFactories.flowConf);
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(ClassLoader classLoader) {
        return newKnowledgeBuilderConfiguration(null, classLoader);
    }

    @Override
    public KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration(Properties properties, ClassLoader classLoader) {
        ClassLoader projClassLoader = getClassLoader(classLoader);

        ChainedProperties chained = ChainedProperties.getChainedProperties(projClassLoader);

        if (properties != null) {
            chained.addProperties(properties);
        }

        return new CompositeBuilderConfiguration(chained, projClassLoader,
                SemanticConfigurationFactory.INSTANCE,
                KnowledgeBuilderConfigurationFactories.ruleConf,
                KnowledgeBuilderConfigurationFactories.flowConf);
    }

    @Override
    public int servicePriority() {
        return 1;
    }

    private static class SemanticConfigurationFactory implements ConfigurationFactory<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> {

        private static final SemanticConfigurationFactory INSTANCE = new SemanticConfigurationFactory();

        @Override
        public String type() {
            return "Base";
        }

        @Override
        public OptionsConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>
                create(CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> compConfig,
                        ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new SemanticKnowledgeBuilderConfigurationImpl(compConfig);
        }
    };
}
