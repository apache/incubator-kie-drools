/**
 * 
 */
package org.drools.concurrent;

import java.io.Serializable;



public interface ExecutorService extends Serializable {
    public void setCommandExecutor(CommandExecutor executor);
    Future submit(Command command);
    void shutDown();
    void startUp();
}