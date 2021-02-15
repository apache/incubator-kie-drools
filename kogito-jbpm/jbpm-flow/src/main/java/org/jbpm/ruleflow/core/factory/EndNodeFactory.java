/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.jbpm.workflow.core.node.EndNode;

import java.util.ArrayList;
import java.util.List;

import static org.jbpm.ruleflow.core.Metadata.ACTION;

public class EndNodeFactory extends ExtendedNodeFactory {

    public static final String METHOD_TERMINATE = "terminate";
    public static final String METHOD_ACTION = "action";

    public EndNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new EndNode();
    }

    protected EndNode getEndNode() {
        return (EndNode) getNode();
    }

    @Override
    public EndNodeFactory name(String name) {
        super.name(name);
        return this;
    }

    public EndNodeFactory terminate(boolean terminate) {
        getEndNode().setTerminate(terminate);
        return this;
    }

    public EndNodeFactory action(Action action) {
        DroolsAction droolsAction = new DroolsAction();
        droolsAction.setMetaData(ACTION, action);
        List<DroolsAction> enterActions = getEndNode().getActions(ExtendedNodeImpl.EVENT_NODE_ENTER);
        if (enterActions == null) {
            enterActions = new ArrayList<>();
            getEndNode().setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, enterActions);
        }
        enterActions.add(droolsAction);
        return this;
    }
}
