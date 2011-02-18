/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.audit;

import org.drools.WorkingMemory;
import org.drools.audit.event.LogEvent;
import org.drools.event.KnowledgeRuntimeEventManager;

public class ThreadedWorkingMemoryFileLogger extends WorkingMemoryFileLogger {
    
    private int interval = 1000;
    private Writer writer;
    
    public ThreadedWorkingMemoryFileLogger(WorkingMemory workingMemory) {
        super(workingMemory);
        setSplit(false);
    }
    
    public ThreadedWorkingMemoryFileLogger(KnowledgeRuntimeEventManager session) {
        super( session );
        setSplit( false );
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
