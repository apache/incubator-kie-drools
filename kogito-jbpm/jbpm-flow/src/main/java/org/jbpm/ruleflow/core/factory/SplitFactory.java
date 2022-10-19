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

import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.process.instance.impl.ReturnValueEvaluator;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;

public class SplitFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends NodeFactory<SplitFactory<T>, T> {

    public static final String METHOD_TYPE = "type";
    public static final String METHOD_CONSTRAINT = "constraint";

    public SplitFactory(T nodeContainerFactory, NodeContainer nodeContainer, long id) {
        super(nodeContainerFactory, nodeContainer, new Split(), id);
    }

    public Split getSplit() {
        return (Split) getNode();
    }

    public SplitFactory<T> type(int type) {
        getSplit().setType(type);
        return this;
    }

    public SplitFactory<T> constraint(long toNodeId, String name, String type, String dialect, String constraint) {
        return constraint(toNodeId, name, type, dialect, constraint, 0);
    }

    public SplitFactory<T> constraint(long toNodeId, String name, String type, String dialect, String constraint, int priority) {
        return constraint(toNodeId, name, type, dialect, constraint, priority, false);
    }

    public SplitFactory<T> constraint(long toNodeId, String name, String type, String dialect, String constraint, int priority, boolean isDefault) {
        return constraintBuilder(toNodeId, name, type, dialect, constraint).withDefault(isDefault).priority(priority).done();
    }

    public ConstraintFactory<SplitFactory<T>> constraintBuilder(long toNodeId, String name, String type, String dialect, String constraint) {
        return new ConstraintFactory<>(this, toNodeId, name, type, dialect, constraint);
    }

    public SplitFactory<T> constraint(long toNodeId, String name, String type, String dialect, ReturnValueEvaluator evaluator, int priority) {
        return constraint(toNodeId, name, type, dialect, evaluator, priority, false);
    }

    public SplitFactory<T> constraint(long toNodeId, String name, String type, String dialect, ReturnValueEvaluator evaluator, int priority, boolean isDefault) {
        ReturnValueConstraintEvaluator constraintImpl = new ReturnValueConstraintEvaluator();
        constraintImpl.setName(name);
        constraintImpl.setType(type);
        constraintImpl.setDialect(dialect);
        constraintImpl.setPriority(priority);
        constraintImpl.setEvaluator(evaluator);
        constraintImpl.setConstraint("expression already given as evaluator");
        constraintImpl.setDefault(isDefault);
        getSplit().addConstraint(
                new ConnectionRef(name, toNodeId, Node.CONNECTION_DEFAULT_TYPE), constraintImpl);
        return this;
    }
}
