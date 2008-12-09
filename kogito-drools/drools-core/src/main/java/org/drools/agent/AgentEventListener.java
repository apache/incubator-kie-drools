package org.drools.agent;

import org.drools.SystemEventListener;

/**
 * This interface is used to provide callback style logging for the agents
 * async events.
 * 
 * @author Michael Neale
 */
public interface AgentEventListener extends SystemEventListener {

    /**
     * This sets the name for logging.
     */
    public void setAgentName(String name);

}
