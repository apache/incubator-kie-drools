/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.process.instance.ProcessInstanceManager;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;

public class DefaultProcessInstanceManager implements ProcessInstanceManager {

    private Map<String, KogitoProcessInstance> processInstances = new ConcurrentHashMap<>();

    public void addProcessInstance(KogitoProcessInstance processInstance) {
        String id = UUID.randomUUID().toString();
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId(id);
        internalAddProcessInstance(processInstance);
    }

    public void internalAddProcessInstance(KogitoProcessInstance processInstance) {
        processInstances.put(processInstance.getStringId(), processInstance);
    }

    public Collection<KogitoProcessInstance> getProcessInstances() {
        return Collections.unmodifiableCollection(processInstances.values());
    }

    public KogitoProcessInstance getProcessInstance(String id) {
        return processInstances.get(id);
    }

    public KogitoProcessInstance getProcessInstance(String id, boolean readOnly) {
        return processInstances.get(id);
    }

    public void removeProcessInstance(KogitoProcessInstance processInstance) {
        internalRemoveProcessInstance(processInstance);
    }

    public void internalRemoveProcessInstance(KogitoProcessInstance processInstance) {
        processInstances.remove(processInstance.getStringId());
    }

    public void clearProcessInstances() {
        processInstances.clear();
    }

    public void clearProcessInstancesState() {

    }
}
