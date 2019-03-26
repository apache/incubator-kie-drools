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
package org.jbpm.workflow.instance.node;

import java.util.Collection;

import org.jbpm.workflow.core.node.BoundaryEventNode;
import org.jbpm.workflow.instance.NodeInstanceContainer;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.kie.api.runtime.process.NodeInstance;

public class BoundaryEventNodeInstance extends EventNodeInstance {

    private static final long serialVersionUID = -4958054074031174180L;

    @Override
    public void signalEvent(String type, Object event) {
        BoundaryEventNode boundaryNode = (BoundaryEventNode) getEventNode();
        
        String attachedTo = boundaryNode.getAttachedToNodeId();
        Collection<NodeInstance> nodeInstances = ((NodeInstanceContainer) getNodeInstanceContainer()).getNodeInstances();
        if( type != null && type.startsWith("Compensation") ) { 
            // if not active && completed, signal
            if( ! isAttachedToNodeActive(nodeInstances, attachedTo, type, event) && isAttachedToNodeCompleted(attachedTo)) {
                super.signalEvent(type, event);
            } 
            else {
                cancel();
            }
        } else { 
            if (isAttachedToNodeActive(nodeInstances, attachedTo, type, event)) {
                super.signalEvent(type, event);
            } else {
                cancel();
            }
        }
    }

    private boolean isAttachedToNodeActive(Collection<NodeInstance> nodeInstances, String attachedTo, String type, Object event) {
        if (nodeInstances != null && !nodeInstances.isEmpty()) {
            for (NodeInstance nInstance : nodeInstances) {
                String nodeUniqueId = (String) nInstance.getNode().getMetaData().get("UniqueId");
                boolean isActivating = ((WorkflowProcessInstanceImpl)nInstance.getProcessInstance()).getActivatingNodeIds().contains(nodeUniqueId);
                if (attachedTo.equals(nodeUniqueId) && !isActivating) {
                    // in case this is timer event make sure it corresponds to the proper node instance
                    if (type.startsWith("Timer-")) {
                        if (Long.valueOf(nInstance.getId()).equals(event)) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
                if (nInstance instanceof CompositeNodeInstance) {
                    boolean hasActive = isAttachedToNodeActive(((CompositeNodeInstance) nInstance).getNodeInstances(), attachedTo, type, event);
                    if (hasActive) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean isAttachedToNodeCompleted(String attachedTo) {
        WorkflowProcessInstanceImpl processInstance = (WorkflowProcessInstanceImpl) getProcessInstance();
        return processInstance.getCompletedNodeIds().contains(attachedTo);
    }

    @Override
    public void cancel() {
        getProcessInstance().removeEventListener(getEventType(), getEventListener(), true);
        ((NodeInstanceContainer) getNodeInstanceContainer()).removeNodeInstance(this);
    }
}
