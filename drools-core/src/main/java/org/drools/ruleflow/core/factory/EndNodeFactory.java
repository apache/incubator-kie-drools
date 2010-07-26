/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ruleflow.core.factory;

import org.drools.ruleflow.core.RuleFlowNodeContainerFactory;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.node.EndNode;

/**
 *
 * @author salaboy
 */
public class EndNodeFactory extends NodeFactory {

    public EndNodeFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new EndNode();
    }

    protected EndNode getEndNode() {
        return (EndNode) getNode();
    }

    public EndNodeFactory name(String name) {
        getNode().setName(name);
        return this;
    }

    public EndNodeFactory terminate(boolean terminate) {
        getEndNode().setTerminate(terminate);
        return this;
    }
}
