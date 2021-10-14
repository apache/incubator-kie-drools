/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import java.math.BigDecimal;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.drools.core.reteoo.AlphaNode;
import org.drools.model.Index;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.api.UnaryTests;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static java.lang.String.format;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceIntegerLiteralExprWith;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceStringLiteralExprWith;

public class TableCell {

    private final String input;
    private final DMNFEELHelper feel;
    private final CompilerContext compilerContext;
    private final TableIndex tableIndex;
    private final String columnName;
    private final Type type;

    private String className;
    private String classNameWithPackage;

    private static final String CREATE_ALPHA_NODE_METHOD = "createAlphaNode";
    private static final String CREATE_INDEX_NODE_METHOD = "createIndex";
    public static final String ALPHANETWORK_STATIC_PACKAGE = "org.kie.dmn.core.alphasupport";

    public static class TableCellFactory {

        final DMNFEELHelper feel;
        final CompilerContext compilerContext;

        public TableCellFactory(DMNFEELHelper feel, CompilerContext compilerContext) {
            this.feel = feel;
            this.compilerContext = compilerContext;
        }

        public TableCell createInputCell(TableIndex tableIndex, String input, String columnName, Type columnType) {
            return new TableCell(feel, compilerContext, tableIndex, input, columnName, columnType);
        }

        public TableCell createOutputCell(TableIndex tableIndex, String input, String columnName, Type columnType) {
            TableCell outputCell = new TableCell(feel, compilerContext, tableIndex, input, columnName, columnType);
            outputCell.className = tableIndex.appendOutputSuffix("");
            outputCell.classNameWithPackage = ALPHANETWORK_STATIC_PACKAGE + "." + outputCell.className;
            return outputCell;
        }

        public ColumnDefinition createColumnDefinition(int columnIndex, String decisionTableName, String columnName, UnaryTests inputValues, Type type) {
            return new ColumnDefinition(columnIndex, decisionTableName, columnName, inputValues, type, feel, compilerContext);
        }
    }

    private TableCell(DMNFEELHelper feel,
                      CompilerContext compilerContext,
                      TableIndex tableIndex,
                      String input,
                      String columnName,
                      Type columnType) {
        this.feel = feel;
        this.compilerContext = compilerContext;
        this.tableIndex = tableIndex;
        this.columnName = columnName;
        this.input = input;
        this.type = columnType;
        this.className = tableIndex.appendTableIndexSuffix("UnaryTest");
        this.classNameWithPackage = ALPHANETWORK_STATIC_PACKAGE + "." + className;
    }

    private String addIndex(BlockStmt stmt) {
        com.github.javaparser.ast.type.Type indexType = StaticJavaParser.parseType(Index.class.getCanonicalName());
        String indexName = tableIndex.appendTableIndexSuffix("index");

        VariableDeclarationExpr variable = new VariableDeclarationExpr(indexType, indexName);

        Expression indexMethodExpression = createIndexMethodExpression();
        final Expression expr = new AssignExpr(variable, indexMethodExpression, AssignExpr.Operator.ASSIGN);
        stmt.addStatement(expr);

        return indexName;
    }

    private Expression createIndexMethodExpression() {
        if (type.equals(BuiltInType.NUMBER)) {
            return createBigDecimalIndex();
        } else if (type.equals(BuiltInType.STRING)) {
            return createStringIndex();
        } else {
            throw new UnsupportedOperationException("Unknown Index Type");
        }
    }

    private Expression createBigDecimalIndex() {
        String bigDecimalClassFulName = BigDecimal.class.getCanonicalName();
        return new MethodCallExpr(alphaNodeCreationName(), CREATE_INDEX_NODE_METHOD, NodeList.nodeList(
                new ClassExpr(parseType(bigDecimalClassFulName)),
                parseExpression(format("x -> (%s)x.getValue(%s)",
                                       bigDecimalClassFulName,
                                       tableIndex.columnIndex())),
                new NullLiteralExpr()

        ));
    }

    private Expression createStringIndex() {
        return new MethodCallExpr(alphaNodeCreationName(), CREATE_INDEX_NODE_METHOD, NodeList.nodeList(
                new ClassExpr(parseType(String.class.getCanonicalName())),
                parseExpression(format("x -> (String)x.getValue(%s)",
                                       tableIndex.columnIndex())),
                isAQuotedString() ? new NameExpr(input) : new NullLiteralExpr()

        ));
    }

    private boolean isAQuotedString() {
        return input.startsWith("\"") && input.endsWith("\"");
    }

    public String addNodeCreation(BlockStmt stmt, ClassOrInterfaceDeclaration alphaNetworkCreationClass, MethodDeclaration testMethodDefinition) {
        com.github.javaparser.ast.type.Type alphaNodeType = StaticJavaParser.parseType(AlphaNode.class.getCanonicalName());
        String alphaNodeName = tableIndex.appendTableIndexSuffix("alphaNode");
        VariableDeclarationExpr variable = new VariableDeclarationExpr(alphaNodeType, alphaNodeName);

        // This is used for Alpha Sharing. It needs to have the column name to avoid collisions with same test in other cells
        String constraintIdentifier = CodegenStringUtil.escapeIdentifier(columnName + input);

        MethodDeclaration unaryTestMethod = testMethodDefinition.clone();
        String testMethodName = tableIndex.appendTableIndexSuffix("test");
        unaryTestMethod.setName(testMethodName);
        Expression methodReference = new MethodReferenceExpr(new ThisExpr(), NodeList.nodeList(), testMethodName);
        alphaNetworkCreationClass.addMember(unaryTestMethod);

        replaceSimpleNameWith(unaryTestMethod, "UnaryTestRXCX", classNameWithPackage);
        replaceStringLiteralExprWith(unaryTestMethod, "UnaryTestRXCX", classNameWithPackage);
        replaceIntegerLiteralExprWith(unaryTestMethod, 99999, tableIndex.columnIndex());

        Expression alphaNodeCreation;
        if (tableIndex.isFirstColumn()) {
            String indexName = addIndex(stmt);
            alphaNodeCreation = new MethodCallExpr(alphaNodeCreationName(), CREATE_ALPHA_NODE_METHOD, NodeList.nodeList(
                    parseExpression("ctx.otn"),
                    new StringLiteralExpr(constraintIdentifier),
                    methodReference,
                    new NameExpr(indexName)
            ));
        } else {
            alphaNodeCreation = new MethodCallExpr(alphaNodeCreationName(), CREATE_ALPHA_NODE_METHOD, NodeList.nodeList(
                    new NameExpr(tableIndex.previousColumn().appendTableIndexSuffix("alphaNode")),
                    new StringLiteralExpr(constraintIdentifier),
                    methodReference
            ));
        }

        final Expression expr = new AssignExpr(variable, alphaNodeCreation, AssignExpr.Operator.ASSIGN);
        stmt.addStatement(expr);

        return alphaNodeName;
    }

    private NameExpr alphaNodeCreationName() {
        return new NameExpr("alphaNetworkCreation");
    }

    public void compileUnaryTestAndAddTo(Map<String, String> allClasses) {
        UnaryTestClass unaryTestClass = new UnaryTestClass(input, feel, compilerContext, type);
        unaryTestClass.compileUnaryTestAndAddTo(allClasses, className, classNameWithPackage, ALPHANETWORK_STATIC_PACKAGE);
    }

    public void compiledFeelExpressionAndAddTo(Map<String, String> allGeneratedClasses) {
        CompilationUnit sourceCode = feel.generateFeelExpressionCompilationUnit(
                input,
                compilerContext);

        replaceSimpleNameWith(sourceCode, "TemplateCompiledFEELExpression", className);

        sourceCode.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(c -> c.setName(className));

        sourceCode.setPackageDeclaration(ALPHANETWORK_STATIC_PACKAGE);
        allGeneratedClasses.put(classNameWithPackage, sourceCode.toString());
    }

    public void addOutputNode(ClassOrInterfaceDeclaration alphaNetworkCreationClass,
                              MethodDeclaration outputMethodDefinitionTemplate,
                              BlockStmt creationStatements, String lastAlphaNodeName) {

        String testMethodName = tableIndex.appendOutputSuffix("output");
        MethodReferenceExpr methodReference = createFeelOutputExpression(alphaNetworkCreationClass,
                                                                         outputMethodDefinitionTemplate,
                                                                         testMethodName);

        Expression resultSinkMethodCallExpr = new MethodCallExpr(alphaNodeCreationName(),
                                                                 "addResultSink",
                                                                 NodeList.nodeList(
                                                                         new NameExpr(lastAlphaNodeName),
                                                                         new IntegerLiteralExpr(tableIndex.rowIndex()),
                                                                         new StringLiteralExpr(this.columnName),
                                                                         methodReference)
        );
        creationStatements.addStatement(resultSinkMethodCallExpr);
    }

    public MethodReferenceExpr createFeelOutputExpression(ClassOrInterfaceDeclaration alphaNetworkCreationClass,
                                                          MethodDeclaration outputMethodDefinitionTemplate,
                                                          String outputMethodName) {
        MethodDeclaration unaryTestMethod = outputMethodDefinitionTemplate.clone();
        unaryTestMethod.setName(outputMethodName);
        MethodReferenceExpr methodReference = new MethodReferenceExpr(new ThisExpr(), NodeList.nodeList(), outputMethodName);
        alphaNetworkCreationClass.addMember(unaryTestMethod);

        unaryTestMethod.findFirst(NameExpr.class, n -> n.toString().equals("OutputRXCX")).ifPresent(n -> n.replace(new NameExpr(classNameWithPackage)));

        return methodReference;
    }

    public void addToCells(TableCell[][] cells) {
        cells[tableIndex.rowIndex()][tableIndex.columnIndex()] = this;
    }

    public void addToOutputCells(TableCell[][] outputCells) {
        outputCells[tableIndex.rowIndex()][tableIndex.columnIndex()] = this;
    }
}



