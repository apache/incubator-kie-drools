package org.drools.audit;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemoryEventManager;
import org.drools.audit.event.LogEvent;
import org.drools.runtime.StatefulKnowledgeSession;

public class WorkingMemoryConsoleLogger extends WorkingMemoryLogger {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public WorkingMemoryConsoleLogger(WorkingMemoryEventManager workingMemory) {
        super(workingMemory);
    }
    
    public WorkingMemoryConsoleLogger(StatefulKnowledgeSession session) {
    	super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        System.out.println(logEvent);
    }

}
