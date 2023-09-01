package org.drools.core;

import org.kie.api.conf.OptionsConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

public class SessionConfigurationFactories {
    public static ConfigurationFactory<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> baseConf = new ConfigurationFactory<>() {

        @Override public String type() {
            return "Base";
        }

        @Override public OptionsConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption>
                                             create(CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig,
                                                    ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new SessionConfiguration(compConfig);
        }
    };

    public static ConfigurationFactory<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> ruleConf = new ConfigurationFactory<>() {

        @Override public String type() {
            return "Rule";
        }

        @Override public OptionsConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption>
                                             create(CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig,
                                                    ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new RuleSessionConfiguration(compConfig);
        }
    };

    public static ConfigurationFactory<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> flowConf = new ConfigurationFactory<>() {

        @Override public String type() {
            return "Flow";
        }

        @Override public OptionsConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption>
                                             create(CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> compConfig,
                                                     ClassLoader classLoader, ChainedProperties chainedProperties) {
            return new FlowSessionConfiguration(compConfig);
        }
    };

}
