/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.codegen.feel11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.Type;
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
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.AliasFEELType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.CodegenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.utils.StringEscapeUtils.escapeJava;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ADDFIELD_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.HASHMAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ILLEGALSTATEEXCEPTION_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INSTANCE_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.MAP_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OF_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PUT_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.VAR_S;
import static org.kie.dmn.feel.codegen.feel11.Constants.BUILTINTYPE_E;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.ALIASFEELTYPE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.ATLITERALNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.BETWEENNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.BOOLEANNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.CONTEXTENTRYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.CONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.CONTEXTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.CTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.DASHNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.DETERMINEOPERATOR_S;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FILTEREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FOREXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FORMALPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FUNCTIONDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FUNCTIONINVOCATIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FUNCTIONTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.GETBIGDECIMALORNULL_S;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.IFEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.INFIXOPERATOR_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.INFIXOPNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.INNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.INSTANCEOFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.ITERATIONCONTEXTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.JAVABACKEDTYPE_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.LISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.LISTTYPENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.MAPBACKEDTYPE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NAMEDEFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NAMEDPARAMETERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NAMEREFNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NULLNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NUMBEREVALHELPER_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.NUMBERNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.PATHEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.QUALIFIEDNAMENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.QUANTIFIEDEXPRESSIONNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.RANGENODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.SIGNEDUNARYNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.STRINGNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.TEMPORALCONSTANTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.TYPE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.UNARYTESTLISTNODE_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.UNARYTESTNODE_CT;
import static org.kie.dmn.feel.util.CodegenUtils.getEnumExpression;
import static org.kie.dmn.feel.util.CodegenUtils.getListExpression;
import static org.kie.dmn.feel.util.CodegenUtils.getStringLiteralExpr;
import static org.kie.dmn.feel.util.CodegenUtils.getVariableDeclaratorWithFieldAccessExpr;
import static org.kie.dmn.feel.util.CodegenUtils.getVariableDeclaratorWithObjectCreation;

public class ASTCompilerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ASTCompilerVisitor.class);
    private static final String EXTENDED_FUNCTION_PACKAGE = "org.kie.dmn.feel.runtime.functions.extended";
    private final ASTCompilerVisitor astCompilerVisitor;
    private final BlockStmt toPopulate;
    private final AtomicInteger variableCounter;
    private final Map<BaseNode, NameExpr> baseNodeCache;
    private final Map<Type, Expression> typeCache;
    private final Map<Object, NameExpr> objectCache;
    private AtomicReference<String> lastVariableName = new AtomicReference<>();

    public ASTCompilerHelper(ASTCompilerVisitor astCompilerVisitor) {
        this.astCompilerVisitor = astCompilerVisitor;
        toPopulate = new BlockStmt();
        variableCounter = new AtomicInteger(0);
        baseNodeCache = new ConcurrentHashMap<>();
        typeCache = new ConcurrentHashMap<>();
        objectCache = new ConcurrentHashMap<>();
    }

    public BlockStmt add(AtLiteralNode n) {
        Expression stringLiteralExpression = getNodeExpression(n.getStringLiteral());
        return addVariableDeclaratorWithObjectCreation(ATLITERALNODE_CT, NodeList.nodeList(stringLiteralExpression),
                                                       n.getText());
    }

    public BlockStmt add(BetweenNode n) {
        Expression valueExpression = getNodeExpression(n.getValue());
        Expression startExpression = getNodeExpression(n.getStart());
        Expression endExpression = getNodeExpression(n.getEnd());
        return addVariableDeclaratorWithObjectCreation(BETWEENNODE_CT, NodeList.nodeList(valueExpression,
                                                                                         startExpression,
                                                                                         endExpression), n.getText());
    }

    public BlockStmt add(BooleanNode n) {
        Expression valueExpression = new BooleanLiteralExpr(n.getValue());
        return addVariableDeclaratorWithObjectCreation(BOOLEANNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    public BlockStmt add(ContextEntryNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression valueExpression = getNodeExpression(n.getValue());
        return addVariableDeclaratorWithObjectCreation(CONTEXTENTRYNODE_CT, NodeList.nodeList(nameExpression,
                                                                                              valueExpression),
                                                       n.getText());
    }

    public BlockStmt add(ContextNode n) {
        List entryNodesExpressions = n.getEntries().stream().map(entryNode -> getNodeExpression(entryNode))
                .toList();
        return addVariableDeclaratorWithObjectCreation(CONTEXTNODE_CT,
                                                       NodeList.nodeList(getListExpression(entryNodesExpressions)),
                                                       n.getText());
    }

    public BlockStmt add(ContextTypeNode n) {
        Map<Expression, Expression> genExpressions = new HashMap<>(); // THe key is the StringLiteralExpr of the
        // original key; the value is the NameExpr pointing at generated variable
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

    public BlockStmt add(CTypeNode n) {
        if (!(n.getType() instanceof BuiltInType)) {
            throw new UnsupportedOperationException();
        }
        BuiltInType feelCType = (BuiltInType) n.getType();
        Expression typeExpression = new FieldAccessExpr(BUILTINTYPE_E, feelCType.name());
        return addVariableDeclaratorWithObjectCreation(CTYPENODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    public BlockStmt add(DashNode n) {
        return addVariableDeclaratorWithObjectCreation(DASHNODE_CT, NodeList.nodeList(), n.getText());
    }

    public BlockStmt add(ForExpressionNode n) {
        List iterationContextsExpressions =
                n.getIterationContexts().stream().map(elementNode -> getNodeExpression(elementNode))
                        .toList();
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(FOREXPRESSIONNODE_CT,
                                                       NodeList.nodeList(getListExpression(iterationContextsExpressions),
                                                                         expressionExpression),
                                                       n.getText());
    }

    public BlockStmt add(FilterExpressionNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression filterExpression = getNodeExpression(n.getFilter());
        return addVariableDeclaratorWithObjectCreation(FILTEREXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                                  filterExpression),
                                                       n.getText());
    }

    public BlockStmt add(FormalParameterNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression typeExpression = getNodeExpression(n.getType());
        return addVariableDeclaratorWithObjectCreation(FORMALPARAMETERNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                 typeExpression),
                                                       n.getText());
    }

    public BlockStmt add(FunctionDefNode n) {
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

    public BlockStmt add(FunctionInvocationNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression paramsExpression = getNodeExpression(n.getParams());
        Expression tcFoldedExpression = getNodeExpression(n.getTcFolded());
        return addVariableDeclaratorWithObjectCreation(FUNCTIONINVOCATIONNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                    paramsExpression,
                                                                                                    tcFoldedExpression), n.getText());
    }

    public BlockStmt add(FunctionTypeNode n) {
        List argTypesExpressions = n.getArgTypes().stream().map(argTypeNode -> getNodeExpression(argTypeNode))
                .toList();
        Expression retTypeExpression = getNodeExpression(n.getRetType());
        return addVariableDeclaratorWithObjectCreation(FUNCTIONTYPENODE_CT,
                                                       NodeList.nodeList(getListExpression(argTypesExpressions),
                                                                         retTypeExpression),
                                                       n.getText());
    }

    public BlockStmt add(IfExpressionNode n) {
        Expression conditionExpression = getNodeExpression(n.getCondition());
        Expression thenExpression = getNodeExpression(n.getThenExpression());
        Expression elseExpression = getNodeExpression(n.getElseExpression());
        return addVariableDeclaratorWithObjectCreation(IFEXPRESSIONNODE_CT, NodeList.nodeList(conditionExpression,
                                                                                              thenExpression,
                                                                                              elseExpression),
                                                       n.getText());
    }

    public BlockStmt add(InfixOpNode n) {
        Expression determineOperatorExpression = new MethodCallExpr(INFIXOPERATOR_N, DETERMINEOPERATOR_S,
                                                                    new NodeList<>(new StringLiteralExpr(n.getOperator().getSymbol())));
        Expression leftExpression = getNodeExpression(n.getLeft());
        Expression rightExpression = getNodeExpression(n.getRight());
        return addVariableDeclaratorWithObjectCreation(INFIXOPNODE_CT, NodeList.nodeList(determineOperatorExpression,
                                                                                         leftExpression,
                                                                                         rightExpression), n.getText());
    }

    public BlockStmt add(InNode n) {
        Expression valueExpression = getNodeExpression(n.getValue());
        Expression exprsExpression = getNodeExpression(n.getExprs());
        return addVariableDeclaratorWithObjectCreation(INNODE_CT, NodeList.nodeList(valueExpression, exprsExpression)
                , n.getText());
    }

    public BlockStmt add(InstanceOfNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression typeExpression = getNodeExpression(n.getType());
        return addVariableDeclaratorWithObjectCreation(INSTANCEOFNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                            typeExpression),
                                                       n.getText());
    }

    public BlockStmt add(IterationContextNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression rangeEndExprExpression = getNodeExpression(n.getRangeEndExpr());
        return addVariableDeclaratorWithObjectCreation(ITERATIONCONTEXTNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                  expressionExpression, rangeEndExprExpression), n.getText());
    }

    public BlockStmt add(ListNode n) {
        List elements = n.getElements().stream().map(elementNode -> getNodeExpression(elementNode))
                .toList();
        return addVariableDeclaratorWithObjectCreation(LISTNODE_CT, NodeList.nodeList(getListExpression(elements)),
                                                       n.getText());
    }

    public BlockStmt add(ListTypeNode n) {
        Expression genTypeNodeExpression = getNodeExpression(n.getGenTypeNode());
        return addVariableDeclaratorWithObjectCreation(LISTTYPENODE_CT, NodeList.nodeList(genTypeNodeExpression),
                                                       n.getText());
    }

    public BlockStmt add(NameDefNode n) {
        List partsExpressions = n.getParts().stream().map(StringLiteralExpr::new)
                .toList();
        Expression nameExpression = n.getName() != null ? new StringLiteralExpr(n.getName()) : new NullLiteralExpr();
        return addVariableDeclaratorWithObjectCreation(NAMEDEFNODE_CT,
                                                       NodeList.nodeList(getListExpression(partsExpressions),
                                                                         nameExpression), n.getText());
    }

    public BlockStmt add(NamedParameterNode n) {
        Expression nameExpression = getNodeExpression(n.getName());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(NAMEDPARAMETERNODE_CT, NodeList.nodeList(nameExpression,
                                                                                                expressionExpression)
                , n.getText());
    }

    public BlockStmt add(NameRefNode n) {
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addVariableDeclaratorWithObjectCreation(NAMEREFNODE_CT, NodeList.nodeList(typeExpression), n.getText());
    }

    public BlockStmt add(NullNode n) {
        return addVariableDeclaratorWithObjectCreation(NULLNODE_CT, NodeList.nodeList(), n.getText());
    }

    public BlockStmt add(NumberNode n) {
        MethodCallExpr valueExpression = new MethodCallExpr(NUMBEREVALHELPER_N, GETBIGDECIMALORNULL_S,
                                                            NodeList.nodeList(new StringLiteralExpr(n.getText())));
        return addVariableDeclaratorWithObjectCreation(NUMBERNODE_CT, NodeList.nodeList(valueExpression), n.getText());
    }

    public BlockStmt add(PathExpressionNode n) {
        Expression expressionExpression = getNodeExpression(n.getExpression());
        Expression nameExpression = getNodeExpression(n.getName());
        return addVariableDeclaratorWithObjectCreation(PATHEXPRESSIONNODE_CT, NodeList.nodeList(expressionExpression,
                                                                                                nameExpression),
                                                       n.getText());
    }

    public BlockStmt add(QualifiedNameNode n) {
        List partsExpressions = n.getParts().stream().map(partNode -> getNodeExpression(partNode))
                .toList();
        Expression typeExpression = getTypeExpression(n.getResultType());
        return addVariableDeclaratorWithObjectCreation(QUALIFIEDNAMENODE_CT,
                                                       NodeList.nodeList(getListExpression(partsExpressions),
                                                                         typeExpression),
                                                       n.getText());
    }

    public BlockStmt add(QuantifiedExpressionNode n) {
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

    public BlockStmt add(RangeNode n) {
        Expression lowerBoundExpression = getEnumExpression(n.getLowerBound());
        Expression upperBoundExpression = getEnumExpression(n.getUpperBound());
        Expression startExpression = getNodeExpression(n.getStart());
        Expression endExpression = getNodeExpression(n.getEnd());
        return addVariableDeclaratorWithObjectCreation(RANGENODE_CT, NodeList.nodeList(lowerBoundExpression,
                                                                                       upperBoundExpression,
                                                                                       startExpression,
                                                                                       endExpression), n.getText());
    }

    public BlockStmt add(SignedUnaryNode n) {
        Expression signExpression = getEnumExpression(n.getSign());
        Expression expressionExpression = getNodeExpression(n.getExpression());
        return addVariableDeclaratorWithObjectCreation(SIGNEDUNARYNODE_CT, NodeList.nodeList(signExpression,
                                                                                             expressionExpression),
                                                       n.getText());
    }

    public BlockStmt add(StringNode n) {
        return addVariableDeclaratorWithObjectCreation(STRINGNODE_CT, NodeList.nodeList(),
                                                       n.getText());
    }

    public BlockStmt add(TemporalConstantNode n) {
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

    public BlockStmt add(UnaryTestListNode n) {
        List elementsExpressions = n.getElements().stream().map(elementNode -> getNodeExpression(elementNode))
                .toList();
        Expression stateExpression = getEnumExpression(n.getState());
        return addVariableDeclaratorWithObjectCreation(UNARYTESTLISTNODE_CT,
                                                       NodeList.nodeList(getListExpression(elementsExpressions),
                                                                         stateExpression),
                                                       n.getText());
    }

    public BlockStmt add(UnaryTestNode n) {
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
        ObjectCreationExpr illegalStateExceptionExpression = new ObjectCreationExpr(null, ILLEGALSTATEEXCEPTION_CT,
                                                                                    NodeList.nodeList(getStringLiteralExpr(errorMessage)));
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

    private Expression getTypeExpression(Type type) {
        if (!typeCache.containsKey(type)) {
            Expression toPut;
            if (type instanceof AliasFEELType aliasFEELType) {
                toPut = getAliasFEELType(aliasFEELType);
            } else if (type instanceof Enum typeEnum) {
                toPut = getEnumExpression(typeEnum);
            } else if (type instanceof JavaBackedType javaBackedType) {
                toPut = getJavaBackedTypeExpression(javaBackedType);
            } else if (type instanceof MapBackedType mapBackedType) {
                toPut = getMapBackedTypeExpression(mapBackedType);
            } else {
                toPut = parseExpression(type.getClass().getCanonicalName());
            }
            typeCache.put(type, toPut);
        }
        return typeCache.get(type);
    }

    private Expression getAliasFEELType(AliasFEELType aliasFEELType) {
        BuiltInType feelCType = aliasFEELType.getBuiltInType();
        Expression typeExpression = new FieldAccessExpr(BUILTINTYPE_E, feelCType.name());
        // Creating the AliasFEELType
        String aliasFeelTypeVariableName = getNextVariableName();
        final VariableDeclarator aliasFeelTypeVariableDeclarator =
                new VariableDeclarator(ALIASFEELTYPE_CT, aliasFeelTypeVariableName);
        NodeList<Expression> arguments = NodeList.nodeList(new StringLiteralExpr(aliasFEELType.getName()),
                                                           typeExpression);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, ALIASFEELTYPE_CT,
                                                                             arguments);
        aliasFeelTypeVariableDeclarator.setInitializer(objectCreationExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr =
                new VariableDeclarationExpr(aliasFeelTypeVariableDeclarator);
        addExpression(mapVariableDeclarationExpr, aliasFeelTypeVariableName);
        return new NameExpr(aliasFeelTypeVariableName);
    }

    private Expression getJavaBackedTypeExpression(JavaBackedType javaBackedType) {
        // Creating the JavaBackedType
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(TYPE_CT, mapVariableName);
        Expression classExpression = new NameExpr(javaBackedType.getWrapped().getCanonicalName() + ".class");
        final MethodCallExpr methodCallExpr = new MethodCallExpr(JAVABACKEDTYPE_N, OF_S,
                                                                 NodeList.nodeList(classExpression));
        mapVariableDeclarator.setInitializer(methodCallExpr);
        VariableDeclarationExpr mapVariableDeclarationExpr = new VariableDeclarationExpr(mapVariableDeclarator);
        addExpression(mapVariableDeclarationExpr, mapVariableName);
        return new NameExpr(mapVariableName);
    }

    private Expression getMapBackedTypeExpression(MapBackedType mapBackedType) {
        Map<Expression, Expression> fieldsExpressions = new HashMap<>(); // The key is the StringLiteralExpr of the
        // original key; the value is the Expression pointing at type
        for (Map.Entry<String, Type> kv : mapBackedType.getFields().entrySet()) {
            fieldsExpressions.put(new StringLiteralExpr(kv.getKey()), getTypeExpression(kv.getValue()));
        }

        // Creating the MapBackedType
        String mapVariableName = getNextVariableName();
        final VariableDeclarator mapVariableDeclarator =
                new VariableDeclarator(MAPBACKEDTYPE_CT, mapVariableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, MAPBACKEDTYPE_CT,
                                                                             NodeList.nodeList(new StringLiteralExpr(mapBackedType.getName())));
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

    private Expression getObjectExpression(Object object) {
        if (object == null) {
            return new NullLiteralExpr();
        }
        if (!objectCache.containsKey(object)) {
            String variableName = getNextVariableName();
            Expression objectExpression = CodegenUtils.getObjectExpression(object, variableName);
            addExpression(objectExpression, variableName);
            objectCache.put(object, new NameExpr(lastVariableName.get()));
        }
        return objectCache.get(object);
    }

    private Expression getNodeExpression(BaseNode node) {
        if (node != null) {
            if (!baseNodeCache.containsKey(node)) {
                node.accept(astCompilerVisitor);
                baseNodeCache.put(node, new NameExpr(lastVariableName.get()));
            }
            return baseNodeCache.get(node);
        } else {
            return new NullLiteralExpr();
        }
    }
}