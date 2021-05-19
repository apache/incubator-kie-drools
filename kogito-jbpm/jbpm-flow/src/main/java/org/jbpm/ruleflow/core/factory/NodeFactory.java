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

import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;

public abstract class NodeFactory<T extends NodeFactory<T, P>, P extends RuleFlowNodeContainerFactory<P, ?>> {

    public static final String METHOD_NAME = "name";
    public static final String METHOD_METADATA = "metaData";
    public static final String METHOD_DONE = "done";

    protected Object node;
    protected NodeContainer nodeContainer;
    protected P nodeContainerFactory;

    protected NodeFactory(P nodeContainerFactory, NodeContainer nodeContainer, Object node, Object id) {
        this.nodeContainerFactory = nodeContainerFactory;
        this.nodeContainer = nodeContainer;
        this.node = node;
        setId(node, id);
        if (node instanceof Node) {
            nodeContainer.addNode((Node) node);
        }
    }

    protected void setId(Object node, Object id) {
        ((Node) node).setId((long) id);
    }

    public Node getNode() {
        return (Node) node;
    }

    public T name(String name) {
        getNode().setName(name);
        return (T) this;
    }

    public T metaData(String name, Object value) {
        getNode().setMetaData(name, value);
        return (T) this;
    }

    public P done() {
        return this.nodeContainerFactory;
    }
}
