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
import org.jbpm.ruleflow.core.factory.RuleSetNodeFactory;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.kie.api.definition.process.Node;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.UnknownType;

public class RuleSetNodeVisitor extends AbstractVisitor {

    @Override
    public void visitNode(Node node, BlockStmt body, VariableScope variableScope, ProcessMetaData metadata) {
        RuleSetNode ruleSetNode = (RuleSetNode) node;
        
        addFactoryMethodWithArgsWithAssignment(body, RuleSetNodeFactory.class, "ruleSetNode" + node.getId(), "ruleSetNode", new LongLiteralExpr(ruleSetNode.getId()));
        addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "name", new StringLiteralExpr(getOrDefault(ruleSetNode.getName(), "Rule")));
        // build supplier for either KieRuntime or DMNRuntime
        BlockStmt actionBody = new BlockStmt();
        LambdaExpr lambda = new LambdaExpr(new Parameter(new UnknownType(), "()"), actionBody);
        
        
        if (ruleSetNode.getLanguage().equals(RuleSetNode.DRL_LANG)) {
            MethodCallExpr ruleRuntimeBuilder = new MethodCallExpr(new NameExpr("app"), "ruleRuntimeBuilder");
            MethodCallExpr ruleRuntimeSupplier = new MethodCallExpr(ruleRuntimeBuilder, "newKieSession", NodeList.nodeList(new StringLiteralExpr("defaultStatelessKieSession")));
            actionBody.addStatement(new ReturnStmt(ruleRuntimeSupplier));
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "ruleFlowGroup", new StringLiteralExpr(ruleSetNode.getRuleFlowGroup()), lambda);
            
        } else if (ruleSetNode.getLanguage().equals(RuleSetNode.DMN_LANG)) {
            MethodCallExpr ruleRuntimeSupplier = new MethodCallExpr(new NameExpr("app"), "dmnRuntimeBuilder");
            actionBody.addStatement(new ReturnStmt(ruleRuntimeSupplier));
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "dmnGroup", new StringLiteralExpr(ruleSetNode.getNamespace()), 
                                     new StringLiteralExpr(ruleSetNode.getModel()), 
                                     ruleSetNode.getDecision() == null ? new NullLiteralExpr() : new StringLiteralExpr(ruleSetNode.getDecision()), 
                                     lambda);
        } else {
            throw new IllegalArgumentException("Unsupported rule language use " + ruleSetNode.getLanguage());
        }
        
        for (Entry<String, String> entry : ruleSetNode.getInMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "inMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        for (Entry<String, String> entry : ruleSetNode.getOutMappings().entrySet()) {
            addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "outMapping", new StringLiteralExpr(entry.getKey()), new StringLiteralExpr(entry.getValue()));
        }
        
        addFactoryMethodWithArgs(body, "ruleSetNode" + node.getId(), "done");
        
    }
}
