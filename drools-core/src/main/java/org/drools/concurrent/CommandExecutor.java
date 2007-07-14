/**
 * 
 */
package org.drools.concurrent;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.util.concurrent.locks.BlockingQueue;
import org.drools.util.concurrent.locks.LinkedBlockingQueue;

/**
 * The CommandExecutor is a Producer/Consumer style classes that provides a queue of Commands
 * in a LinkedBlockingQueue. This the run() method loops for continously until shutdown() is 
 * called.
 *
 */
public class CommandExecutor implements Runnable, Serializable {
    private WorkingMemory workingMemory;
    private BlockingQueue queue;
    
    private volatile boolean run;
    
    
    public CommandExecutor(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;            
        this.queue = new LinkedBlockingQueue();
    }        
    
    /**
     * Allows the looping run() method to execute. 
     *
     */
    public void shutdown() {
        this.run = false;
    }        
    
    /**
     * Submit a Command for execution
     * 
     * @param command
     * 
     * @return
     *     return the Future
     */
    public Future submit(Command command) {
        this.queue.offer( command );
        // we know our commands also implement Future
        return (Future) command;
    }

    public void run() {
        this.run = true;
        while (this.run) {
            try {
                Command executor = ( Command ) this.queue.take();
                executor.execute( this.workingMemory );
            } catch(InterruptedException e) {
                return;
            }
        }
    }        
}