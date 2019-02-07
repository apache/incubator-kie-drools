package org.jbpm.process.instance;

import org.kie.services.time.TimerService;
import org.kie.services.signal.SignalManager;

public interface ProcessRuntimeServiceProvider {

    TimerService getTimerService();

    ProcessInstanceManager getProcessInstanceManager();

    SignalManager getSignalManager();
}
