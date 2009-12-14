package org.drools.agent;

import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.SystemEventListener;
import org.drools.io.Resource;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatelessKnowledgeSession;

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
    
    
    /**
     * StatelessKnowledgeSession created from here will always have the execute() method called against the latest built KnowledgeBase
     * @return
     */
    StatelessKnowledgeSession newStatelessKnowledgeSession();
    
    /**
     * StatelessKnowledgeSession created from here will always have the execute() method called against the latest built KnowledgeBase
     * @return
     */    
    StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf);

    void monitorResourceChangeEvents(boolean monitor);

    void applyChangeSet(Resource resource);

    void applyChangeSet(ChangeSet changeSet);

    void setSystemEventListener(SystemEventListener listener);
}
