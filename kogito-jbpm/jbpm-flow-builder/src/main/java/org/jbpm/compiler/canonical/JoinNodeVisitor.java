/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.compiler.canonical;

import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.workflow.core.node.Join;
import org.kie.api.definition.process.Node;

import static org.jbpm.ruleflow.core.factory.JoinFactory.METHOD_TYPE;

public class JoinNodeVisitor extends AbstractNodeVisitor {

    private static final String NODE_KEY = "joinNode";

    @Override
    protected String getNodeKey() {
        return NODE_KEY;
    }

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        Join joinNode = (Join) node;
        body.addStatement(getAssignedFactoryMethod(factoryField, JoinFactory.class, getNodeId(node), NODE_KEY, new LongLiteralExpr(joinNode.getId())));
        body.addStatement(getNameMethod(node, "Join"));
        body.addStatement(getFactoryMethod(getNodeId(node), METHOD_TYPE, new IntegerLiteralExpr(joinNode.getType())));

        visitMetaData(joinNode.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));
    }
}
