/**
 * 
 */
package org.drools.concurrent;

import java.io.Serializable;


/**
 * This class instance is configed by the RuleBaseConfiguration and is responsible for thread management
 * of the async services.
 *
 */
public interface ExecutorService extends Serializable {
    
    /**
     * The CommandExecutor is a producer/consumer style class that handles the queue and execution
     * of the async actions
     * @param executor
     */
    public void setCommandExecutor(CommandExecutor executor);
    
    /**
     * Submit a command for execution, adds it ot the commandExecutor's queue
     * @param command
     * @return
     */
    Future submit(Command command);
    
    /**
     * Shutdown this ExecutorService
     *
     */
    void shutDown();
    
    /**
     * Startup this ExecutorService, typically called on first submit for lazy startup.
     *
     */
    void startUp();
}