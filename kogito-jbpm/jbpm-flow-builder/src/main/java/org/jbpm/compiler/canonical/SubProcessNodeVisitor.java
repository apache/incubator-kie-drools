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

import java.util.Map.Entry;

import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.node.SubProcessNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class SubProcessNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        SubProcessNode subProcessNode = (SubProcessNode) node;
        addFactoryMethodWithArgsWithAssignment(body, SubProcessNodeFactory.class, "subProcessNode" + node.getId(), "subProcessNode", new LongLiteralExpr(subProcessNode.getId()));
        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(subProcessNode.getName(), "Call Activity")));
        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "processId", new StringLiteralExpr(subProcessNode.getProcessId()));
        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "processName", new StringLiteralExpr(getOrDefault(subProcessNode.getProcessName(), "")));
        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "waitForCompletion", new BooleanLiteralExpr(subProcessNode.isWaitForCompletion()));
        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "independent", new BooleanLiteralExpr(subProcessNode.isIndependent()));

        for (Entry<String, String> entry : subProcessNode.getInMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "inMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        for (Entry<String, String> entry : subProcessNode.getOutMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "outMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        
        visitMetaData(subProcessNode.getMetaData(), body, "subProcessNode" + node.getId());

        addFactoryMethodWithArgs(body, "subProcessNode" + node.getId(), "done");
    }
}
