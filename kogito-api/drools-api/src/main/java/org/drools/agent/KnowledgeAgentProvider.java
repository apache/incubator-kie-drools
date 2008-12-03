package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBase;

public interface KnowledgeAgentProvider {
    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase,
                                     KnowledgeAgentConfiguration configuration);

    KnowledgeAgent newKnowledgeAgent(String name,
                                     KnowledgeBase kbase,
                                     KnowledgeAgentConfiguration configuration,
                                     KnowledgeAgentEventListener listener);
}
