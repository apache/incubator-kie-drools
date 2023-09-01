/**
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
package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;

import static com.github.javaparser.ast.NodeList.nodeList;

/**
 * A ForEachStatement downcasting the iterable variable
 */
public class ForEachDowncastStmtT implements TypedExpression {

    private VariableDeclarationExpr variableDeclarationExpr;
    private String iterable;
    private TypedExpression child;

    public ForEachDowncastStmtT(VariableDeclarationExpr variableDeclarationExpr, String iterable, TypedExpression child) {
        this.variableDeclarationExpr = variableDeclarationExpr;
        this.iterable = iterable;
        this.child = child;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        ForEachStmt newForEachStmt = new ForEachStmt();

        BlockStmt body = new BlockStmt();

        NodeList<VariableDeclarator> variables = nodeList();

        for (VariableDeclarator v : variableDeclarationExpr.getVariables()) {
            VariableDeclarator newVariable = v.clone();

            String newIteratorVariable = "_" + v.getNameAsString();

            VariableDeclarationExpr castAssign = new VariableDeclarationExpr(
                    new VariableDeclarator(v.getType(), v.getName(),
                                           new CastExpr(v.getType(), new NameExpr(newIteratorVariable))));

            body.addStatement(0, castAssign);

            newVariable.setType(Object.class);
            newVariable.setName(newIteratorVariable);

            variables.add(newVariable);
        }

        body.addStatement((BlockStmt) child.toJavaExpression());
        newForEachStmt.setBody(body);

        VariableDeclarationExpr newVariables = new VariableDeclarationExpr(variables);
        newForEachStmt.setVariable(newVariables);

        return new ForEachStmt(newVariables, new NameExpr(iterable), body);
    }

    @Override
    public String toString() {
        return "ForEachDowncastStmtT{" +
                "variableDeclarationExpr=" + variableDeclarationExpr +
                ", child=" + child +
                '}';
    }
}
