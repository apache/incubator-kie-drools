/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.submarine.process.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.submarine.process.Process;
import org.kie.submarine.process.ProcessInstance;
import org.kie.submarine.process.Signal;

public abstract class AbstractProcessInstance<T> implements ProcessInstance<T> {

    private final T variables;
    private final AbstractProcess<T> process;
    private final ProcessRuntime rt;
    private org.kie.api.runtime.process.ProcessInstance legacyProcessInstance;
    private int status;

    public AbstractProcessInstance(AbstractProcess<T> process, T variables, ProcessRuntime rt) {
        this.process = process;
        this.rt = rt;
        this.variables = variables;
    }

    public void start() {
        Map<String, Object> map = bind(variables);
        String id = process.legacyProcess().getId();
        this.legacyProcessInstance = rt.createProcessInstance(id, map);
        long pid = legacyProcessInstance.getId();
        process.instances().update(pid, this);
        org.kie.api.runtime.process.ProcessInstance pi = this.rt.startProcessInstance(pid);
        unbind(variables, pi.getVariables());
        
        this.status = pi.getState();
    }

    public void abort() {
        if (legacyProcessInstance == null) {
            return;
        }
        long pid = legacyProcessInstance.getId();
        unbind(variables, legacyProcessInstance.getVariables());
        process.instances().remove(pid);
        this.rt.abortProcessInstance(pid);
        
        this.status = legacyProcessInstance.getState();
    }

    @Override
    public <S> void send(Signal<S> signal) {
        legacyProcessInstance.signalEvent(signal.channel(), signal.payload());
        
        this.status = legacyProcessInstance.getState();
    }

    @Override
    public Process<T> process() {
        return process;
    }

    @Override
    public T variables() {
        return variables;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public long id() {
        return legacyProcessInstance.getId();
    }

    @Override
    public void completeWorkItem(long id, T variables) {
        this.rt.getWorkItemManager().completeWorkItem(id, bind(variables));
        unbind(variables, legacyProcessInstance.getVariables());
        this.status = legacyProcessInstance.getState();
    }

    // this must be overridden at compile time
    protected Map<String, Object> bind(T variables) {
        HashMap<String, Object> vmap = new HashMap<>();
        if (variables == null) {
            return vmap;
        }
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = null;
                v = f.get(variables);
                vmap.put(f.getName(), v);
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
        return vmap;
    }

    protected void unbind(T variables, Map<String, Object> vmap) {
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                f.set(variables, vmap.get(f.getName()));
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
    }
}
