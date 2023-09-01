package org.drools.core;

import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.OptionsConfiguration;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

public class BaseConfigurationFactories {
    public static ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> baseConf = new ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>() {

        @Override public String type() {
            return "Base";
        }

        @Override public OptionsConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
                                              create(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig,
                                                     ClassLoader classLoader,
                                                     ChainedProperties chainedProperties) {
            return new KieBaseConfigurationImpl(compConfig);
        }
    };

    public static ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> ruleConf = new ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>() {

        @Override public String type() {
            return "Rule";
        }

        @Override public OptionsConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
                                             create(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig,
                                                    ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new RuleBaseConfiguration(compConfig);
        }
    };

    public static ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> flowConf = new ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>() {

        @Override public String type() {
            return "Flow";
        }

        @Override public OptionsConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>
                                             create(CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> compConfig,
                                                    ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new FlowBaseConfiguration(compConfig);
        }
    };

}
