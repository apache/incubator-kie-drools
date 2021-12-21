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
package org.jbpm.process.instance.impl.actions;

import java.io.Serializable;
import java.util.Map;

import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.Metadata;
import org.jbpm.workflow.core.impl.NodeIoHelper;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.process.workitems.InternalKogitoWorkItemManager;
import org.kie.kogito.process.workitems.impl.KogitoWorkItemImpl;

public class HandleMessageAction implements Action, Serializable {

    private static final long serialVersionUID = 1L;

    private final String messageType;
    private String variableName;

    public HandleMessageAction(String messageType, String variableName) {
        this.messageType = messageType;
        this.variableName = variableName;
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        KogitoWorkItemImpl workItem = new KogitoWorkItemImpl();
        workItem.setName("Send Task");
        workItem.setNodeInstanceId((context.getNodeInstance()).getStringId());
        workItem.setProcessInstanceId((context.getProcessInstance()).getStringId());
        workItem.setNodeId(context.getNodeInstance().getNodeId());
        workItem.setParameter(Metadata.MESSAGE_TYPE, messageType);

        // compute inputs for message action
        NodeInstanceImpl impl = ((NodeInstanceImpl) context.getNodeInstance());
        Map<String, Object> inputSet = NodeIoHelper.processInputs(impl, varRef -> impl.getVariable(varRef));
        workItem.getParameters().put(variableName, inputSet.get(variableName));

        ((InternalKogitoWorkItemManager) context.getKogitoProcessRuntime().getKogitoWorkItemManager()).internalExecuteWorkItem(workItem);
    }

}
