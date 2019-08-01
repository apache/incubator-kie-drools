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

package org.kie.kogito.process.impl;

import java.util.Collections;

import org.jbpm.process.instance.LightProcessRuntime;
import org.jbpm.process.instance.LightProcessRuntimeContext;
import org.jbpm.process.instance.LightProcessRuntimeServiceProvider;
import org.jbpm.process.instance.ProcessRuntimeServiceProvider;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.kogito.Model;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.process.Signal;

public abstract class AbstractProcess<T extends Model> implements Process<T> {

    protected ProcessInstancesFactory processInstancesFactory;

    protected MutableProcessInstances<T> instances;

    protected final ProcessRuntimeServiceProvider services;

    protected CompletionEventListener completionEventListener = new CompletionEventListener();

    protected AbstractProcess() {
        this(new LightProcessRuntimeServiceProvider());
    }

    protected AbstractProcess(ProcessConfig config) {
        this(new ConfiguredProcessServices(config));
    }
    
    protected AbstractProcess(ProcessRuntimeServiceProvider services) {
        this.services = services;
        this.instances = new MapProcessInstances<>();   
        
    }

    
    @Override
    public String id() {
        return legacyProcess().getId();
    }

    @Override
    public T createModel() {
        return null;
    }

    @Override
    public ProcessInstance<T> createInstance(Model m) {
        return createInstance((T) m);
    }


    @Override
    public ProcessInstances<T> instances() {
        return instances;
    }

    @Override
    public <S> void send(Signal<S> signal) {
        instances().values().forEach(pi -> pi.send(signal));
    }
    
    @SuppressWarnings("unchecked")
    public Process<T> configure() {

        registerListeners();
        if (isProcessFactorySet()) {
            this.instances = (MutableProcessInstances<T>) processInstancesFactory.createProcessInstances(this);
        }
        //services.getWorkItemManager().registerWorkItemHandler(name, handlerConfig.forName(name)
        //services.getEventSupport().addEventListener(listener)
        

        return this;
    }

    protected void registerListeners() {
        
    }

    public abstract org.kie.api.definition.process.Process legacyProcess();

    protected ProcessRuntime createLegacyProcessRuntime() {        
        return new LightProcessRuntime(
                new LightProcessRuntimeContext(Collections.singletonList(legacyProcess())),
                services);
    }

    protected boolean isProcessFactorySet() {
        return processInstancesFactory != null;
    }    
    
    public void setProcessInstancesFactory(ProcessInstancesFactory processInstancesFactory) {
        this.processInstancesFactory = processInstancesFactory;
    }
    
    private class CompletionEventListener implements EventListener {
        
        @Override
        public void signalEvent(String type, Object event) {
            if (type.startsWith("processInstanceCompleted:")) {
                org.kie.api.runtime.process.ProcessInstance pi = (org.kie.api.runtime.process.ProcessInstance) event;
                if (!id().equals(pi.getProcessId())) {
                    instances().findById(pi.getParentProcessInstanceId()).ifPresent(p -> p.send(Sig.of(type, event)));
                }
            }
        }
        
        @Override
        public String[] getEventTypes() {
            return new String[0];
        }
    }
}
