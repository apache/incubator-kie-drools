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

import org.jbpm.process.core.Work;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.WorkItemNodeFactory;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

public class WorkItemNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        WorkItemNode workItemNode = (WorkItemNode) node;
        Work work = workItemNode.getWork();
        
        addFactoryMethodWithArgsWithAssignment(body, WorkItemNodeFactory.class, "workItemNode" + node.getId(), "workItemNode", new LongLiteralExpr(workItemNode.getId()));
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(workItemNode.getName(), work.getName())));
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "workName", new StringLiteralExpr(work.getName()));

        addWorkItemParameters(work, body, "workItemNode" + node.getId());
        addWorkItemMappings(workItemNode, body, "workItemNode" + node.getId());
        
        addFactoryMethodWithArgs(body, "workItemNode" + node.getId(), "done");
        
        metadata.getWorkItems().add(work.getName());
    }
}
