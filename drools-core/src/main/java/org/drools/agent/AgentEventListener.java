package org.drools.agent;

/**
 * This interface is used to provide callback style logging for the agents
 * async events.
 * 
 * @author Michael Neale
 */
public interface AgentEventListener extends KnowledgeEventListener {

    /**
     * This sets the name for logging.
     */
    public void setAgentName(String name);

}
