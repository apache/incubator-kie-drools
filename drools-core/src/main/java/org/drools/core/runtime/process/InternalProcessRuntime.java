package org.drools.core.runtime.process;

import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.internal.process.CorrelationAwareProcessRuntime;

public interface InternalProcessRuntime extends ProcessRuntime, ProcessEventManager, CorrelationAwareProcessRuntime {

    void dispose();

    void clearProcessInstances();

    void clearProcessInstancesState();

}
