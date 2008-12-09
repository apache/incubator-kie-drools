package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;

public interface KnowledgeAgentProvider {
    KnowledgeAgentConfiguration newKnowledgeAgentConfiguration();
    
    KnowledgeAgentConfiguration newKnowledgeAgentConfiguration(Properties properties);
    
    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase,
                                     KnowledgeAgentConfiguration configuration);
}
