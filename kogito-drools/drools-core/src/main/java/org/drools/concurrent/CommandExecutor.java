/**
 * 
 */
package org.drools.concurrent;

import java.io.Serializable;

import org.drools.WorkingMemory;
import org.drools.util.concurrent.locks.BlockingQueue;
import org.drools.util.concurrent.locks.LinkedBlockingQueue;

public class CommandExecutor implements Runnable, Serializable {
    private WorkingMemory workingMemory;
    private BlockingQueue queue;
    
    private volatile boolean run;
    
    
    public CommandExecutor(WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;            
        this.queue = new LinkedBlockingQueue();
    }        
    
    public void shutdown() {
        this.run = false;
    }        
    
    public Future submit(Command command) {
        this.queue.offer( command );
        // we know our commands also implement Future
        return (Future) command;
    }

    public void run() {
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