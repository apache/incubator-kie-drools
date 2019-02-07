package org.jbpm.process.instance.timer;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.time.TimerService;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessRuntimeImpl;
import org.jbpm.process.instance.event.SignalManager;
import org.kie.api.time.SessionClock;

public class TimerManagerRuntimeAdaptor implements TimerManagerRuntime {
    private final InternalKnowledgeRuntime kruntime;
    private final InternalProcessRuntime processRuntime;

    public TimerManagerRuntimeAdaptor(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
        this.processRuntime = (InternalProcessRuntime) kruntime.getProcessRuntime();
    }

    @Override
    public void startOperation() {
        kruntime.startOperation();
    }

    @Override
    public void endOperation() {
        kruntime.endOperation();
    }

    @Override
    public SessionClock getSessionClock() {
        return kruntime.getSessionClock();
    }

    @Override
    public SignalManager getSignalManager() {
        return processRuntime.getSignalManager();
    }

    @Override
    public TimerManager getTimerManager() {
        return processRuntime.getTimerManager();
    }

    @Override
    public boolean isActive() {
        return (processRuntime instanceof ProcessRuntimeImpl)
                && ((ProcessRuntimeImpl) processRuntime).isActive();
    }

    @Override
    public void startProcess(String processId, Map<String, Object> parameters, String timer) {
        ((ProcessRuntimeImpl) processRuntime).startProcess(processId, parameters, timer);
    }
}
