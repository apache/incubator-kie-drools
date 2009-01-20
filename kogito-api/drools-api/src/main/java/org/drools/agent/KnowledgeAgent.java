package org.drools.agent;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListener;
import org.drools.io.Resource;

/**
 * The KnolwedgeAgentFactory provides detailed information on how to create and use the KnowledgeAgent.
 * 
 * @see org.drools.agent.KnowledgeAgentFactory
 * @see org.drools.agent.KnowledgeAgentConfiguration
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

    void monitorResourceChangeEvents(boolean monitor);

    void applyChangeSet(Resource resource);

    void setSystemEventListener(SystemEventListener listener);
}
