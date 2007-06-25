package org.drools.agent;

/**
 * This interface is used to provide callback style logging for the agents
 * async events.
 * 
 * @author Michael Neale
 */
public interface AgentEventListener {

    /**
     * For general info messages.
     */
    public void info(String configName, String message);
    
    /**
     * For a warning (useful when tracking down problems).
     */
    public void warning(String configName, String message);
    
    /**
     * An exception occurred.
     */
    public void exception(String configName, Exception e);
    
    
    /**
     * These should not be logged, just shown if needed.
     */
    public void debug(String configName, String message);
}
