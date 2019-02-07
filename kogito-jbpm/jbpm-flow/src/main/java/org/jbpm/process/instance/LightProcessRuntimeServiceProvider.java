package org.jbpm.process.instance;

import java.util.Optional;

import org.kie.services.time.TimerService;
import org.kie.services.time.impl.JDKTimerService;
import org.kie.services.signal.LightSignalManager;
import org.kie.services.signal.SignalManager;
import org.kie.services.signal.Signalable;
import org.kie.services.signal.SignalableResolver;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManager;
import org.kie.api.runtime.process.ProcessInstance;

public class LightProcessRuntimeServiceProvider implements ProcessRuntimeServiceProvider {

    private final TimerService timerService;
    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;

    public LightProcessRuntimeServiceProvider() {
        timerService = new JDKTimerService();
        processInstanceManager = new DefaultProcessInstanceManager();
        signalManager = new LightSignalManager(
                new ProcessInstanceResolver(processInstanceManager));
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

    /**
     * Adapts a ProcessInstanceManager to the generic SignalableResolver interface
     */
    private static class ProcessInstanceResolver implements SignalableResolver {

        private final ProcessInstanceManager processInstanceManager;

        ProcessInstanceResolver(ProcessInstanceManager processInstanceManager) {
            this.processInstanceManager = processInstanceManager;
        }

        @Override
        public Optional<Signalable> find(long id) {
            return Optional.of(processInstanceManager.getProcessInstance(id))
                    .map(SignalableAdaptor::new);
        }
    }

    /**
     * Adapts a ProcessInstance to the generic Signalable interface
     */
    private static class SignalableAdaptor implements Signalable {

        private final ProcessInstance instance;

        SignalableAdaptor(org.kie.api.runtime.process.ProcessInstance instance) {
            this.instance = instance;
        }

        @Override
        public void signalEvent(String type, Object event) {
            instance.signalEvent(type, event);
        }
    }
}
