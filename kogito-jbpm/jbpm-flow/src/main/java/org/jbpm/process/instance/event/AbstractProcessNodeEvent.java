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

import org.kie.api.event.process.ProcessNodeEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public abstract class AbstractProcessNodeEvent extends ProcessEvent implements ProcessNodeEvent {

    private final NodeInstance nodeInstance;

    protected AbstractProcessNodeEvent(NodeInstance nodeInstance, ProcessInstance instance, KieRuntime kruntime) {
        super(instance, kruntime);
        this.nodeInstance = nodeInstance;
    }

    protected AbstractProcessNodeEvent(NodeInstance nodeInstance, ProcessInstance instance, KieRuntime kruntime, String identity) {
        super(instance, kruntime, identity);
        this.nodeInstance = nodeInstance;
    }

    @Override
    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

}
