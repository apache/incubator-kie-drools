package org.jbpm.process.instance;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.JDKTimerService;
import org.jbpm.process.instance.event.DefaultSignalManager;
import org.jbpm.process.instance.event.SignalManager;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManager;

public class LightProcessRuntimeServiceProvider implements ProcessRuntimeServiceProvider {

    private final TimerService timerService = new JDKTimerService();

    @Override
    public TimerService getTimerService() {
        return timerService;
    }

    @Override
    public ProcessInstanceManager getProcessInstanceManager() {
        return new DefaultProcessInstanceManager();
    }

    @Override
    public SignalManager getSignalManager(InternalKnowledgeRuntime kruntime) {
        return new DefaultSignalManager(kruntime);
    }
}
