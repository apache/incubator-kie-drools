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
