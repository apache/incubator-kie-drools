/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;
import org.kie.kogito.rules.DataObserver;
import org.kie.kogito.rules.DataStore;
import org.kie.kogito.rules.DataStream;
import org.kie.kogito.rules.SingletonStore;
import org.kie.kogito.rules.units.AssignableChecker;

import static com.github.javaparser.StaticJavaParser.parseExpression;

public class RuleUnitMetaModel {

    private final RuleUnitDescription ruleUnitDescription;
    private final String modelClassName;

    private final String instanceVarName;
    private final AssignableChecker assignableChecker;

    public RuleUnitMetaModel(RuleUnitDescription ruleUnitDescription, String instanceVarName, AssignableChecker assignableChecker ) {
        this.ruleUnitDescription = ruleUnitDescription;
        this.modelClassName = ruleUnitDescription.getCanonicalName();
        this.instanceVarName = instanceVarName;
        this.assignableChecker = assignableChecker;
    }

    public String instanceVarName() {
        return instanceVarName;
    }

    public AssignExpr newInstance() {
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, modelClassName);
        return new AssignExpr(
                new VariableDeclarationExpr(type, instanceVarName),
                new ObjectCreationExpr().setType(type),
                AssignExpr.Operator.ASSIGN);
    }

    public NodeList<Statement> hoistVars() {
        NodeList<Statement> statements = new NodeList<>();
        for (RuleUnitVariable v : ruleUnitDescription.getUnitVarDeclarations()) {
            statements.add(new ExpressionStmt(assignVar(v)));
        }
        return statements;
    }

    public MethodCallExpr get(String unitVar) {
        RuleUnitVariable v = ruleUnitDescription.getVar(unitVar);
        return get(v);
    }

    private MethodCallExpr get(RuleUnitVariable v) {
        String getter = v.getter();
        return new MethodCallExpr(new NameExpr(instanceVarName), getter);
    }

    public MethodCallExpr set(String unitVar, Expression sourceExpr) {
        return set(ruleUnitDescription.getVar(unitVar), sourceExpr);
    }

    private MethodCallExpr set(RuleUnitVariable targetUnitVar, Expression sourceExpr) {
        String setter = targetUnitVar.setter();
        return new MethodCallExpr(new NameExpr(instanceVarName), setter)
                .addArgument(sourceExpr);
    }

    public AssignExpr assignVar(RuleUnitVariable v) {
        ClassOrInterfaceType type = new ClassOrInterfaceType(null, v.getType().getCanonicalName());
        return new AssignExpr(
                new VariableDeclarationExpr(type, localVarName(v)),
                get(v),
                AssignExpr.Operator.ASSIGN);
    }

    private String localVarName(RuleUnitVariable v) {
        return String.format("%s_%s", instanceVarName, v.getName());
    }

    public Statement injectCollection(
            String targetUnitVar, String sourceProcVar) {
        BlockStmt blockStmt = new BlockStmt();
        RuleUnitVariable v = ruleUnitDescription.getVar(targetUnitVar);
        String appendMethod = appendMethodOf(v.getType());
        blockStmt.addStatement(assignVar(v));
        blockStmt.addStatement(
                iterate(new VariableDeclarator()
                                .setType("Object").setName("it"),
                        new NameExpr(sourceProcVar))
                        .setBody(new ExpressionStmt(
                                new MethodCallExpr()
                                        .setScope(new NameExpr(localVarName(v)))
                                        .setName(appendMethod)
                                        .addArgument(new NameExpr("it")))));
        return blockStmt;
    }

    private ForEachStmt iterate(VariableDeclarator iterVar, Expression sourceExpression) {
        return new ForEachStmt()
                .setVariable(new VariableDeclarationExpr(iterVar))
                .setIterable(sourceExpression);
    }

    public Statement injectScalar(String targetUnitVar, Expression sourceExpression) {
        BlockStmt blockStmt = new BlockStmt();
        RuleUnitVariable v = ruleUnitDescription.getVar(targetUnitVar);
        String appendMethod = appendMethodOf(v.getType());
        blockStmt.addStatement(assignVar(v));
        blockStmt.addStatement(
                new MethodCallExpr()
                        .setScope(new NameExpr(localVarName(v)))
                        .setName(appendMethod)
                        .addArgument(sourceExpression));
        return blockStmt;
    }

    private String appendMethodOf(Class<?> type) {
        String appendMethod;
        if ( assignableChecker.isAssignableFrom(DataStream.class, type)) {
            appendMethod = "append";
        } else if ( assignableChecker.isAssignableFrom(DataStore.class, type)) {
            appendMethod = "add";
        } else if ( assignableChecker.isAssignableFrom(SingletonStore.class, type)) {
            appendMethod = "set";
        } else {
            throw new IllegalArgumentException("Unknown data source type " + type.getCanonicalName());
        }
        return appendMethod;
    }

    public Statement extractIntoCollection(String sourceUnitVar, String targetProcessVar) {
        BlockStmt blockStmt = new BlockStmt();
        RuleUnitVariable v = ruleUnitDescription.getVar(sourceUnitVar);
        String localVarName = localVarName(v);
        blockStmt.addStatement(assignVar(v))
                .addStatement(new ExpressionStmt(
                        new MethodCallExpr(new NameExpr(localVarName), "subscribe")
                                .addArgument(new MethodCallExpr(
                                        new NameExpr(DataObserver.class.getCanonicalName()), "of")
                                                     .addArgument(parseExpression(targetProcessVar + "::add")))));
        return blockStmt;
    }

    public Statement extractIntoScalar(String sourceUnitVar, String targetProcessVar) {
        BlockStmt blockStmt = new BlockStmt();
        RuleUnitVariable v = ruleUnitDescription.getVar(sourceUnitVar);
        String localVarName = localVarName(v);
        blockStmt.addStatement(assignVar(v))
                .addStatement(new ExpressionStmt(
                        new MethodCallExpr(new NameExpr(localVarName), "subscribe")
                                .addArgument(new MethodCallExpr(
                                        new NameExpr(DataObserver.class.getCanonicalName()), "ofUpdatable")
                                                     .addArgument(parseExpression("o -> kcontext.setVariable(\"" + targetProcessVar + "\", o)")))));

        return blockStmt;
    }
}
