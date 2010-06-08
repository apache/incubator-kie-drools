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
