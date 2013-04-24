package org.jbpm.runtime.manager.impl.cdi;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.runtime.manager.api.WorkItemHandlerProducer;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEngineImpl;
import org.jbpm.services.task.annotations.External;
import org.jbpm.services.task.wih.ExternalTaskEventListener;
import org.jbpm.services.task.wih.LocalHTWorkItemHandler;
import org.jbpm.services.task.wih.RuntimeFinder;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;

public class InjectableRegisterableItemsFactory extends DefaultRegisterableItemsFactory {

    @Inject
    @External
    private ExternalTaskEventListener taskListener; 
    
    @Inject
    private WorkItemHandlerProducer workItemHandlerProducer;
    
    @Inject
    private RuntimeFinder finder;
    
    private AbstractAuditLogger auditlogger;
    

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
        Map<String, WorkItemHandler> handler = new HashMap<String, WorkItemHandler>();
        handler.put("Human Task", getHTWorkItemHandler(runtime));
        
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ksession", runtime.getKieSession());
        params.put("taskClient", runtime.getTaskService());
        handler.putAll(workItemHandlerProducer.getWorkItemHandlers(manager.getIdentifier(), params));
        return handler;
    }
    
    protected WorkItemHandler getHTWorkItemHandler(RuntimeEngine runtime) {
        
        RuntimeManager manager = ((RuntimeEngineImpl)runtime).getManager();
        taskListener.setFinder(finder);
        taskListener.addMappedManger(manager.getIdentifier(), manager);
        
        LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler();
        humanTaskHandler.setRuntimeManager(manager);

        return humanTaskHandler;
    }  
    

    @Override
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
        
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        if(auditlogger != null) {
            defaultListeners.add(auditlogger);
        }
        return defaultListeners;
    }
    
    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(RuntimeEngine runtime) {
        List<WorkingMemoryEventListener> defaultListeners = new ArrayList<WorkingMemoryEventListener>();
        
        
        return defaultListeners;
    }   
    
    public static RegisterableItemsFactory getFactory(BeanManager beanManager, AbstractAuditLogger auditlogger) {
        InjectableRegisterableItemsFactory instance = getInstanceByType(beanManager, InjectableRegisterableItemsFactory.class, new Annotation[]{});
        instance.setAuditlogger(auditlogger);
        return instance;
    }
    
    
    protected static <T> T getInstanceByType(BeanManager manager, Class<T> type, Annotation... bindings) {
        final Bean<?> bean = manager.resolve(manager.getBeans(type, bindings));
        if (bean == null) {
            throw new UnsatisfiedResolutionException("Unable to resolve a bean for " + type + " with bindings " + Arrays.asList(bindings));
        }
        CreationalContext<?> cc = manager.createCreationalContext(null);
        return type.cast(manager.getReference(bean, type, cc));
    }

    public AbstractAuditLogger getAuditlogger() {
        return auditlogger;
    }

    public void setAuditlogger(AbstractAuditLogger auditlogger) {
        this.auditlogger = auditlogger;
    }

    
}
