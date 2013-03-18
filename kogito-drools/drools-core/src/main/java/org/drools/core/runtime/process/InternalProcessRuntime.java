package org.drools.core.runtime.process;

import org.drools.core.event.ProcessEventSupport;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.api.runtime.process.ProcessRuntime;

public interface InternalProcessRuntime extends ProcessRuntime, ProcessEventManager, CorrelationAwareProcessRuntime {

    void dispose();

    void setProcessEventSupport(ProcessEventSupport processEventSupport);

    void clearProcessInstances();

    void clearProcessInstancesState();

}
