/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.process.instance.event;

import org.kie.api.event.process.ProcessNodeStateChangeEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;

public class KogitoProcessNodeStateChangeEventImpl extends AbstractProcessNodeEvent implements ProcessNodeStateChangeEvent {

    private static final long serialVersionUID = 510l;

    public KogitoProcessNodeStateChangeEventImpl(NodeInstance nodeInstance, KieRuntime kruntime, String identity) {
        super(nodeInstance, nodeInstance.getProcessInstance(), kruntime, identity);
    }

    @Override
    public String toString() {
        return "==>[ProcessNodeStateChangeEvent(nodeId=" + getNodeInstance().getNodeId() + "; id=" + ((KogitoNodeInstance) getNodeInstance()).getStringId()
                + "; nodeName=" + getNodeInstance().getNodeName() + "; processName=" + getProcessInstance().getProcessName() + "; processId=" + getProcessInstance().getProcessId() + ")]";
    }
}
