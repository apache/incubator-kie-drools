/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event;

import java.util.List;

import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class KogitoProcessVariableChangedEventImpl extends ProcessVariableChangedEventImpl implements ProcessVariableChangedEvent {

    private NodeInstance nodeInstance;

    public KogitoProcessVariableChangedEventImpl( final String id, final String instanceId,
                                                  final Object oldValue, final Object newValue, List<String> tags,
                                                  final ProcessInstance processInstance, NodeInstance nodeInstance, KieRuntime kruntime ) {
        super( id, instanceId, oldValue, newValue, tags, processInstance, kruntime );
        this.nodeInstance = nodeInstance;
    }

    @Override
    public NodeInstance getNodeInstance() {
        return this.nodeInstance;
    }

    public String toString() {
        return "==>[ProcessVariableChanged(id=" + getVariableId() + "; instanceId=" + getVariableInstanceId() + "; oldValue=" + getOldValue() + "; newValue=" + getNewValue()
                + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }
}