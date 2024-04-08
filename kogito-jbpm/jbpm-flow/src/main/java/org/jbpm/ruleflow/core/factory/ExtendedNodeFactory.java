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
package org.jbpm.ruleflow.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.ExtendedNodeImpl;
import org.kie.api.definition.process.WorkflowElementIdentifier;

public abstract class ExtendedNodeFactory<T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> extends NodeFactory<T, P> {

    protected ExtendedNodeFactory(P nodeContainerFactory, NodeContainer nodeContainer, Node node, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, node, id);
    }

    protected ExtendedNodeImpl getExtendedNode() {
        return (ExtendedNodeImpl) getNode();
    }

    public T onEntryAction(String dialect, String action) {
        if (getExtendedNode().getActions(dialect) != null) {
            getExtendedNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getExtendedNode().setActions(ExtendedNodeImpl.EVENT_NODE_ENTER, actions);
        }
        return (T) this;
    }

    public T onExitAction(String dialect, String action) {
        if (getExtendedNode().getActions(dialect) != null) {
            getExtendedNode().getActions(dialect).add(new DroolsConsequenceAction(dialect, action));
        } else {
            List<DroolsAction> actions = new ArrayList<>();
            actions.add(new DroolsConsequenceAction(dialect, action));
            getExtendedNode().setActions(ExtendedNodeImpl.EVENT_NODE_EXIT, actions);
        }
        return (T) this;
    }
}
