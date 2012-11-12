package org.drools.runtime.process;

import org.drools.event.ProcessEventSupport;
import org.kie.event.process.ProcessEventManager;
import org.kie.runtime.process.ProcessRuntime;

public interface InternalProcessRuntime extends ProcessRuntime, ProcessEventManager {

    void dispose();

    void setProcessEventSupport(ProcessEventSupport processEventSupport);

    void clearProcessInstances();

    void clearProcessInstancesState();

}
