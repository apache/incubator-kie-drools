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
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.node.Split;

/**
 *
 */
public class SplitFactory extends NodeFactory {

    public SplitFactory(RuleFlowNodeContainerFactory nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, id);
    }

    protected Node createNode() {
        return new Split();
    }
    
    protected Split getSplit() {
    	return (Split) getNode();
    }

    public SplitFactory name(String name) {
        getNode().setName(name);
        return this;
    }

    public SplitFactory type(int type) {
    	getSplit().setType(type);
        return this;
    }
    
    public SplitFactory constraint(long toNodeId, String name, String type, String dialect, String constraint) {
    	return constraint(toNodeId, name, type, dialect, constraint, 0);
    }
    
    public SplitFactory constraint(long toNodeId, String name, String type, String dialect, String constraint, int priority) {
        ConstraintImpl constraintImpl = new ConstraintImpl();
        constraintImpl.setName(name);
        constraintImpl.setType(type); 
        constraintImpl.setDialect(dialect);
        constraintImpl.setConstraint(constraint);
        constraintImpl.setPriority(priority);
        getSplit().addConstraint(
    		new ConnectionRef(toNodeId, Node.CONNECTION_DEFAULT_TYPE), constraintImpl);
        return this;
    }


}
