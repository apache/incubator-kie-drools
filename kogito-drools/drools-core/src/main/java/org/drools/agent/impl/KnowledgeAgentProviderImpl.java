/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentProvider;
import org.drools.builder.KnowledgeBuilderConfiguration;

public class KnowledgeAgentProviderImpl implements KnowledgeAgentProvider {
    
    public KnowledgeAgentConfiguration newKnowledgeAgentConfiguration() {
        return new KnowledgeAgentConfigurationImpl();
    }
    
    public KnowledgeAgentConfiguration newKnowledgeAgentConfiguration(Properties properties) {
        return new KnowledgeAgentConfigurationImpl(properties);
    }    

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            KnowledgeBase kbase) {
        return new KnowledgeAgentImpl(name, kbase, new KnowledgeAgentConfigurationImpl(),null );
    }

    public KnowledgeAgent newKnowledgeAgent(String name,
                                            KnowledgeBase kbase,
                                            KnowledgeAgentConfiguration configuration) {
        return new KnowledgeAgentImpl(name, kbase, configuration, null);
    }

    public KnowledgeAgent newKnowledgeAgent(String name, KnowledgeBase kbase, KnowledgeAgentConfiguration configuration, KnowledgeBuilderConfiguration builderConfiguration) {
        return new KnowledgeAgentImpl(name, kbase, configuration, builderConfiguration);
    }


}
