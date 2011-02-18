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

package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilderConfiguration;

/**
 * KnowledgeAgentProvider is used by the KnowledgeAgentFactory to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is considered stable.
 *
 */
public interface KnowledgeAgentProvider {
    KnowledgeAgentConfiguration newKnowledgeAgentConfiguration();

    KnowledgeAgentConfiguration newKnowledgeAgentConfiguration(Properties properties);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase,
                                     KnowledgeAgentConfiguration configuration);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase,
                                     KnowledgeAgentConfiguration configuration,
                                     KnowledgeBuilderConfiguration builderConfiguration);
}
