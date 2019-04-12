package org.jbpm.process.instance;

import org.kie.api.runtime.process.WorkItemManager;
import org.kie.services.time.TimerService;
import org.kie.services.signal.SignalManager;
import org.kie.submarine.process.WorkItemHandlerConfig;

public interface ProcessRuntimeServiceProvider {

    TimerService getTimerService();

    ProcessInstanceManager getProcessInstanceManager();

    SignalManager getSignalManager();

    WorkItemManager getWorkItemManager();
}
