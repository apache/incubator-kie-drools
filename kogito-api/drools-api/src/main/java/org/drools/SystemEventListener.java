package org.drools;

/**
 * <p>
 * This interface is used to provide callback style logging for the system events.
 * </p>
 * 
 * <p>
 * The SystemEventListenerFactory is used to provide the default SystemEventListener to various Drools components
 * such as the KnowledgeAgent, ResourceChangeNotifier and ResourceChangeListener. Although many of these components
 * allow the used listener to be overriden with a setSystemEventListener(SystemEventListener) method.
 * </p>
 * 
 * Componens 
 * 
 */
public interface SystemEventListener {
    /**
     * For general info messages
     */
    public void info(String message);
    
    public void info(String message, Object object);

    /**
     * For a warning (useful when tracking down problems).
     */
    public void warning(String message);
    
    public void warning(String message, Object object);

    /**
     * An exception occurred.
     */
    public void exception(Exception e);

    /**
     * These should not be logged, just shown if needed.
     */
    public void debug(String message);
    
    public void debug(String message, Object object);
}
