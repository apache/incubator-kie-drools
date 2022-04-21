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

import org.kie.api.event.process.MessageEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

public class MessageEventImpl extends ProcessEvent implements MessageEvent {

    private static final long serialVersionUID = 1L;
    private NodeInstance nodeInstance;
    private String messageName;
    private Object messageObject;

    public MessageEventImpl(ProcessInstance instance, KieRuntime kruntime, NodeInstance nodeInstance,
            String messageName, Object messageObject) {
        super(instance, kruntime);
        this.nodeInstance = nodeInstance;
        this.messageName = messageName;
        this.messageObject = messageObject;
    }

    @Override
    public NodeInstance getNodeInstance() {
        return nodeInstance;
    }

    @Override
    public String getMessageName() {
        return messageName;
    }

    @Override
    public Object getMessage() {
        return messageObject;
    }

    @Override
    public String toString() {
        return "MessageEventImpl [nodeInstance=" + nodeInstance + ", messageName=" + messageName + ", messageObject=" +
                messageObject + "]";
    }
}
