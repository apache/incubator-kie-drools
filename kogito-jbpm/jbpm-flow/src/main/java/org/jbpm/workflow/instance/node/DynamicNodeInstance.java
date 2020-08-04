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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.InternalAgenda;
import org.drools.core.spi.KogitoProcessContext;
import org.jbpm.workflow.core.node.DynamicNode;
import org.kie.api.definition.process.Node;
import org.kie.api.event.process.ContextAwareEventListener;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;

import static org.jbpm.ruleflow.core.Metadata.IS_FOR_COMPENSATION;
import static org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE;
import static org.jbpm.workflow.core.impl.ExtendedNodeImpl.EVENT_NODE_ENTER;

public class DynamicNodeInstance extends CompositeContextNodeInstance {

    private static final long serialVersionUID = 510l;

    private String getRuleFlowGroupName() {
        return getNodeName();
    }

    protected DynamicNode getDynamicNode() {
        return (DynamicNode) getNode();
    }

    @Override
    public String getNodeName() {
        return resolveVariable(super.getNodeName());
    }

    @Override
    public void internalTrigger(NodeInstance from, String type) {
        triggerTime = new Date();
        triggerEvent(EVENT_NODE_ENTER);

        // if node instance was cancelled, abort
        if (getNodeInstanceContainer().getNodeInstance(getId()) == null) {
            return;
        }
        if (canActivate()) {
            triggerActivated();
        } else {
            setState(ProcessInstance.STATE_PENDING);
            addActivationListener();
        }
    }

    private void triggerActivated() {
        setState(ProcessInstance.STATE_ACTIVE);
        // activate ad hoc fragments if they are marked as such
        List<Node> autoStartNodes = getDynamicNode().getAutoStartNodes();
        autoStartNodes.forEach(autoStartNode -> triggerSelectedNode(autoStartNode, null));
    }

    private boolean canActivate() {
        KogitoProcessContext context = new KogitoProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        return getDynamicNode().canActivate(context);
    }

    private boolean canComplete() {
        KogitoProcessContext context = new KogitoProcessContext(getProcessInstance().getKnowledgeRuntime());
        context.setNodeInstance(this);
        return getNodeInstances(false).isEmpty() && getDynamicNode().canComplete(context);
    }

    private void addActivationListener() {
        getProcessInstance().getKnowledgeRuntime().getProcessRuntime().addEventListener(ContextAwareEventListener.using(listener -> {
            if (canActivate() && getState() == ProcessInstance.STATE_PENDING) {
                triggerActivated();
                getProcessInstance().getKnowledgeRuntime().getProcessRuntime().removeEventListener(listener);
            }
        }));
    }

    private void addCompletionListener() {
        getProcessInstance().getKnowledgeRuntime()
                .getProcessRuntime()
                .addEventListener(ContextAwareEventListener.using(listener -> {
                    if (canComplete()) {
                        triggerCompleted(CONNECTION_DEFAULT_TYPE);
                    }
                }));
    }

    @Override
    public void nodeInstanceCompleted(org.jbpm.workflow.instance.NodeInstance nodeInstance, String outType) {
        Node nodeInstanceNode = nodeInstance.getNode();
        if (nodeInstanceNode != null) {
            Object compensationBoolObj = nodeInstanceNode.getMetaData().get(IS_FOR_COMPENSATION);
            if (Boolean.TRUE.equals(compensationBoolObj)) {
                return;
            }
        }
        // TODO what if we reach the end of one branch but others might still need to be created ?
        // TODO are we sure there will always be node instances left if we are not done yet?
        if (isTerminated(nodeInstance) || canComplete()) {
            triggerCompleted(CONNECTION_DEFAULT_TYPE);
        }
        if (!canComplete()) {
            addCompletionListener();
        }
    }

    @Override
    public void triggerCompleted(String outType) {
        if (getProcessInstance().getKnowledgeRuntime().getAgenda() != null) {
            ((InternalAgenda) getProcessInstance().getKnowledgeRuntime().getAgenda())
                    .deactivateRuleFlowGroup(getRuleFlowGroupName());
        }
        super.triggerCompleted(outType);
    }

    protected boolean isTerminated(NodeInstance from) {
        if (from instanceof EndNodeInstance) {
            return ((EndNodeInstance) from).getEndNode().isTerminate();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected void triggerSelectedNode(Node node, Object event) {
        org.jbpm.workflow.instance.NodeInstance nodeInstance = getNodeInstance(node);
        if (event != null) {
            Map<String, Object> dynamicParams = new HashMap<>();
            if (event instanceof Map) {
                dynamicParams.putAll((Map<String, Object>) event);
            } else {
                dynamicParams.put("Data", event);
            }
            nodeInstance.setDynamicParameters(dynamicParams);
        }
        nodeInstance.trigger(null, CONNECTION_DEFAULT_TYPE);
    }
}
