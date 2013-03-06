package org.jbpm.runtime.manager.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.utils.OnErrorAction;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.WorkingMemoryEventListener;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.manager.Disposable;
import org.kie.runtime.manager.DisposeListener;
import org.kie.runtime.manager.Runtime;
import org.kie.runtime.process.WorkItemHandler;

public class DefaultRegisterableItemsFactory extends SimpleRegisterableItemsFactory {

    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(Runtime runtime) {
        Map<String, WorkItemHandler> defaultHandlers = new HashMap<String, WorkItemHandler>();
        //HT handler 
        WorkItemHandler handler = getHTWorkItemHandler(runtime);
        defaultHandlers.put("Human Task", handler);
        // add any custom registered
        defaultHandlers.putAll(super.getWorkItemHandlers(runtime));
        
        return defaultHandlers;
    }


    @Override
    public List<ProcessEventListener> getProcessEventListeners(Runtime runtime) {
        List<ProcessEventListener> defaultListeners = new ArrayList<ProcessEventListener>();
        
        // add any custom listeners
        defaultListeners.addAll(super.getProcessEventListeners(runtime));
        return defaultListeners;
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(Runtime runtime) {
        List<AgendaEventListener> defaultListeners = new ArrayList<AgendaEventListener>();
        
        // add any custom listeners
        defaultListeners.addAll(super.getAgendaEventListeners(runtime));
        return defaultListeners;
    }

    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(Runtime runtime) {
        // register JPAWorkingMemoryDBLogger
        AuditLoggerFactory.newInstance(Type.JPA, (StatefulKnowledgeSession)runtime.getKieSession(), null);
        List<WorkingMemoryEventListener> defaultListeners = new ArrayList<WorkingMemoryEventListener>();
        
        // add any custom listeners
        defaultListeners.addAll(super.getWorkingMemoryEventListeners(runtime));
        return defaultListeners;
    }


    protected WorkItemHandler getHTWorkItemHandler(Runtime<TaskServiceEntryPoint> runtime) {
        final LocalHTWorkItemHandler handler = new LocalHTWorkItemHandler(
                runtime.getTaskService(), (KnowledgeRuntime)runtime.getKieSession(), OnErrorAction.RETHROW);
        handler.connect();
        if (runtime instanceof Disposable) {
            ((Disposable)runtime).addDisposeListener(new DisposeListener() {
                
                @Override
                public void onDispose(Runtime runtime) {
                    try {
                        handler.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        return handler;
    }    
}
