package org.drools.audit;

import org.drools.WorkingMemory;
import org.drools.audit.event.LogEvent;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

public class WorkingMemoryConsoleLogger extends WorkingMemoryLogger {

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public WorkingMemoryConsoleLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }

    public void logEventCreated(LogEvent logEvent) {
        System.out.println(logEvent);
    }

}
