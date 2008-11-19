package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBaseConfiguration;

public interface KnowledgeAgentProvider {

    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config);

    /**
     * Properties configured to load up packages into a KnowledgeBase with the provided
     * configuration (and monitor them for changes).
     */
    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config,
                                     KnowledgeBaseConfiguration ruleBaseConf);

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config,
                                     KnowledgeEventListener listener);

    /**
     * This allows an optional listener to be passed in.
     * The default one prints some stuff out to System.err only when really needed.
     */
    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config,
                                     KnowledgeEventListener listener,
                                     KnowledgeBaseConfiguration ruleBaseConf);
}
