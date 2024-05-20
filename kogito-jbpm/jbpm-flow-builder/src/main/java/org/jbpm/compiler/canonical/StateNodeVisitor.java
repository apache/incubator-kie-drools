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
package org.jbpm.compiler.canonical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.jbpm.compiler.canonical.node.NodeVisitorBuilderService;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.StateNodeFactory;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.StateNode;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.utils.StringEscapeUtils;

import static org.jbpm.ruleflow.core.factory.StateNodeFactory.METHOD_CONSTRAINT;

public class StateNodeVisitor extends CompositeContextNodeVisitor<StateNode> {

    public StateNodeVisitor(NodeVisitorBuilderService nodeVisitorService) {
        super(nodeVisitorService);
    }

    @Override
    protected Class<?> factoryClass() {
        return StateNodeFactory.class;
    }

    @Override
    protected String getNodeKey() {
        return "stateNode";
    }

    @Override
    protected String getDefaultName() {
        return "State";
    }

    @Override
    public Stream<MethodCallExpr> visitCustomFields(StateNode node, VariableScope variableScope) {
        if (node.getConstraints() == null) {
            return Stream.empty();
        }

        Collection<MethodCallExpr> result = new ArrayList<>();
        for (Map.Entry<ConnectionRef, Collection<Constraint>> entry : node.getConstraints().entrySet()) {
            ConnectionRef ref = entry.getKey();
            for (Constraint constraint : entry.getValue()) {
                if (constraint != null) {
                    result.add(getFactoryMethod(getNodeId(node), METHOD_CONSTRAINT,
                            getOrNullExpr(ref.getConnectionId()),
                            getWorkflowElementConstructor(ref.getNodeId()),
                            new StringLiteralExpr(ref.getToType()),
                            new StringLiteralExpr(constraint.getDialect()),
                            new StringLiteralExpr(StringEscapeUtils.escapeJava(constraint.getConstraint())),
                            new IntegerLiteralExpr(constraint.getPriority())));
                }
            }
        }
        return result.stream();
    }
}
