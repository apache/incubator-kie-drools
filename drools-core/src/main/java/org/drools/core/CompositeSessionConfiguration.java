package org.drools.core;

import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.KieSessionOption;
import org.kie.api.runtime.conf.MultiValueKieSessionOption;
import org.kie.api.runtime.conf.SingleValueKieSessionOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

public class CompositeSessionConfiguration extends CompositeConfiguration<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption> implements KieSessionConfiguration {

    public CompositeSessionConfiguration(ChainedProperties properties, ClassLoader classloader,
                                         ConfigurationFactory<KieSessionOption, SingleValueKieSessionOption, MultiValueKieSessionOption>... factories) {
        super(properties, classloader, factories);
    }

}
