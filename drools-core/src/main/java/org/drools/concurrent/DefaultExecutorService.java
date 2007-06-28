/**
 * 
 */
package org.drools.concurrent;

import org.drools.WorkingMemory;

public class DefaultExecutorService implements ExecutorService {   
    private Thread thread;
    private CommandExecutor executor;
    private boolean running;
    
    public DefaultExecutorService() {
        
    }
    
    public void setCommandExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    public void startUp() {
        this.thread = new Thread( executor );
        this.thread.start();
        this.running = true;
    }
    
    public void shutDown() {
        this.executor.shutdown();
        this.running = false;
    }             
    
    public Future submit(Command command) {
        if (!this.running) {
            startUp();
        }
        return this.executor.submit( command );
    }
}