package org.kie.internal.conf;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.KieBaseOption;
import org.kie.api.conf.MultiValueKieBaseOption;
import org.kie.api.conf.SingleValueKieBaseOption;
import org.kie.internal.utils.ChainedProperties;

public class CompositeBaseConfiguration extends CompositeConfiguration<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption> implements KieBaseConfiguration {

    public CompositeBaseConfiguration(ChainedProperties properties, ClassLoader classloader,
                                      ConfigurationFactory<KieBaseOption, SingleValueKieBaseOption, MultiValueKieBaseOption>... factories) {
        super(properties, classloader, factories);
    }

}
