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
package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.CTypeNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ContextTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.FunctionTypeNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.ListTypeNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.StringEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ASLIST_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ATLITERALNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BETWEENNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BIG_DECIMAL_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BOOLEANNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BOOLEAN_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTENTRYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DASHNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DETERMINEOPERATOR_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FILTEREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FOREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FORMALPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONINVOCATIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.HASHMAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.IFEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INFIXOPERATOR_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INFIXOPNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INSTANCEOFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ITERATIONCONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LISTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.MAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEDPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEREFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NULLNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NUMBERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PATHEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PUT_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.QUALIFIEDNAMENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.QUANTIFIEDEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.RANGENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.SIGNEDUNARYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.STRINGNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.UNARYTESTLISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.UNARYTESTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.VALUEOF_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.VAR_S;
import static org.kie.dmn.feel.codegen.feel11.Constants.BUILTINTYPE_E;

public class ASTCompilerVisitor implements Visitor<BlockStmt> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASTCompilerVisitor.class);
    private final BlockStmt toPopulate;
    private final AtomicInteger variableCounter;
    private AtomicReference<String> lastVariableName = new AtomicReference<>();

    public ASTCompilerVisitor() {
        toPopulate = new BlockStmt();
        variableCounter = new AtomicInteger(0);
    }

    @Override
    public BlockStmt visit(ASTNode n) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BlockStmt visit(AtLiteralNode n) {
        n.getStringLiteral().accept(this);
        Expression stringLiteralExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(ATLITERALNODE_CT, NodeList.nodeList(stringLiteralExpression), n.getText());
    }

    @Override
    public BlockStmt visit(BetweenNode n) {
        n.getValue().accept(this);
        Expression valueExpression = new NameExpr(lastVariableName.get());
        n.getStart().accept(this);
        Expression startExpression = new NameExpr(lastVariableName.get());
        n.getEnd().accept(this);
        Expression endExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(BETWEENNODE_CT, NodeList.nodeList(valueExpression, startExpression, endExpression), n.getText());
    }

    @Override
    public BlockStmt visit(BooleanNode n) {
        Expression valueExpression = new BooleanLiteralExpr(n.getValue());
        return addNodeInstantiationStatement(BOOLEANNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    @Override
    public BlockStmt visit(ContextEntryNode n) {
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        n.getValue().accept(this);
        Expression valueExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(CONTEXTENTRYNODE_CT, NodeList.nodeList(nameExpression, valueExpression), n.getText());
    }

    @Override
    public BlockStmt visit(ContextNode n) {
        List entryNodesExpressions = n.getEntries().stream().map(entryNode -> {
                    entryNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        return addNodeInstantiationStatement(CONTEXTNODE_CT, NodeList.nodeList(getListExpression(entryNodesExpressions)), n.getText());
    }

    @Override
    public BlockStmt visit(ContextTypeNode n) {
        Map<Expression, Expression> genExpressions = new HashMap<>(); // THe key is the StringLiteralExpr of the original key; the value is the NameExpr pointing at generated variable
        for (Map.Entry<String, TypeNode> kv : n.getGen().entrySet()) {
            kv.getValue().accept(this);
            genExpressions.put(new StringLiteralExpr(kv.getKey()), new NameExpr(lastVariableName.get()));
        }
        // Creating the map
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(MAP_CT, mapVariableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, HASHMAP_CT, NodeList.nodeList());
        mapVariableDeclarator.setInitializer(objectCreationExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr = new VariableDeclarationExpr(mapVariableDeclarator);
        addNodeInstantiationStatement(mapVariableDeclarationExpr, mapVariableName);
        // Populating the map
        Expression mapVariableExpression = new NameExpr(mapVariableName);
        genExpressions.forEach((key, value) -> {
            MethodCallExpr putExpression = new MethodCallExpr(mapVariableExpression, PUT_S);
            putExpression.addArgument(key);
            putExpression.addArgument(value);
            addNodeInstantiationStatement(putExpression);
        });
        return addNodeInstantiationStatement(CONTEXTTYPENODE_CT, NodeList.nodeList(mapVariableExpression), n.getText());
    }

    @Override
    public BlockStmt visit(CTypeNode n) {
        if (!(n.getType() instanceof BuiltInType)) {
            throw new UnsupportedOperationException();
        }
        BuiltInType feelCType = (BuiltInType) n.getType();
        Expression typeExpression = new FieldAccessExpr(BUILTINTYPE_E, feelCType.name());
        return addNodeInstantiationStatement(CTYPENODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(DashNode n) {
        return addNodeInstantiationStatement(DASHNODE_CT, NodeList.nodeList(), n.getText());
    }

    @Override
    public BlockStmt visit(ForExpressionNode n) {
        List iterationContextsExpressions = n.getIterationContexts().stream().map(elementNode -> {
                    elementNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(FOREXPRESSIONNODE_CT, NodeList.nodeList(getListExpression(iterationContextsExpressions),
                                                                                   expressionExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FilterExpressionNode n) {
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        n.getFilter().accept(this);
        Expression filterExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(FILTEREXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                     filterExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FormalParameterNode n) {
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        n.getType().accept(this);
        Expression typeExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(FORMALPARAMETERNODE_CT, NodeList.nodeList(nameExpression, typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FunctionDefNode n) {
        List formalParametersExpressions = n.getFormalParameters().stream().map(elementNode -> {
                    elementNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        n.getBody().accept(this);
        Expression bodyExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(FUNCTIONDEFNODE_CT, NodeList.nodeList(getListExpression(formalParametersExpressions),
                                                                                   new BooleanLiteralExpr(n.isExternal()),
                                                                                   bodyExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FunctionInvocationNode n) {
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        n.getParams().accept(this);
        Expression paramsExpression = new NameExpr(lastVariableName.get());
        Expression tcFoldedExpression;
        if (n.getTcFolded() != null) {
            n.getTcFolded().accept(this);
            tcFoldedExpression = new NameExpr(lastVariableName.get());
        } else {
            tcFoldedExpression = new NullLiteralExpr();
        }
        return addNodeInstantiationStatement(FUNCTIONINVOCATIONNODE_CT, NodeList.nodeList(nameExpression,
                                                                                          paramsExpression, tcFoldedExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FunctionTypeNode n) {
        List argTypesExpressions = n.getArgTypes().stream().map(argTypeNode -> {
                    argTypeNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        n.getRetType().accept(this);
        Expression retTypeExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(FUNCTIONTYPENODE_CT, NodeList.nodeList(getListExpression(argTypesExpressions),
                                                                                    retTypeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(IfExpressionNode n) {
        n.getCondition().accept(this);
        Expression conditionExpression = new NameExpr(lastVariableName.get());
        n.getThenExpression().accept(this);
        Expression thenExpression = new NameExpr(lastVariableName.get());
        n.getElseExpression().accept(this);
        Expression elseExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(IFEXPRESSIONNODE_CT, NodeList.nodeList(conditionExpression, thenExpression, elseExpression), n.getText());
    }

    @Override
    public BlockStmt visit(InfixOpNode n) {
        Expression determineOperatorExpression = new MethodCallExpr(INFIXOPERATOR_N, DETERMINEOPERATOR_S,
                                                              new NodeList<>(new StringLiteralExpr(n.getOperator().getSymbol())));
        n.getLeft().accept(this);
        Expression leftExpression = new NameExpr(lastVariableName.get());
        n.getRight().accept(this);
        Expression rightExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(INFIXOPNODE_CT, NodeList.nodeList(determineOperatorExpression, leftExpression, rightExpression), n.getText());
    }

    @Override
    public BlockStmt visit(InNode n) {
        n.getValue().accept(this);
        Expression valueExpression = new NameExpr(lastVariableName.get());
        n.getExprs().accept(this);
        Expression exprsExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(INNODE_CT, NodeList.nodeList(valueExpression, exprsExpression), n.getText());
    }

    @Override
    public BlockStmt visit(InstanceOfNode n) {
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        n.getType().accept(this);
        Expression typeExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(INSTANCEOFNODE_CT, NodeList.nodeList(expressionExpression, typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(IterationContextNode n) {
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        n.getRangeEndExpr().accept(this);
        Expression rangeEndExprExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(ITERATIONCONTEXTNODE_CT, NodeList.nodeList(nameExpression, expressionExpression, rangeEndExprExpression), n.getText());
    }

    @Override
    public BlockStmt visit(ListNode n) {
        List elements = n.getElements().stream().map(elementNode -> {
                    elementNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        return addNodeInstantiationStatement(LISTNODE_CT, NodeList.nodeList(getListExpression(elements)), n.getText());
    }

    @Override
    public BlockStmt visit(ListTypeNode n) {
        n.getGenTypeNode().accept(this);
        Expression genTypeNodeExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(LISTTYPENODE_CT, NodeList.nodeList(genTypeNodeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NameDefNode n) {
        List partsExpressions = n.getParts().stream().map(StringLiteralExpr::new)
                .toList();
        Expression nameExpression = n.getName() != null ? new StringLiteralExpr(n.getName()) : new NullLiteralExpr();
        return addNodeInstantiationStatement(NAMEDEFNODE_CT, NodeList.nodeList(getListExpression(partsExpressions),
                                                                               nameExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NamedParameterNode n) {
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(NAMEDPARAMETERNODE_CT, NodeList.nodeList(nameExpression, expressionExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NameRefNode n) {
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addNodeInstantiationStatement(NAMEREFNODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NullNode n) {
        return addNodeInstantiationStatement(NULLNODE_CT, NodeList.nodeList(), n.getText());
    }

    @Override
    public BlockStmt visit(NumberNode n) {
        MethodCallExpr valueExpression = new MethodCallExpr(BIG_DECIMAL_N, VALUEOF_S);
        valueExpression.addArgument(n.getValue().toEngineeringString());
        return addNodeInstantiationStatement(NUMBERNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    @Override
    public BlockStmt visit(PathExpressionNode n) {
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        n.getName().accept(this);
        Expression nameExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(PATHEXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                      nameExpression), n.getText());
    }

    @Override
    public BlockStmt visit(QualifiedNameNode n) {
        List partsExpressions = n.getParts().stream().map(partNode -> {
                    partNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addNodeInstantiationStatement(QUALIFIEDNAMENODE_CT, NodeList.nodeList(getListExpression(partsExpressions),
                                                                                     typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(QuantifiedExpressionNode n) {
        Expression quantifierExpression = getEnumExpression(n.getQuantifier());
        List iterationContextsExpressions = n.getIterationContexts().stream().map(iterationContextNode -> {
                    iterationContextNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(QUANTIFIEDEXPRESSIONNODE_CT, NodeList.nodeList(quantifierExpression, getListExpression(iterationContextsExpressions),
                                                                                            expressionExpression), n.getText());
    }

    @Override
    public BlockStmt visit(RangeNode n) {
        Expression lowerBoundExpression = getEnumExpression(n.getLowerBound());
        Expression upperBoundExpression = getEnumExpression(n.getUpperBound());
        n.getStart().accept(this);
        Expression startExpression = new NameExpr(lastVariableName.get());
        n.getEnd().accept(this);
        Expression endExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(RANGENODE_CT, NodeList.nodeList(lowerBoundExpression, upperBoundExpression,
                                                                             startExpression, endExpression), n.getText());
    }

    @Override
    public BlockStmt visit(SignedUnaryNode n) {
        Expression signExpression = getEnumExpression(n.getSign());
        n.getExpression().accept(this);
        Expression expressionExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(SIGNEDUNARYNODE_CT, NodeList.nodeList(signExpression, expressionExpression), n.getText());
    }

    @Override
    public BlockStmt visit(StringNode n) {
        return addNodeInstantiationStatement(STRINGNODE_CT, NodeList.nodeList(new StringLiteralExpr(n.getValue())), n.getText());
    }

    @Override
    public BlockStmt visit(UnaryTestListNode n) {
        List elementsExpressions = n.getElements().stream().map(elementNode -> {
                    elementNode.accept(this);
                    return new NameExpr(lastVariableName.get());
                })
                .toList();
        Expression stateExpression = getEnumExpression(n.getState());
        return addNodeInstantiationStatement(UNARYTESTLISTNODE_CT, NodeList.nodeList(getListExpression(elementsExpressions),
                                                                                            stateExpression), n.getText());
    }

    @Override
    public BlockStmt visit(UnaryTestNode n) {
        Expression opExpression = getEnumExpression(n.getOperator());
        n.getValue().accept(this);
        Expression valueExpression = new NameExpr(lastVariableName.get());
        return addNodeInstantiationStatement(UNARYTESTNODE_CT, NodeList.nodeList(opExpression, valueExpression), n.getText());
    }

    public String getLastVariableName() {
        return lastVariableName.get();
    }

    private String getNextVariableName() {
        return String.format("%s_%d", VAR_S, variableCounter.getAndIncrement());
    }

    private BlockStmt addNodeInstantiationStatement(ClassOrInterfaceType variableType, NodeList<Expression> arguments, String text) {
        Expression textExpression = text!= null ? new StringLiteralExpr(StringEvalHelper.escapeInnerDoubleQuotes(text)) : new NullLiteralExpr();
        arguments.add(textExpression);
        return addNodeInstantiationStatement(variableType, arguments);
    }

    private BlockStmt addNodeInstantiationStatement(ClassOrInterfaceType variableType, NodeList<Expression> arguments) {
        String variableName = getNextVariableName();
        final VariableDeclarationExpr toAdd = getNodeInstantiationVariableDeclarator(variableName, variableType,
                                                                                     arguments);
        return addNodeInstantiationStatement(toAdd, variableName);
    }

    private BlockStmt addNodeInstantiationStatement(Expression toAdd, String variableName) {
        lastVariableName.set(variableName);
        return addNodeInstantiationStatement(toAdd);
    }

    private BlockStmt addNodeInstantiationStatement(Expression toAdd) {
        toPopulate.addStatement(toAdd);
        LOGGER.debug(toPopulate.toString());
        return toPopulate;
    }


    private VariableDeclarationExpr getNodeInstantiationVariableDeclarator(String variableName,
                                                                           ClassOrInterfaceType variableType,
                                                                           NodeList<Expression> arguments) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, variableType, arguments);
        variableDeclarator.setInitializer(objectCreationExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    private Expression getTypeExpression(Type type) {
        return type instanceof Enum typeEnum ? getEnumExpression(typeEnum) : parseExpression(type.getClass().getCanonicalName());
    }

    private Expression getEnumExpression(Enum enumType) {
        Expression scopeExpression = parseExpression(enumType.getClass().getCanonicalName());
        return new FieldAccessExpr(scopeExpression, enumType.name());
    }

    // Move to a common package
    static Expression getListExpression(List<Expression> expressions) {
        ExpressionStmt asListExpression = new ExpressionStmt();
        MethodCallExpr arraysCallExpression = new MethodCallExpr();
        SimpleName arraysName = new SimpleName(Arrays.class.getName());
        arraysCallExpression.setScope(new NameExpr(arraysName));
        arraysCallExpression.setName(new SimpleName(ASLIST_S));
        asListExpression.setExpression(arraysCallExpression);
        NodeList<Expression> arguments = new NodeList<>();
        arguments.addAll(expressions);
        arraysCallExpression.setArguments(arguments);
        asListExpression.setExpression(arraysCallExpression);
        return asListExpression.getExpression();
    }


}
