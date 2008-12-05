package org.drools.agent;

import org.drools.KnowledgeBase;

/**
 * <p>
 * The KnowlegeAgent is created by the KnowlegeAgentFactory. It's role is to provide a cached
 * KnowlegeBase and to update or rebuild this KnowlegeBase as the resources it uses are changed.
 * The strategy for this is determined by the configuration given to the factory, but it is 
 * typically pull based using regular polling. We hope to add push based updates and rebuilds in future
 * versions.
 * </p>
 * <p>
 * The Follow example constructs an agent that will build a new KnowledgeBase from the files specified in the path String.
 * It will poll those files every 30 seconds to see if they are updated. If new files are found it will construct a new 
 * KnowledgeBase, instead of upating the existing one, due to the "newInstance" set to "true":
 * <p/>
 * <pre>
 * Properties props = new Properties();
 * props.setProperty( "file", path );
 *
 * props.setProperty( "newInstance", "true" );
 * props.setProperty( "poll", "30" );
 * KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent( "agent1", props );
 * KnowledgeBase kbase = kagent.getKnowledgeBase();
 * </pre>
 * 
 * @see org.drools.agent.KnowledgeAgentFactory
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
}
