package org.drools.kiesession.audit;

import org.drools.core.WorkingMemory;
import org.kie.api.event.KieRuntimeEventManager;

public class ThreadedWorkingMemoryFileLogger extends WorkingMemoryFileLogger {

    private int    interval = 1000;
    private Writer writer;

    public ThreadedWorkingMemoryFileLogger(WorkingMemory workingMemory) {
        super( workingMemory );
        setSplit( false );
    }

    public ThreadedWorkingMemoryFileLogger(KieRuntimeEventManager session) {
        super( session );
        setSplit( false );
    }

    public void start(int interval) {
        this.interval = interval;
        writer = new Writer();
        new Thread( writer ).start();
    }

    public void stop() {
        writer.interrupt();
        super.stop();
    }

    public synchronized void logEventCreated(final LogEvent logEvent) {
        super.logEventCreated( logEvent );
    }

    public synchronized void writeToDisk() {
        super.writeToDisk();
    }

    private class Writer
        implements
        Runnable {
        private boolean interrupt = false;

        public void run() {
            while ( !interrupt ) {
                try {
                    Thread.sleep( interval );
                } catch ( Throwable t ) {
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
