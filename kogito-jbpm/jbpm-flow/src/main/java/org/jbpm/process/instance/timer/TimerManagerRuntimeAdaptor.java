package org.jbpm.process.instance.timer;

import java.util.Map;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessRuntimeImpl;
import org.kie.services.time.manager.TimerManager;
import org.kie.services.time.manager.TimerManagerRuntime;
import org.kie.api.time.SessionClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.signal.SignalManager;

public class TimerManagerRuntimeAdaptor implements TimerManagerRuntime {
    private final InternalKnowledgeRuntime kruntime;

    public TimerManagerRuntimeAdaptor(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
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
        return getProcessRuntime().getSignalManager();
    }

    private InternalProcessRuntime getProcessRuntime() {
        return (InternalProcessRuntime) kruntime.getProcessRuntime();
    }

    @Override
    public TimerManager getTimerManager() {
        return getProcessRuntime().getTimerManager();
    }

    @Override
    public boolean isActive() {
        InternalProcessRuntime processRuntime = getProcessRuntime();
        return (processRuntime instanceof ProcessRuntimeImpl)
                && ((ProcessRuntimeImpl) processRuntime).isActive();
    }

    @Override
    public void startProcess(String processId, Map<String, Object> parameters, String timer) {
        ((ProcessRuntimeImpl) getProcessRuntime()).startProcess(processId, parameters, timer);
    }

    @Override
    public long getIdentifier() {
        return ((StatefulKnowledgeSession)kruntime).getIdentifier();
    }
}
