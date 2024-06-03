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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
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
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BaseNode;
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
import org.kie.dmn.feel.lang.ast.TemporalConstantNode;
import org.kie.dmn.feel.lang.ast.TypeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.Visitor;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.DateTimeEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.utils.StringEscapeUtils.escapeJava;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ADDFIELD_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ASLIST_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ATLITERALNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BETWEENNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.BOOLEANNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.COMPARABLEPERIOD_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.COMPARABLEPERIOD_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTENTRYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CONTEXTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.CTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DASHNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DETERMINEOPERATOR_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DURATION_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DURATION_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FEEL_TIME_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FILTEREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FOREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FORMALPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONINVOCATIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.FUNCTIONTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.GETBIGDECIMALORNULL_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.HASHMAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.IFEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ILLEGALSTATEEXCEPTION_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INFIXOPERATOR_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INFIXOPNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INSTANCEOFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INSTANCE_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ITERATIONCONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.JAVABACKEDTYPE_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LISTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.MAPBACKEDTYPE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.MAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEDPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NAMEREFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NULLNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NUMBEREVALHELPER_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.NUMBERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OFFSETTIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OFFSETTIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OF_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PARSE_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PATHEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PUT_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.QUALIFIEDNAMENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.QUANTIFIEDEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.RANGENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.SIGNEDUNARYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.STRINGNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.TEMPORALACCESSOR_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.TEMPORALCONSTANTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.TIMEFUNCTION_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.TYPE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.UNARYTESTLISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.UNARYTESTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.VAR_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONED_DATE_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONED_DATE_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONE_ID_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONE_OFFSET_N;
import static org.kie.dmn.feel.codegen.feel11.Constants.BUILTINTYPE_E;

public class ASTCompilerVisitor implements Visitor<BlockStmt> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASTCompilerVisitor.class);
    private final BlockStmt toPopulate;
    private final AtomicInteger variableCounter;
    private AtomicReference<String> lastVariableName = new AtomicReference<>();
    private static final String EXTENDED_FUNCTION_PACKAGE = "org.kie.dmn.feel.runtime.functions.extended";

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
        Expression stringLiteralExpression = getNodeExpression(n.getStringLiteral());
        return addVariableDeclaratorWithObjectCreation(ATLITERALNODE_CT, NodeList.nodeList(stringLiteralExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(BetweenNode n) {
        Expression valueExpression = getNodeExpression(n.getValue());
        Expression startExpression = getNodeExpression(n.getStart());
        Expression endExpression = getNodeExpression(n.getEnd());
        return addVariableDeclaratorWithObjectCreation(BETWEENNODE_CT, NodeList.nodeList(valueExpression,
                                                                                         startExpression,
                                                                                         endExpression), n.getText());
    }

    @Override
    public BlockStmt visit(BooleanNode n) {
        Expression valueExpression = new BooleanLiteralExpr(n.getValue());
        return addVariableDeclaratorWithObjectCreation(BOOLEANNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    @Override
    public BlockStmt visit(ContextEntryNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression valueExpression = getNodeExpression(n.getValue());
        return addVariableDeclaratorWithObjectCreation(CONTEXTENTRYNODE_CT, NodeList.nodeList(nameExpression,
                                                                                              valueExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(ContextNode n) {
        List entryNodesExpressions = n.getEntries().stream().map(entryNode -> getNodeExpression(entryNode))
                .toList();
        return addVariableDeclaratorWithObjectCreation(CONTEXTNODE_CT,
                                                       NodeList.nodeList(getListExpression(entryNodesExpressions)),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(ContextTypeNode n) {
        Map<Expression, Expression> genExpressions = new HashMap<>(); // THe key is the StringLiteralExpr of the original key; the value is the NameExpr pointing at generated variable
        for (Map.Entry<String, TypeNode> kv : n.getGen().entrySet()) {
            genExpressions.put(new StringLiteralExpr(kv.getKey()), getNodeExpression(kv.getValue()));
        }
        // Creating the map
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(MAP_CT, mapVariableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, HASHMAP_CT, NodeList.nodeList());
        mapVariableDeclarator.setInitializer(objectCreationExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr = new VariableDeclarationExpr(mapVariableDeclarator);
        addExpression(mapVariableDeclarationExpr, mapVariableName);
        // Populating the map
        Expression mapVariableExpression = new NameExpr(mapVariableName);
        genExpressions.forEach((key, value) -> {
            MethodCallExpr putExpression = new MethodCallExpr(mapVariableExpression, PUT_S);
            putExpression.addArgument(key);
            putExpression.addArgument(value);
            addExpression(putExpression);
        });
        return addVariableDeclaratorWithObjectCreation(CONTEXTTYPENODE_CT, NodeList.nodeList(mapVariableExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(CTypeNode n) {
        if (!(n.getType() instanceof BuiltInType)) {
            throw new UnsupportedOperationException();
        }
        BuiltInType feelCType = (BuiltInType) n.getType();
        Expression typeExpression = new FieldAccessExpr(BUILTINTYPE_E, feelCType.name());
        return addVariableDeclaratorWithObjectCreation(CTYPENODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(DashNode n) {
        return addVariableDeclaratorWithObjectCreation(DASHNODE_CT, NodeList.nodeList(), n.getText());
    }

    @Override
    public BlockStmt visit(ForExpressionNode n) {
        List iterationContextsExpressions =
                n.getIterationContexts().stream().map(elementNode -> getNodeExpression(elementNode))
                .toList();
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(FOREXPRESSIONNODE_CT,
                                                       NodeList.nodeList(getListExpression(iterationContextsExpressions),
                                                                         expressionExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(FilterExpressionNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression filterExpression = getNodeExpression(n.getFilter());
        return addVariableDeclaratorWithObjectCreation(FILTEREXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                                  filterExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(FormalParameterNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression typeExpression = getNodeExpression(n.getType());
        return addVariableDeclaratorWithObjectCreation(FORMALPARAMETERNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                 typeExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(FunctionDefNode n) {
        List formalParametersExpressions = n.getFormalParameters().stream()
                .map(elementNode -> getNodeExpression(elementNode))
                .toList();
        Expression bodyExpression = getNodeExpression(n.getBody());
        return addVariableDeclaratorWithObjectCreation(FUNCTIONDEFNODE_CT,
                                                       NodeList.nodeList(getListExpression(formalParametersExpressions),
                                                                         new BooleanLiteralExpr(n.isExternal()),
                                                                         bodyExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(FunctionInvocationNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression paramsExpression = getNodeExpression(n.getParams());
        Expression tcFoldedExpression = getNodeExpression(n.getTcFolded());
        return addVariableDeclaratorWithObjectCreation(FUNCTIONINVOCATIONNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                    paramsExpression,
                                                                                                    tcFoldedExpression), n.getText());
    }

    @Override
    public BlockStmt visit(FunctionTypeNode n) {
        List argTypesExpressions = n.getArgTypes().stream().map(argTypeNode -> getNodeExpression(argTypeNode))
                .toList();
        Expression retTypeExpression = getNodeExpression(n.getRetType());
        return addVariableDeclaratorWithObjectCreation(FUNCTIONTYPENODE_CT,
                                                       NodeList.nodeList(getListExpression(argTypesExpressions),
                                                                         retTypeExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(IfExpressionNode n) {
        Expression conditionExpression = getNodeExpression(n.getCondition());
        Expression thenExpression = getNodeExpression(n.getThenExpression());
        Expression elseExpression = getNodeExpression(n.getElseExpression());
        return addVariableDeclaratorWithObjectCreation(IFEXPRESSIONNODE_CT, NodeList.nodeList(conditionExpression,
                                                                                              thenExpression,
                                                                                              elseExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(InfixOpNode n) {
        Expression determineOperatorExpression = new MethodCallExpr(INFIXOPERATOR_N, DETERMINEOPERATOR_S,
                                                              new NodeList<>(new StringLiteralExpr(n.getOperator().getSymbol())));
        Expression leftExpression = getNodeExpression(n.getLeft());
        Expression rightExpression = getNodeExpression(n.getRight());
        return addVariableDeclaratorWithObjectCreation(INFIXOPNODE_CT, NodeList.nodeList(determineOperatorExpression,
                                                                                         leftExpression,
                                                                                         rightExpression), n.getText());
    }

    @Override
    public BlockStmt visit(InNode n) {
        Expression valueExpression = getNodeExpression(n.getValue());
        Expression exprsExpression = getNodeExpression(n.getExprs());
        return addVariableDeclaratorWithObjectCreation(INNODE_CT, NodeList.nodeList(valueExpression, exprsExpression)
                , n.getText());
    }

    @Override
    public BlockStmt visit(InstanceOfNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression typeExpression = getNodeExpression(n.getType());
        return addVariableDeclaratorWithObjectCreation(INSTANCEOFNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                            typeExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(IterationContextNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression rangeEndExprExpression = getNodeExpression(n.getRangeEndExpr());
        return addVariableDeclaratorWithObjectCreation(ITERATIONCONTEXTNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                  expressionExpression, rangeEndExprExpression), n.getText());
    }

    @Override
    public BlockStmt visit(ListNode n) {
        List elements = n.getElements().stream().map(elementNode -> getNodeExpression(elementNode))
                .toList();
        return addVariableDeclaratorWithObjectCreation(LISTNODE_CT, NodeList.nodeList(getListExpression(elements)),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(ListTypeNode n) {
        Expression genTypeNodeExpression = getNodeExpression(n.getGenTypeNode());
        return addVariableDeclaratorWithObjectCreation(LISTTYPENODE_CT, NodeList.nodeList(genTypeNodeExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(NameDefNode n) {
        List partsExpressions = n.getParts().stream().map(StringLiteralExpr::new)
                .toList();
        Expression nameExpression = n.getName() != null ? new StringLiteralExpr(n.getName()) : new NullLiteralExpr();
        return addVariableDeclaratorWithObjectCreation(NAMEDEFNODE_CT,
                                                       NodeList.nodeList(getListExpression(partsExpressions),
                                                                         nameExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NamedParameterNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(NAMEDPARAMETERNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                expressionExpression)
                , n.getText());
    }

    @Override
    public BlockStmt visit(NameRefNode n) {
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addVariableDeclaratorWithObjectCreation(NAMEREFNODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    @Override
    public BlockStmt visit(NullNode n) {
        return addVariableDeclaratorWithObjectCreation(NULLNODE_CT, NodeList.nodeList(), n.getText());
    }

    @Override
    public BlockStmt visit(NumberNode n) {
        MethodCallExpr valueExpression = new MethodCallExpr(NUMBEREVALHELPER_N, GETBIGDECIMALORNULL_S,
                                                                 NodeList.nodeList(new StringLiteralExpr(n.getText())));
        return addVariableDeclaratorWithObjectCreation(NUMBERNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    @Override
    public BlockStmt visit(PathExpressionNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression nameExpression = getNodeExpression(n.getName());
        return addVariableDeclaratorWithObjectCreation(PATHEXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                                nameExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(QualifiedNameNode n) {
        List partsExpressions = n.getParts().stream().map(partNode -> getNodeExpression(partNode))
                .toList();
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addVariableDeclaratorWithObjectCreation(QUALIFIEDNAMENODE_CT,
                                                       NodeList.nodeList(getListExpression(partsExpressions),
                                                                         typeExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(QuantifiedExpressionNode n) {
        Expression quantifierExpression = getEnumExpression(n.getQuantifier());
        List iterationContextsExpressions =
                n.getIterationContexts().stream().map(iterationContextNode -> getNodeExpression(iterationContextNode))
                .toList();
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(QUANTIFIEDEXPRESSIONNODE_CT,
                                                       NodeList.nodeList(quantifierExpression,
                                                                         getListExpression(iterationContextsExpressions),
                                                                         expressionExpression), n.getText());
    }

    @Override
    public BlockStmt visit(RangeNode n) {
        Expression lowerBoundExpression = getEnumExpression(n.getLowerBound());
        Expression upperBoundExpression = getEnumExpression(n.getUpperBound());
        Expression startExpression = getNodeExpression(n.getStart());
        Expression endExpression = getNodeExpression(n.getEnd());
        return addVariableDeclaratorWithObjectCreation(RANGENODE_CT, NodeList.nodeList(lowerBoundExpression,
                                                                                       upperBoundExpression,
                                                                                       startExpression,
                                                                                       endExpression), n.getText());
    }

    @Override
    public BlockStmt visit(SignedUnaryNode n) {
        Expression signExpression = getEnumExpression(n.getSign());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(SIGNEDUNARYNODE_CT, NodeList.nodeList(signExpression,
                                                                                             expressionExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(StringNode n) {
        return addVariableDeclaratorWithObjectCreation(STRINGNODE_CT, NodeList.nodeList(),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(TemporalConstantNode n) {
        Expression valueExpression = getObjectExpression(n.getValue());
        FEELFunction fn = n.getFn();
        Expression fnVariableNameExpression;
        if (fn != null) {
            Class fnClass = fn.getClass();
            ClassOrInterfaceType fn_CT = parseClassOrInterfaceType(fnClass.getCanonicalName());
            Expression fn_N = new NameExpr(fnClass.getCanonicalName());
            if (fnClass.getPackageName().equals(EXTENDED_FUNCTION_PACKAGE)) {
                addVariableDeclaratorWithWithFieldAccess(fn_CT, INSTANCE_S, fn_N);
            } else {
                addVariableDeclaratorWithObjectCreation(fn_CT,
                                                        NodeList.nodeList());
            }
            fnVariableNameExpression = new NameExpr(lastVariableName.get());
        } else {
            fnVariableNameExpression = new NullLiteralExpr();
        }
        List paramsExpressions = n.getParams().stream().map(param -> getObjectExpression(param)).toList();
        return addVariableDeclaratorWithObjectCreation(TEMPORALCONSTANTNODE_CT, NodeList.nodeList(valueExpression,
                                                                                                  fnVariableNameExpression, getListExpression(paramsExpressions)), n.getText());
    }

    @Override
    public BlockStmt visit(UnaryTestListNode n) {
        List elementsExpressions = n.getElements().stream().map(elementNode -> getNodeExpression(elementNode))
                .toList();
        Expression stateExpression = getEnumExpression(n.getState());
        return addVariableDeclaratorWithObjectCreation(UNARYTESTLISTNODE_CT,
                                                       NodeList.nodeList(getListExpression(elementsExpressions),
                                                                         stateExpression),
                                                       n.getText());
    }

    @Override
    public BlockStmt visit(UnaryTestNode n) {
        Expression opExpression = getEnumExpression(n.getOperator());
        Expression valueExpression = getNodeExpression(n.getValue());
        return addVariableDeclaratorWithObjectCreation(UNARYTESTNODE_CT, NodeList.nodeList(opExpression,
                                                                                           valueExpression),
                                                       n.getText());
    }

    public String getLastVariableName() {
        return lastVariableName.get();
    }

    public BlockStmt returnError(String errorMessage) {
        ObjectCreationExpr illegalStateExceptionExpression = new ObjectCreationExpr(null, ILLEGALSTATEEXCEPTION_CT, NodeList.nodeList(Expressions.stringLiteral(errorMessage)));
        ThrowStmt throwStmt = new ThrowStmt();
        throwStmt.setExpression(illegalStateExceptionExpression);
        return addStatement(throwStmt);
    }

    private String getNextVariableName() {
        return String.format("%s_%d", VAR_S, variableCounter.getAndIncrement());
    }

    private BlockStmt addVariableDeclaratorWithObjectCreation(ClassOrInterfaceType variableType,
                                                              NodeList<Expression> arguments, String text) {
        Expression textExpression = text != null ? new StringLiteralExpr(escapeJava(text)) : new NullLiteralExpr();
        arguments.add(textExpression);
        return addVariableDeclaratorWithObjectCreation(variableType, arguments);
    }

    private BlockStmt addVariableDeclaratorWithObjectCreation(ClassOrInterfaceType variableType,
                                                              NodeList<Expression> arguments) {
        String variableName = getNextVariableName();
        final VariableDeclarationExpr toAdd = getVariableDeclaratorWithObjectCreation(variableName, variableType,
                                                                                      arguments);
        return addExpression(toAdd, variableName);
    }

    private BlockStmt addVariableDeclaratorWithWithMethodCall(ClassOrInterfaceType variableType,
                                                              String name,
                                                              Expression scope,
                                                              NodeList<Expression> arguments) {
        String variableName = getNextVariableName();
        final VariableDeclarationExpr toAdd = getVariableDeclaratorWithMethodCall(variableName,
                                                                                  variableType,
                                                                                  name,
                                                                                  scope,
                                                                                  arguments);
        return addExpression(toAdd, variableName);
    }

    private BlockStmt addVariableDeclaratorWithWithFieldAccess(ClassOrInterfaceType variableType,
                                                               String name,
                                                               Expression scope) {

        String variableName = getNextVariableName();
        final VariableDeclarationExpr toAdd = getVariableDeclaratorWithFieldAccessExpr(variableName,
                                                                                       variableType,
                                                                                       name,
                                                                                       scope);
        return addExpression(toAdd, variableName);
    }

    private BlockStmt addExpression(Expression toAdd, String variableName) {
        lastVariableName.set(variableName);
        return addExpression(toAdd);
    }

    private BlockStmt addExpression(Expression toAdd) {
        toPopulate.addStatement(toAdd);
        LOGGER.debug(toPopulate.toString());
        return toPopulate;
    }

    private BlockStmt addStatement(Statement toAdd) {
        toPopulate.addStatement(toAdd);
        LOGGER.debug(toPopulate.toString());
        return toPopulate;
    }

    private VariableDeclarationExpr getVariableDeclaratorWithObjectCreation(String variableName,
                                                                            ClassOrInterfaceType variableType,
                                                                            NodeList<Expression> arguments) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, variableType, arguments);
        variableDeclarator.setInitializer(objectCreationExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    private VariableDeclarationExpr getVariableDeclaratorWithMethodCall(String variableName,
                                                                        ClassOrInterfaceType variableType,
                                                                        String name,
                                                                        Expression scope,
                                                                        NodeList<Expression> arguments) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final MethodCallExpr methodCallExpr = new MethodCallExpr(scope, name, arguments);
        variableDeclarator.setInitializer(methodCallExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    private VariableDeclarationExpr getVariableDeclaratorWithFieldAccessExpr(String variableName,
                                                                             ClassOrInterfaceType variableType,
                                                                             String name,
                                                                             Expression scope) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final FieldAccessExpr methodCallExpr = new FieldAccessExpr(scope, name);
        variableDeclarator.setInitializer(methodCallExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    private Expression getTypeExpression(Type type) {
        if (type instanceof Enum typeEnum) {
            return getEnumExpression(typeEnum);
        } else if (type instanceof JavaBackedType javaBackedType) {
            return getJavaBackedTypeExpression(javaBackedType);
        } else if (type instanceof MapBackedType mapBackedType) {
            return getMapBackedTypeExpression(mapBackedType);
        } else {
            // TODO gcardosi 1206 - fix
            return parseExpression(type.getClass().getCanonicalName());
        }
    }


    private Expression getJavaBackedTypeExpression(JavaBackedType javaBackedType) {
        // Creating the JavaBackedType
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(TYPE_CT, mapVariableName);
        Expression classExpression = new NameExpr(javaBackedType.getWrapped().getCanonicalName() + ".class");
        final MethodCallExpr methodCallExpr = new MethodCallExpr(JAVABACKEDTYPE_N, OF_S, NodeList.nodeList(classExpression));
        mapVariableDeclarator.setInitializer(methodCallExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr = new VariableDeclarationExpr(mapVariableDeclarator);
        addExpression(mapVariableDeclarationExpr, mapVariableName);
        return new NameExpr(mapVariableName);
    }
    
    private Expression getMapBackedTypeExpression(MapBackedType mapBackedType) {
        Map<Expression, Expression> fieldsExpressions = new HashMap<>(); // The key is the StringLiteralExpr of the original key; the value is the Expression pointing at type
        for (Map.Entry<String, Type> kv : mapBackedType.getFields().entrySet()) {
            fieldsExpressions.put(new StringLiteralExpr(kv.getKey()), getTypeExpression(kv.getValue()));
        }

        // Creating the MapBackedType
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(MAPBACKEDTYPE_CT, mapVariableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, MAPBACKEDTYPE_CT, NodeList.nodeList(new StringLiteralExpr(mapBackedType.getName())));
        mapVariableDeclarator.setInitializer(objectCreationExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr = new VariableDeclarationExpr(mapVariableDeclarator);
        addExpression(mapVariableDeclarationExpr, mapVariableName);
        // Populating the map
        Expression mapVariableExpression = new NameExpr(mapVariableName);
        fieldsExpressions.forEach((key, value) -> {
            MethodCallExpr putExpression = new MethodCallExpr(mapVariableExpression, ADDFIELD_S);
            putExpression.addArgument(key);
            putExpression.addArgument(value);
            addExpression(putExpression);
        });
        return new NameExpr(mapVariableName);
    }

    private Expression getEnumExpression(Enum enumType) {
        Expression scopeExpression = parseExpression(enumType.getClass().getCanonicalName());
        return new FieldAccessExpr(scopeExpression, enumType.name());
    }

    private Expression getNodeExpression(BaseNode node) {
        if (node != null) {
            node.accept(this);
            return new NameExpr(lastVariableName.get());
        } else {
            return new NullLiteralExpr();
        }
    }

    private Expression getObjectExpression(Object object) {
        if (object == null) {
            return new NullLiteralExpr();
        }
        if (object instanceof ComparablePeriod comparablePeriod) {
            String variableName = getNextVariableName();
            NodeList arguments = NodeList.nodeList(new StringLiteralExpr(comparablePeriod.asPeriod().toString()));

            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  COMPARABLEPERIOD_CT,
                                                                                                  PARSE_S,
                                                                                                  COMPARABLEPERIOD_N, arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof Duration duration) {
            String variableName = getNextVariableName();
            NodeList arguments = NodeList.nodeList(new StringLiteralExpr(duration.toString()));
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  DURATION_CT,
                                                                                                  PARSE_S,
                                                                                                  DURATION_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof LocalDate localDate) {
            String variableName = getNextVariableName();
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localDate.getYear()),
                                                   new IntegerLiteralExpr(localDate.getMonthValue()),
                                                   new IntegerLiteralExpr(localDate.getDayOfMonth()));

            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  LOCAL_DATE_CT,
                                                                                                  OF_S,
                                                                                                  LOCAL_DATE_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof LocalDateTime localDateTime) {
            String variableName = getNextVariableName();
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localDateTime.getYear()),
                                                   new IntegerLiteralExpr(localDateTime.getMonthValue()),
                                                   new IntegerLiteralExpr(localDateTime.getDayOfMonth()),
                                                   new IntegerLiteralExpr(localDateTime.getHour()),
                                                   new IntegerLiteralExpr(localDateTime.getMinute()),
                                                   new IntegerLiteralExpr(localDateTime.getSecond()));
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  LOCAL_DATE_TIME_CT,
                                                                                                  OF_S,
                                                                                                  LOCAL_DATE_TIME_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof LocalTime localTime) {
            String variableName = getNextVariableName();
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localTime.getHour()),
                                                   new IntegerLiteralExpr(localTime.getMinute()),
                                                   new IntegerLiteralExpr(localTime.getSecond()),
                                                   new IntegerLiteralExpr(localTime.getNano()));
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  LOCAL_TIME_CT,
                                                                                                  OF_S,
                                                                                                  LOCAL_TIME_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof Number number) {
            return new IntegerLiteralExpr(number.toString());
        } else if (object instanceof OffsetTime offsetTime) {
            String variableName = getNextVariableName();
            Expression zoneOffsetExpression = new MethodCallExpr(ZONE_OFFSET_N,
                                                                 OF_S,
                                                                 NodeList.nodeList(new StringLiteralExpr(offsetTime.getOffset().getId())));
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(offsetTime.getHour()),
                                                   new IntegerLiteralExpr(offsetTime.getMinute()),
                                                   new IntegerLiteralExpr(offsetTime.getSecond()),
                                                   new IntegerLiteralExpr(offsetTime.getNano()),
                                                   zoneOffsetExpression);
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  OFFSETTIME_CT,
                                                                                                  OF_S,
                                                                                                  OFFSETTIME_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof String string) {
            return new StringLiteralExpr(escapeJava(string));
        } else if (object instanceof ZonedDateTime zonedDateTime) {
            String variableName = getNextVariableName();
            Expression zoneIdExpression = new MethodCallExpr(ZONE_ID_N, OF_S,
                                                             NodeList.nodeList(new StringLiteralExpr(zonedDateTime.getZone().getId())));
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(zonedDateTime.getYear()),
                                                   new IntegerLiteralExpr(zonedDateTime.getMonthValue()),
                                                   new IntegerLiteralExpr(zonedDateTime.getDayOfMonth()),
                                                   new IntegerLiteralExpr(zonedDateTime.getHour()),
                                                   new IntegerLiteralExpr(zonedDateTime.getMinute()),
                                                   new IntegerLiteralExpr(zonedDateTime.getSecond()),
                                                   new IntegerLiteralExpr(zonedDateTime.getNano()),
                                                   zoneIdExpression);
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  ZONED_DATE_TIME_CT,
                                                                                                  OF_S,
                                                                                                  ZONED_DATE_TIME_N,
                                                                                                  arguments);
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else if (object instanceof TemporalAccessor temporalAccessor) {
            // FallBack in case of Parse or other unmanaged classes - keep at the end
            String variableName = getNextVariableName();
            String parsedString = DateTimeEvalHelper.toParsableString(temporalAccessor);
            Expression feelTimeExpression = new FieldAccessExpr(TIMEFUNCTION_N, FEEL_TIME_S);
            VariableDeclarationExpr variableDeclarationExpr = getVariableDeclaratorWithMethodCall(variableName,
                                                                                                  TEMPORALACCESSOR_CT,
                                                                                                  PARSE_S,
                                                                                                  feelTimeExpression,
                                                                                                  NodeList.nodeList(new StringLiteralExpr(parsedString)));
            addExpression(variableDeclarationExpr, variableName);
            return new NameExpr(variableName);
        } else {
            throw new UnsupportedOperationException("Unexpected Object: " + object + " " + object.getClass());
        }
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
