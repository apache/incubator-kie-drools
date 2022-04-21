/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.event;

import java.util.Collections;
import java.util.List;

import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.kogito.internal.process.event.KogitoProcessVariableChangedEvent;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

public class KogitoProcessVariableChangedEventImpl extends ProcessEvent implements KogitoProcessVariableChangedEvent {

    private String id;
    private String instanceId;
    private Object oldValue;
    private Object newValue;
    private List<String> tags;
    private KogitoNodeInstance nodeInstance;

    public KogitoProcessVariableChangedEventImpl(final String id, final String instanceId,
            final Object oldValue, final Object newValue, List<String> tags,
            final ProcessInstance processInstance, KogitoNodeInstance nodeInstance, KieRuntime kruntime) {
        super(processInstance, kruntime);
        this.id = id;
        this.instanceId = instanceId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.tags = tags == null ? Collections.emptyList() : tags;
        this.nodeInstance = nodeInstance;
    }

    public String getVariableInstanceId() {
        return instanceId;
    }

    public String getVariableId() {
        return id;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public List<String> getTags() {
        return tags;
    }

    @Override
    public KogitoNodeInstance getNodeInstance() {
        return this.nodeInstance;
    }

    public String toString() {
        return "==>[ProcessVariableChanged(id=" + getVariableId() + "; instanceId=" + getVariableInstanceId() + "; oldValue=" + getOldValue() + "; newValue=" + getNewValue()
                + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }
}