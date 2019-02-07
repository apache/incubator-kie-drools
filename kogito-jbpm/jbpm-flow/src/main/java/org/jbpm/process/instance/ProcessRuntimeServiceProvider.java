package org.jbpm.process.instance;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.time.TimerService;
import org.jbpm.process.instance.event.SignalManager;

public interface ProcessRuntimeServiceProvider {

    TimerService getTimerService();

    ProcessInstanceManager getProcessInstanceManager();

    SignalManager getSignalManager(InternalKnowledgeRuntime kruntime);
}
