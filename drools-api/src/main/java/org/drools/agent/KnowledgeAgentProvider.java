package org.drools.agent;

import java.util.Properties;

import org.drools.KnowledgeBaseConfiguration;

/**
 * KnowledgeAgentProvider is used by the KnowledgeAgentFacotry to "provide" it's concrete implementation.
 * 
 * This class is not considered stable and may change, the user is protected from this change by using 
 * the Factory api, which is consiered stable.
 *
 */
public interface KnowledgeAgentProvider {

    /**
     * Create and return a new KnowlegeAgent using the given name and configuration.
     * 
     * @param name
     * @param config
     * @return
     *     The KnowledgeAgent
     */
    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config);

    /**
     * Create and return a new KnowlegeAgent using the given name and configuration.
     * A listener is also specified for callback type logging on for info, warning,
     * exception and debug. The KnowledgeBaseConfiguration will be used by the 
     * KnowledgeBases that the RuleAgent creates.
     * 
     * @param name
     * @param config
     * @param listener
     * @param kbaseConf
     * @return
     *     The KnowledgeAgent
     */
    KnowledgeAgent newKnowledgeAgent(String name,
                                     Properties config,
                                     KnowledgeEventListener listener,
                                     KnowledgeBaseConfiguration ruleBaseConf);
}
