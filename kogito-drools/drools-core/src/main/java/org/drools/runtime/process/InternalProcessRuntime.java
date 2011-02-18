package org.drools.runtime.process;

import org.drools.event.ProcessEventSupport;
import org.drools.event.process.ProcessEventManager;

public interface InternalProcessRuntime extends ProcessRuntime, ProcessEventManager {

    void dispose();

    void setProcessEventSupport(ProcessEventSupport processEventSupport);

    void clearProcessInstances();

}
