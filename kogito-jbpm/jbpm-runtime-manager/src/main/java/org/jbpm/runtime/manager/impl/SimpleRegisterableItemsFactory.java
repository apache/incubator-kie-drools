package org.jbpm.runtime.manager.impl;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.WorkingMemoryEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.Runtime;

public class SimpleRegisterableItemsFactory implements RegisterableItemsFactory {

    private Map<String, Class<? extends WorkItemHandler>> workItemHandlersClasses = new ConcurrentHashMap<String, Class<? extends WorkItemHandler>>();
    private List<Class<? extends ProcessEventListener>> processListeners = new CopyOnWriteArrayList<Class<? extends ProcessEventListener>>();
    private List<Class<? extends AgendaEventListener>> agendListeners = new CopyOnWriteArrayList<Class<? extends AgendaEventListener>>();
    private List<Class<? extends WorkingMemoryEventListener>> workingMemoryListeners = new CopyOnWriteArrayList<Class<? extends WorkingMemoryEventListener>>();
    
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(Runtime runtime) {
        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
        for (Entry<String, Class<? extends WorkItemHandler>> entry : workItemHandlersClasses.entrySet()) {
            WorkItemHandler handler = createInstance(entry.getValue(), runtime.getKieSession());
            
            if (handler != null) {
                handlers.put(entry.getKey(), handler);
            }
        }
        return handlers;
    }

    @Override
    public List<ProcessEventListener> getProcessEventListeners(Runtime runtime) {
        List<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();
        for (Class<? extends ProcessEventListener> clazz : processListeners) {
            ProcessEventListener pListener = createInstance(clazz, runtime.getKieSession());
            if (pListener != null) {
                listeners.add(pListener);
            }
        }
        return listeners;
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners(Runtime runtime) {
        List<AgendaEventListener> listeners = new ArrayList<AgendaEventListener>();
        for (Class<? extends AgendaEventListener> clazz : agendListeners) {
            AgendaEventListener aListener = createInstance(clazz, runtime.getKieSession());
            if (aListener != null) {
                listeners.add(aListener);
            }
        }
        return listeners;
    }

    @Override
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(Runtime runtime) {
        List<WorkingMemoryEventListener> listeners = new ArrayList<WorkingMemoryEventListener>();
        for (Class<? extends WorkingMemoryEventListener> clazz : workingMemoryListeners) {
            WorkingMemoryEventListener wmListener = createInstance(clazz, runtime.getKieSession());
            if (wmListener != null) {
                listeners.add(wmListener);
            }
        }
        return listeners;
    }
    
    public void addWorkItemHandler(String name, Class<? extends WorkItemHandler> clazz) {
        this.workItemHandlersClasses.put(name, clazz);
    }
    
    public void addProcessListener(Class<? extends ProcessEventListener> clazz) {
        this.processListeners.add(clazz);
    }

    public void addAgendaListener(Class<? extends AgendaEventListener> clazz) {
        this.agendListeners.add(clazz);
    }
    
    public void addWorkingMemoryListener(Class<? extends WorkingMemoryEventListener> clazz) {
        this.workingMemoryListeners.add(clazz);
    }
    
    protected <T> T createInstance(Class<T> clazz, KieSession ksession) {
        T instance = null;
        try {
            Constructor<T> constructor = clazz.getConstructor(KieSession.class);
            
            instance = constructor.newInstance(ksession);
        } catch (Exception e) {

        }
        
        if (instance == null) {
            try {
                instance = clazz.newInstance();

            } catch (Exception e) {

            }                        
        }
        
        return instance;
    }
}
