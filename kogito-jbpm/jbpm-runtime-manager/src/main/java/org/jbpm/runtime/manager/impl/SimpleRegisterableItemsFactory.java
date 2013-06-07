/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;

/**
 * The most basic <code>RegisterableItemsFactory</code> implementation that allows to define listeners and work
 * item handlers by their class and whenever they will be required new instance will be created out of the given
 * <code>Class</code> instance.
 * It's construction is limited to only two options:
 * <ul>
 *  <li>default - no argument constructor</li>
 *  <li>single argument constructor of type <code>KieSession</code></li>
 * </ul> 
 * to populate this factory with class definitions use halper methods:
 * <ul>
 *  <li>addWorkItemHandler</li>
 *  <li>addProcessListener</li>
 *  <li>addAgendaListener</li>
 *  <li>addWorkingMemoryListener</li>
 * </ul>
 */
public class SimpleRegisterableItemsFactory implements RegisterableItemsFactory {

    private Map<String, Class<? extends WorkItemHandler>> workItemHandlersClasses = new ConcurrentHashMap<String, Class<? extends WorkItemHandler>>();
    private List<Class<? extends ProcessEventListener>> processListeners = new CopyOnWriteArrayList<Class<? extends ProcessEventListener>>();
    private List<Class<? extends AgendaEventListener>> agendListeners = new CopyOnWriteArrayList<Class<? extends AgendaEventListener>>();
    private List<Class<? extends WorkingMemoryEventListener>> workingMemoryListeners = new CopyOnWriteArrayList<Class<? extends WorkingMemoryEventListener>>();
    
    @Override
    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
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
    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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
    public List<AgendaEventListener> getAgendaEventListeners(RuntimeEngine runtime) {
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
    public List<WorkingMemoryEventListener> getWorkingMemoryEventListeners(RuntimeEngine runtime) {
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
