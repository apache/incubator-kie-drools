/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance.event.listeners;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;

/**
 * Process event listener that is responsible for managing process instance as fact
 * so rules can reason over it. It ensure that process instance is inserted as soon as it starts
 * and gets retracted as soon as process instance completes. In addition it updates process instance
 * whenever process variable is updated.
 *
 */
public class RuleAwareProcessEventListener implements ProcessEventListener {

    private ConcurrentHashMap<String, FactHandle> store = new ConcurrentHashMap<>();

    public void beforeProcessStarted(ProcessStartedEvent event) {

        FactHandle handle = event.getKieRuntime().insert(event.getProcessInstance());
        store.put(((KogitoProcessInstance) event.getProcessInstance()).getStringId(), handle);
    }

    public void afterProcessStarted(ProcessStartedEvent event) {
        // do nothing
    }

    public void beforeProcessCompleted(ProcessCompletedEvent event) {
        // do nothing
    }

    public void afterProcessCompleted(ProcessCompletedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(((KogitoProcessInstance) event.getProcessInstance()).getStringId(), event.getKieRuntime());

        if (handle != null) {
            event.getKieRuntime().delete(handle);
        }
    }

    public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
    }

    public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        // do nothing
    }

    public void beforeNodeLeft(ProcessNodeLeftEvent event) {
        // do nothing
    }

    public void afterNodeLeft(ProcessNodeLeftEvent event) {
        // do nothing
    }

    public void beforeVariableChanged(ProcessVariableChangedEvent event) {
        // do nothing
    }

    public void afterVariableChanged(ProcessVariableChangedEvent event) {
        FactHandle handle = getProcessInstanceFactHandle(((KogitoProcessInstance) event.getProcessInstance()).getStringId(), event.getKieRuntime());

        if (handle != null) {
            event.getKieRuntime().update(handle, event.getProcessInstance());
        } else {
            handle = event.getKieRuntime().insert(event.getProcessInstance());
            store.put(((KogitoProcessInstance) event.getProcessInstance()).getStringId(), handle);
        }
    }

    protected FactHandle getProcessInstanceFactHandle(final String processInstanceId, KieRuntime kruntime) {

        if (store.containsKey(processInstanceId)) {
            return store.get(processInstanceId);
        }

        //else try to search for it in the working memory
        Collection<FactHandle> factHandles = kruntime.getFactHandles(
                object -> WorkflowProcessInstance.class.isAssignableFrom(object.getClass())
                        && (((KogitoWorkflowProcessInstance) object).getStringId().equals(processInstanceId)));

        if (factHandles != null && !factHandles.isEmpty()) {
            FactHandle handle = factHandles.iterator().next();
            // put it into store for faster access
            store.put(processInstanceId, handle);
            return handle;
        }
        return null;
    }
}
