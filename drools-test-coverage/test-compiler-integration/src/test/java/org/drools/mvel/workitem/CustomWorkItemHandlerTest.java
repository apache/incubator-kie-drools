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
package org.drools.mvel.workitem;

import java.util.Map;
import java.util.Properties;

import org.drools.core.FlowSessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.utils.ChainedProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomWorkItemHandlerTest {

    @Test
    public void testRegisterHandlerWithKsessionUsingConfiguration() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        Properties props = new Properties();
        props.setProperty("drools.workItemHandlers", "CustomWorkItemHandlers.conf");


        ClassLoader cl = ((InternalKnowledgeBase)kbase).getConfiguration().getClassLoader();
        KieSessionConfiguration config = RuleBaseFactory.newKnowledgeSessionConfiguration(ChainedProperties.getChainedProperties(cl).addProperties(props), cl);
        
        KieSession ksession = kbase.newKieSession(config, EnvironmentFactory.newEnvironment());
        assertThat(ksession).isNotNull();
        // this test would fail on creation of the work item manager if injecting session is not supported
        WorkItemManager manager = ksession.getWorkItemManager();
        assertThat(manager).isNotNull();
        
        Map<String, WorkItemHandler> handlers = config.as(FlowSessionConfiguration.KEY).getWorkItemHandlers();
        assertThat(handlers).isNotNull();
        assertThat(handlers.size()).isEqualTo(1);
        assertThat(handlers.containsKey("Custom")).isTrue();
    }

}
