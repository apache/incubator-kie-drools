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

package org.jbpm.ruleflow.core.factory;

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;

/**
 *
 */
public abstract class NodeFactory {

    private Node node;
    private NodeContainer nodeContainer;
    protected RuleFlowNodeContainerFactory nodeContainerFactory;
    
    protected NodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        this.nodeContainerFactory = nodeContainerFactory;
        this.nodeContainer = nodeContainer;
        this.node = createNode();
        this.node.setId(id);
    }

    protected abstract Node createNode();

    public RuleFlowNodeContainerFactory done() {
        nodeContainer.addNode(node);
        return this.nodeContainerFactory;
    }

    protected Node getNode() {
        return node;
    }
}


    