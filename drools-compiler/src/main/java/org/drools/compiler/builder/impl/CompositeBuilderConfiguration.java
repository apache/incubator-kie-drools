package org.drools.compiler.builder.impl;


import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.KnowledgeBuilderOption;
import org.kie.internal.builder.conf.MultiValueKieBuilderOption;
import org.kie.internal.builder.conf.SingleValueKieBuilderOption;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.conf.ConfigurationFactory;
import org.kie.internal.utils.ChainedProperties;

import java.util.Properties;

public class CompositeBuilderConfiguration extends CompositeConfiguration<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption> implements KnowledgeBuilderConfiguration {

    public CompositeBuilderConfiguration(ChainedProperties properties, ClassLoader classloader,
                                         ConfigurationFactory<KnowledgeBuilderOption, SingleValueKieBuilderOption, MultiValueKieBuilderOption>... factories) {
        super(properties, classloader, factories);
    }

}
