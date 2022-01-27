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

import org.drools.core.event.ProcessEvent;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

public class KogitoProcessNodeLeftEventImpl extends ProcessEvent implements ProcessNodeLeftEvent {

    private static final long serialVersionUID = 510l;

    private NodeInstance nodeInstance;

    public KogitoProcessNodeLeftEventImpl(final NodeInstance nodeInstance, KieRuntime kruntime) {
        super(nodeInstance.getProcessInstance(), kruntime);
        this.nodeInstance = nodeInstance;
    }

    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    public String toString() {
        return "==>[ProcessNodeLeft(nodeId=" + nodeInstance.getNodeId() + "; id=" + ((KogitoNodeInstance) nodeInstance).getStringId()
                + "; nodeName=" + getNodeInstance().getNodeName() + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }
}
