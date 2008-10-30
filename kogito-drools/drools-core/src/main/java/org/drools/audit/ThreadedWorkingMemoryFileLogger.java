package org.drools.audit;

import org.drools.WorkingMemoryEventManager;
import org.drools.audit.event.LogEvent;
import org.drools.WorkingMemory;

public class ThreadedWorkingMemoryFileLogger extends WorkingMemoryFileLogger {
    
    private int interval = 1000;
    private Writer writer;
    
    public ThreadedWorkingMemoryFileLogger(WorkingMemoryEventManager workingMemory) {
        super(workingMemory);
        setSplit(false);
    }
    
    public void start(int interval) {
        this.interval = interval;
        writer = new Writer();
        new Thread(writer).start();
    }
    
    public void stop() {
        writer.interrupt();
        writeToDisk();
    }
    
    public synchronized void logEventCreated(final LogEvent logEvent) {
        super.logEventCreated(logEvent);
    }
    
    public synchronized void writeToDisk() {
        super.writeToDisk();
    }
    
    private class Writer implements Runnable {
        private boolean interrupt = false;
        public void run() {
            while (!interrupt) {
                try {
                    Thread.sleep(interval);
                } catch (Throwable t) {
                    // do nothing
                }
                writeToDisk();
            }
        }
        public void interrupt() {
            this.interrupt = true;
        }
    }

}
