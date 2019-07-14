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

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.workflow.core.Constraint;
import org.kie.api.definition.process.Node;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.jbpm.workflow.core.node.Split;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;

public class SplitNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(String factoryField, Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        Split splitNode = (Split) node;
        addFactoryMethodWithArgsWithAssignment(factoryField, body, SplitFactory.class, "splitNode" + node.getId(), "splitNode", new LongLiteralExpr(splitNode.getId()));
        addFactoryMethodWithArgs(body, "splitNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(splitNode.getName(), "Split")));
        addFactoryMethodWithArgs(body, "splitNode" + node.getId(), "type", new IntegerLiteralExpr(splitNode.getType()));
        
        visitMetaData(splitNode.getMetaData(), body, "splitNode" + node.getId());            
        
        if (splitNode.getType() == Split.TYPE_OR || splitNode.getType() == Split.TYPE_XOR) {
            for (Entry<ConnectionRef, Constraint> entry : splitNode.getConstraints().entrySet()) {
                
                if (entry.getValue() != null) {
                
                    BlockStmt actionBody = new BlockStmt();
                    LambdaExpr lambda = new LambdaExpr(
                            new Parameter(new UnknownType(), "kcontext"), // (kcontext) ->
                            actionBody
                    );
    
                    for (Variable v : variableScope.getVariables()) {
                        actionBody.addStatement(makeAssignment(v));
                    }
                    BlockStmt constraintBody = new BlockStmt();
                    constraintBody.addStatement(entry.getValue().getConstraint());
                                  
                    actionBody.addStatement(constraintBody);
                    
                    addFactoryMethodWithArgs(body, "splitNode" + node.getId(), "constraint", new LongLiteralExpr(entry.getKey().getNodeId()),                
                                             new StringLiteralExpr(getOrDefault(entry.getKey().getConnectionId(), "")),
                                             new StringLiteralExpr(entry.getKey().getToType()),
                                             new StringLiteralExpr(entry.getValue().getDialect()),
                                             lambda,
                                             new IntegerLiteralExpr(entry.getValue().getPriority()));
                }
            }
        }
        addFactoryMethodWithArgs(body, "splitNode" + node.getId(), "done");
    }
}
