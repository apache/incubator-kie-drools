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
package org.kie.dmn.core.compiler.alphanetbased;

import java.math.BigDecimal;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.StaticJavaParser.parseType;
import static java.lang.String.format;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class TableCell {

    private static Logger logger = LoggerFactory.getLogger(TableCell.class);

    private final String input;
    private final DMNFEELHelper feel;
    private final CompilerContext compilerContext;
    private final TableIndex tableIndex;
    private final String columnName;
    private final Type type;
    private final String constraintIdentifier;

    private String className;
    private String classNameWithPackage;

    private static final String CREATE_INDEX_NODE_METHOD = "createIndex";

    // TODO DT-ANC https://issues.redhat.com/browse/DROOLS-6620
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
            return new TableCell(feel, compilerContext, tableIndex, input, columnName, columnType);
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

        // We don't want to share alpha nodes between two columns
        this.constraintIdentifier = CodegenStringUtil.escapeIdentifier(columnName + input);

        // FEEL expression among columns are the same
        String feelExpressionIdentifier = CodegenStringUtil.escapeIdentifier(input);

        this.className = feelExpressionIdentifier;
        this.classNameWithPackage = ALPHANETWORK_STATIC_PACKAGE + "." + this.className;
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

    private Index createIndex() {
        if (type.equals(BuiltInType.NUMBER)) {
            return AlphaNetworkCreation.createIndex(BigDecimal.class, x -> (BigDecimal) x.getValue(tableIndex.columnIndex()), null);
        } else if (type.equals(BuiltInType.STRING)) {
            return AlphaNetworkCreation.createIndex(String.class, x -> (String) x.getValue(tableIndex.columnIndex()), null);
        } else {
            throw new UnsupportedOperationException("Unknown Index Type");
        }
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

    public AlphaNode createAlphaNode(AlphaNetworkCreation alphaNetworkCreation, ReteBuilderContext reteBuilderContext, AlphaNode previousAlphaNode) {

        // This is used for Alpha Sharing. It needs to have the column name to avoid collisions with same test in other cells

        CanBeInlinedAlphaNode candidateAlphaNode;
        if (tableIndex.isFirstColumn()) {
            Index index = createIndex();
            candidateAlphaNode = CanBeInlinedAlphaNode.createBuilder()
                    .withConstraint(constraintIdentifier, null, index, reteBuilderContext.variable, reteBuilderContext.declaration)
                    .withFeelConstraint(classNameWithPackage, tableIndex.columnIndex(), "trace String")
                    .createAlphaNode(alphaNetworkCreation.getNextId(),
                                     reteBuilderContext.otn,
                                     reteBuilderContext.buildContext);
        } else {
            if (previousAlphaNode == null) {
                throw new RuntimeException("Need a previous Alpha Node");
            }

            candidateAlphaNode = CanBeInlinedAlphaNode.createBuilder()
                    .withConstraint(constraintIdentifier, null, null, reteBuilderContext.variable, reteBuilderContext.declaration)
                    .withFeelConstraint(classNameWithPackage, tableIndex.columnIndex(), "trace String")
                    .createAlphaNode(alphaNetworkCreation.getNextId(),
                                     previousAlphaNode,
                                     reteBuilderContext.buildContext);
        }

        return alphaNetworkCreation.shareAlphaNode(candidateAlphaNode);
    }

    private NameExpr alphaNodeCreationName() {
        return new NameExpr("alphaNetworkCreation");
    }

    public void crateUnaryTestAndAddTo(Map<String, String> allClasses) {
        if (allClasses.containsKey(classNameWithPackage)) {
            logger.debug("FEEL Unary Test {} already generated: {} avoiding generating", input, className);
            return;
        }

        UnaryTestClass unaryTestClass = new UnaryTestClass(input, feel, compilerContext, type);
        unaryTestClass.compileUnaryTestAndAddTo(allClasses, className, classNameWithPackage, ALPHANETWORK_STATIC_PACKAGE);
    }

    public void compiledFeelExpressionAndAddTo(Map<String, String> allGeneratedClasses) {
        if (allGeneratedClasses.containsKey(classNameWithPackage)) {
            logger.debug("FEEL Expression {} already generated: {} avoiding generating", input, className);
            return;
        }

        CompilationUnit sourceCode = feel.generateFeelExpressionCompilationUnit(
                input,
                compilerContext);

        replaceSimpleNameWith(sourceCode, "TemplateCompiledFEELExpression", className);

        sourceCode.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(c -> c.setName(className));

        sourceCode.setPackageDeclaration(ALPHANETWORK_STATIC_PACKAGE);
        allGeneratedClasses.put(classNameWithPackage, sourceCode.toString());
    }

    public void addOutputNode(AlphaNetworkCreation alphaNetworkCreation, AlphaNode lastAlphaNode) {
        alphaNetworkCreation.addResultSink(lastAlphaNode, tableIndex.rowIndex(), this.columnName, classNameWithPackage);
    }

    public void addToCells(TableCell[][] cells) {
        cells[tableIndex.rowIndex()][tableIndex.columnIndex()] = this;
    }

    public void addToOutputCells(TableCell[][] outputCells) {
        outputCells[tableIndex.rowIndex()][tableIndex.columnIndex()] = this;
    }
}



