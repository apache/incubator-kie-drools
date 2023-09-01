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
