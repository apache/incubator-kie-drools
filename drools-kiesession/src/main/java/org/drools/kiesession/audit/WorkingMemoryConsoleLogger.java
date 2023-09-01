package org.drools.kiesession.audit;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.WorkingMemory;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieRuntimeLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkingMemoryConsoleLogger extends WorkingMemoryLogger implements KieRuntimeLogger {

    protected static final transient Logger logger = LoggerFactory.getLogger(WorkingMemoryConsoleLogger.class);

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public WorkingMemoryConsoleLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }
    
    public WorkingMemoryConsoleLogger(KieRuntimeEventManager session) {
        super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        logger.info(logEvent.toString());
    }

    @Override
    public void close() {
        // nothing to do.
    }

}
