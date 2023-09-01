package org.drools.mvel.compiler.builder.impl;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.junit.Test;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.ParallelRulesBuildThresholdOption;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBuilderConfigurationImplTest {

    @Test
    public void testParallelRulesBuildThresholdConfiguration() {
        try {
            System.getProperties().put(ParallelRulesBuildThresholdOption.PROPERTY_NAME, "20");
            KnowledgeBuilderConfigurationImpl kbConfigImpl = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
            assertThat(kbConfigImpl.getOption(ParallelRulesBuildThresholdOption.KEY).getParallelRulesBuildThreshold()).isEqualTo(20);
        } finally {
            System.getProperties().remove(ParallelRulesBuildThresholdOption.PROPERTY_NAME);
        }
    }

    @Test
    public void testMinusOneParallelRulesBuildThresholdConfiguration() {
        try {
            System.getProperties().put(ParallelRulesBuildThresholdOption.PROPERTY_NAME, "-1");
            KnowledgeBuilderConfigurationImpl kbConfigImpl = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
            assertThat(kbConfigImpl.getOption(ParallelRulesBuildThresholdOption.KEY).getParallelRulesBuildThreshold()).isEqualTo(-1);
        } finally {
            System.getProperties().remove(ParallelRulesBuildThresholdOption.PROPERTY_NAME); 
        }
    }

}
