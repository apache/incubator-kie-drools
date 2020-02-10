/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event;

import java.util.Collections;
import java.util.List;

import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.ProcessInstance;

public class ProcessVariableChangedEventImpl extends ProcessEvent implements ProcessVariableChangedEvent {

    private static final long serialVersionUID = 510l;
    
    private String id;
    private String instanceId;
    private Object oldValue;
    private Object newValue;
    private List<String> tags;

    public ProcessVariableChangedEventImpl(final String id, final String instanceId,
            final Object oldValue, final Object newValue, List<String> tags,
            final ProcessInstance processInstance, KieRuntime kruntime ) {
        super( processInstance, kruntime );
        this.id = id;
        this.instanceId = instanceId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.tags = tags == null ? Collections.emptyList() : tags;
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

    public String toString() {
        return "==>[ProcessVariableChanged(id=" + id + "; instanceId=" + instanceId + "; oldValue=" + oldValue + "; newValue=" + newValue
            + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }
}
