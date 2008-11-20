package org.drools.agent;

import org.drools.KnowledgeBase;

/**
 * The KnowlegeAgent is created by the KnowlegeAgentFactory. It's roll is to provide a cached
 * KnowlegeBase and to update or rebuild this KnowlegeBase as the resources it uses are changed.
 * The strategy for this is determined by the configuration given to the factory, but it is 
 * typically pull based using regular polling. We hope to add push based updates and rebuilds in future
 * versions.
 * 
 */
public interface KnowledgeAgent {
    /**
     * 
     * @return
     *    The name
     */
    String getName();

    /**
     * Returns the cached KnowledgeBase
     * @return
     *     The KnowledgeBase
     */
    KnowledgeBase getKnowledgeBase();
}
