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

import org.jbpm.compiler.canonical.descriptors.ExpressionUtils;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.workflow.core.node.SubProcessNode;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;

import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_INDEPENDENT;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_PROCESS_ID;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_PROCESS_NAME;
import static org.jbpm.ruleflow.core.factory.SubProcessNodeFactory.METHOD_WAIT_FOR_COMPLETION;

public class LambdaSubProcessNodeVisitor extends AbstractNodeVisitor<SubProcessNode> {

    public LambdaSubProcessNodeVisitor(ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    protected String getNodeKey() {
        return "subProcessNode";
    }

    @Override
    public void visitNode(String factoryField, SubProcessNode node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        String name = node.getName();
        String subProcessId = node.getProcessId();

        NodeValidator.of(getNodeKey(), name)
                .notEmpty("subProcessId", subProcessId)
                .validate();

        body.addStatement(getAssignedFactoryMethod(factoryField, SubProcessNodeFactory.class, getNodeId(node), getNodeKey(), getWorkflowElementConstructor(node.getId())))
                .addStatement(getNameMethod(node, "Call Activity"))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_PROCESS_ID, ExpressionUtils.getLiteralExpr(subProcessId)))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_PROCESS_NAME, ExpressionUtils.getLiteralExpr(getOrDefault(node.getProcessName(), ""))))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_WAIT_FOR_COMPLETION, new BooleanLiteralExpr(node.isWaitForCompletion())))
                .addStatement(getFactoryMethod(getNodeId(node), METHOD_INDEPENDENT, new BooleanLiteralExpr(node.isIndependent())));

        addNodeMappings(node, body, getNodeId(node));
        visitMetaData(node.getMetaData(), body, getNodeId(node));
        body.addStatement(getDoneMethod(getNodeId(node)));

        if (subProcessId != null && !subProcessId.contains("#{")) {
            String processId = ProcessToExecModelGenerator.extractProcessId(subProcessId);
            metadata.addSubProcess(processId, subProcessId);
        }
    }
}
