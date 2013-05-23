package org.jbpm.runtime.manager.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.core.time.TimerService;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.process.core.timer.GlobalSchedulerService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.runtime.manager.impl.tx.DisposeSessionTransactionSynchronization;
import org.jbpm.runtime.manager.impl.tx.ExtendedJTATransactionManager;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;

public abstract class AbstractRuntimeManager implements InternalRuntimeManager {

    protected volatile static List<String> activeManagers = new CopyOnWriteArrayList<String>();
    protected RuntimeEnvironment environment;
    
    protected String identifier;
    
    public AbstractRuntimeManager(RuntimeEnvironment environment, String identifier) {
        this.environment = environment;
        this.identifier = identifier;
        if (activeManagers.contains(identifier)) {
            throw new IllegalStateException("RuntimeManager with id " + identifier + " is already active");
        }
        
    }
    
    public abstract void init();
    
    protected void registerItems(RuntimeEngine runtime) {
        RegisterableItemsFactory factory = environment.getRegisterableItemsFactory();
        // process handlers
        Map<String, WorkItemHandler> handlers = factory.getWorkItemHandlers(runtime);
        for (Entry<String, WorkItemHandler> entry : handlers.entrySet()) {
            runtime.getKieSession().getWorkItemManager().registerWorkItemHandler(entry.getKey(), entry.getValue());
        }
        
        // process listeners
        List<ProcessEventListener> processListeners = factory.getProcessEventListeners(runtime);
        for (ProcessEventListener listener : processListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
        
        List<AgendaEventListener> agendaListeners = factory.getAgendaEventListeners(runtime);
        for (AgendaEventListener listener : agendaListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
        
        List<WorkingMemoryEventListener> wmListeners = factory.getWorkingMemoryEventListeners(runtime);
        for (WorkingMemoryEventListener listener : wmListeners) {
            runtime.getKieSession().addEventListener(listener);
        }
    }
    
    protected void registerDisposeCallback(RuntimeEngine runtime, TransactionSynchronization sync) {
        // register it if there is an active transaction as we assume then to be running in a managed environment e.g CMT
        // TODO is there better way to register transaction synchronization?
        JtaTransactionManager tm = new ExtendedJTATransactionManager(null, null, null);
        if (tm.getStatus() != JtaTransactionManager.STATUS_NO_TRANSACTION
                && tm.getStatus() != JtaTransactionManager.STATUS_ROLLEDBACK
                && tm.getStatus() != JtaTransactionManager.STATUS_COMMITTED) {
            tm.registerTransactionSynchronization(sync);
        }
    }
    
    protected void attachManager(RuntimeEngine runtime) {
        runtime.getKieSession().getEnvironment().set("RuntimeManager", this);
    }

    @Override
    public void close() {
        environment.close();
        activeManagers.remove(identifier);
        TimerService timerService = TimerServiceRegistry.getInstance().remove(getIdentifier()+"-timerServiceId");
        if (timerService != null) {
            timerService.shutdown();
            GlobalSchedulerService schedulerService = ((SchedulerProvider) environment).getSchedulerService();  
            if (schedulerService != null) {
                schedulerService.shutdown();
            }
        }
    }

    public RuntimeEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
