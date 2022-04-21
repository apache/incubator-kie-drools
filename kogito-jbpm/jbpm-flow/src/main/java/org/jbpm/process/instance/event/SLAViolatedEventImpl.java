/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.event.process.SLAViolatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class SLAViolatedEventImpl extends ProcessEvent implements SLAViolatedEvent {

    private static final long serialVersionUID = 510l;
    private NodeInstance nodeInstance;

    public SLAViolatedEventImpl(final ProcessInstance instance, KieRuntime kruntime) {
        super(instance, kruntime);
    }

    public SLAViolatedEventImpl(final ProcessInstance instance, NodeInstance nodeInstance, KieRuntime kruntime) {
        super(instance, kruntime);
        this.nodeInstance = nodeInstance;
    }

    public String toString() {
        return "==>[SLAViolatedEvent(name=" + getProcessInstance().getProcessName() + "; id=" + getProcessInstance().getProcessId() + ")]";
    }

    @Override
    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

}
