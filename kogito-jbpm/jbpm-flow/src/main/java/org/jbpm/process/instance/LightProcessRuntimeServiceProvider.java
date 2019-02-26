package org.jbpm.process.instance;

import java.util.Optional;

import org.jbpm.process.instance.impl.DefaultProcessInstanceManager;
import org.kie.services.signal.LightSignalManager;
import org.kie.services.signal.SignalManager;
import org.kie.services.time.TimerService;
import org.kie.services.time.impl.JDKTimerService;

public class LightProcessRuntimeServiceProvider implements ProcessRuntimeServiceProvider {

    private final TimerService timerService;
    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;

    public LightProcessRuntimeServiceProvider() {
        timerService = new JDKTimerService();
        processInstanceManager = new DefaultProcessInstanceManager();
        signalManager = new LightSignalManager(
                id -> Optional.ofNullable(
                        processInstanceManager.getProcessInstance(id)));
    }

    @Override
    public TimerService getTimerService() {
        return timerService;
    }

    @Override
    public ProcessInstanceManager getProcessInstanceManager() {
        return processInstanceManager;
    }

    @Override
    public SignalManager getSignalManager() {
        return signalManager;
    }

}
