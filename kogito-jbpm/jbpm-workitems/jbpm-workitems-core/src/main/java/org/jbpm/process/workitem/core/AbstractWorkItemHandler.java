/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.core;

import java.util.Collection;

import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public abstract class AbstractWorkItemHandler implements WorkItemHandler {

    private KieSession ksession;

    public AbstractWorkItemHandler(KieSession ksession) {
        if (ksession == null) {
            throw new IllegalArgumentException("ksession cannot be null");
        }
        this.ksession = ksession;
    }

    public KieSession getSession() {
        return ksession;
    }

    public long getProcessInstanceId(WorkItem workItem) {
        return workItem.getProcessInstanceId();
    }

    public ProcessInstance getProcessInstance(WorkItem workItem) {
        ProcessInstance processInstance = ksession.getProcessInstance(getProcessInstanceId(workItem));
        return processInstance;
    }

    public NodeInstance getNodeInstance(WorkItem workItem) {
        ProcessInstance processInstance = getProcessInstance(workItem);
        if (!(processInstance instanceof WorkflowProcessInstance)) {
            return null;
        }
        return findWorkItemNodeInstance(workItem.getId(),
                                        ((WorkflowProcessInstance) processInstance).getNodeInstances());
    }

    private WorkItemNodeInstance findWorkItemNodeInstance(long workItemId,
                                                          Collection<NodeInstance> nodeInstances) {
        for (NodeInstance nodeInstance : nodeInstances) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) nodeInstance;
                WorkItem workItem = workItemNodeInstance.getWorkItem();
                if (workItem != null && workItemId == workItem.getId()) {
                    return workItemNodeInstance;
                }
            }
            if (nodeInstance instanceof NodeInstanceContainer) {
                WorkItemNodeInstance result = findWorkItemNodeInstance(workItemId,
                                                                       ((NodeInstanceContainer) nodeInstance).getNodeInstances());
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}
