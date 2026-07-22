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

import org.kie.api.event.process.SignalEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class SignalEventImpl extends AbstractProcessNodeEvent implements SignalEvent {

    private static final long serialVersionUID = 1L;
    private final String signalName;
    private final Object signalObject;

    public SignalEventImpl(ProcessInstance instance, KieRuntime kruntime, NodeInstance nodeInstance,
            String signalName, Object signalObject, String identity) {
        super(nodeInstance, instance, kruntime, identity);
        this.signalName = signalName;
        this.signalObject = signalObject;
    }

    @Override
    public String getSignalName() {
        return signalName;
    }

    @Override
    public Object getSignal() {
        return signalObject;
    }

    @Override
    public String toString() {
        return "SignalEventImpl [nodeInstance=" + getNodeInstance() + ", signalName=" + signalName + ", signalObject=" +
                signalObject + "]";
    }
}
