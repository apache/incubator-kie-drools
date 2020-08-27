/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.compiler.commons.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.utils.DerivedFieldFunctionUtils.getDerivedFieldsMethodMap;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelFactoryUtilsTest {

    private static final String SOURCE = "TransformationsSample.pmml";
    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static PMML pmmlModel;
    private static TreeModel model;
    private ConstructorDeclaration constructorDeclaration;
    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    @BeforeClass
    public static void setup() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(SOURCE), "");
        assertNotNull(pmmlModel);
        model = (TreeModel) pmmlModel.getModels().get(0);
        assertNotNull(model);
    }

    @Before
    public void init() {
        CompilationUnit compilationUnit = getFromFileName(TEMPLATE_SOURCE);
        constructorDeclaration = compilationUnit.getClassByName(TEMPLATE_CLASS_NAME)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + TEMPLATE_CLASS_NAME + "  from " + TEMPLATE_SOURCE))
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + TEMPLATE_SOURCE));
        assertNotNull(constructorDeclaration);
        assertTrue(compilationUnit.getClassByName(TEMPLATE_CLASS_NAME).isPresent());
        classOrInterfaceDeclaration = compilationUnit.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    public void addOutputFieldsPopulation() {
        BlockStmt blockStmt = new BlockStmt();
        List<KiePMMLOutputField> outputFields = IntStream.range(0, 3)
                .mapToObj(index -> KiePMMLOutputField.builder("OUTPUTFIELD-" + index, Collections.emptyList())
                        .withRank(new Random().nextInt(3))
                        .withValue("VALUE-" + index)
                        .withTargetField("TARGETFIELD-" + index)
                        .build())
                .collect(Collectors.toList());
        KiePMMLModelFactoryUtils.addOutputFieldsPopulation(blockStmt, outputFields);
        List<MethodCallExpr> retrieved = getMethodCallExprList(blockStmt, outputFields.size(), "outputFields", "add");
        for (KiePMMLOutputField outputField : outputFields) {
            assertTrue(retrieved.stream()
                               .filter(methodCallExpr -> methodCallExpr.getArguments().size() == 1)
                               .map(methodCallExpr -> methodCallExpr.getArgument(0))
                               .filter(Expression::isMethodCallExpr)
                               .map(expressionArgument -> (MethodCallExpr) expressionArgument)
                               .anyMatch(methodCallExpr -> evaluateOutputFieldPopulation(methodCallExpr, outputField)));
        }
    }

    @Test
    public void addTransformationsInClassOrInterfaceDeclaration() {
        KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration(classOrInterfaceDeclaration,
                                                                                 pmmlModel.getTransformationDictionary(),
                                                                                 model.getLocalTransformations());
        pmmlModel.getTransformationDictionary().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, null, "commonTransformationsMap"));
        model.getLocalTransformations().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, null, "localTransformationsMap"));
        commonVerifyConstructorClass("commonTransformationsMap");
        commonVerifyConstructorClass("localTransformationsMap");
    }

    @Test
    public void populateTransformationsInConstructor() {
        final AtomicInteger arityCounter = new AtomicInteger(0);
        final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap = getDerivedFieldsMethodMap(pmmlModel.getTransformationDictionary().getDerivedFields(), arityCounter);
        final Map<String, MethodDeclaration> localDerivedFieldsMethodMap = getDerivedFieldsMethodMap(model.getLocalTransformations().getDerivedFields(), arityCounter);
        KiePMMLModelFactoryUtils.populateTransformationsInConstructor(constructorDeclaration, commonDerivedFieldsMethodMap, localDerivedFieldsMethodMap);
        pmmlModel.getTransformationDictionary().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, commonDerivedFieldsMethodMap, "commonTransformationsMap"));
        model.getLocalTransformations().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, localDerivedFieldsMethodMap, "localTransformationsMap"));
    }

    private void commonVerifyConstructorClass(String mapName) {
        List<MethodDeclaration> methodDeclarations = classOrInterfaceDeclaration.getMembers().stream()
                .filter(bodyDeclaration -> bodyDeclaration instanceof MethodDeclaration).map(bodyDeclaration -> (MethodDeclaration) bodyDeclaration).collect(Collectors.toList());
        NodeList<Statement> statements = constructorDeclaration.getBody().getStatements();
        statements.stream().filter(statement -> {
            if (!(statement instanceof ExpressionStmt)) {
                return false;
            }
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            if (!(expressionStmt.getExpression() instanceof MethodCallExpr)) {
                return false;
            }
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();
            if (!methodCallExpr.getScope().isPresent() || !(methodCallExpr.getScope().get() instanceof NameExpr)) {
                return false;
            }
            NameExpr nameExpr = (NameExpr) methodCallExpr.getScope().get();
            return mapName.equals(nameExpr.getNameAsString());
        }).map(statement -> {
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();
            return (MethodReferenceExpr) methodCallExpr.getArgument(1);
        }).forEach(methodReferenceExpr -> assertTrue(methodDeclarations
                           .stream()
                           .anyMatch(methodDeclaration ->
                                             methodDeclaration.getName().asString().equals(methodReferenceExpr.getIdentifier()))));
    }

    private void commonVerifyDerivedFieldTransformation(DerivedField toVerify, Map<String, MethodDeclaration> derivedFieldsMethodMap, String mapToVerify) {
        NodeList<Statement> statements = constructorDeclaration.getBody().getStatements();
        String fieldName = toVerify.getName().getValue();
        if (derivedFieldsMethodMap != null) {
            commonVerifyDerivedFieldMethodMap(toVerify, derivedFieldsMethodMap);
        }
        assertTrue(statements.stream().anyMatch(statement -> {
            if (!(statement instanceof ExpressionStmt)) {
                return false;
            }
            ExpressionStmt expressionStmt = (ExpressionStmt) statement;
            if (!(expressionStmt.getExpression() instanceof MethodCallExpr)) {
                return false;
            }
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionStmt.getExpression();
            if (!methodCallExpr.getScope().isPresent() || !(methodCallExpr.getScope().get() instanceof NameExpr)) {
                return false;
            }
            NameExpr nameExpr = (NameExpr) methodCallExpr.getScope().get();
            if (!mapToVerify.equals(nameExpr.getNameAsString())) {
                return false;
            }
            assertEquals("put", methodCallExpr.getName().asString());
            assertEquals(2, methodCallExpr.getArguments().size());
            assertTrue(methodCallExpr.getArgument(0) instanceof StringLiteralExpr);
            assertTrue(methodCallExpr.getArgument(1) instanceof MethodReferenceExpr);
            if (!fieldName.equals(((StringLiteralExpr) methodCallExpr.getArgument(0)).getValue())) {
                return false;
            }
            assertTrue(((MethodReferenceExpr) methodCallExpr.getArgument(1)).getScope() instanceof ThisExpr);
            if (derivedFieldsMethodMap != null) {
                assertEquals(derivedFieldsMethodMap.get(fieldName).getName().asString(), ((MethodReferenceExpr) methodCallExpr.getArgument(1)).getIdentifier());
            }
            return true;
        }));
    }

    private boolean evaluateOutputFieldPopulation(MethodCallExpr methodCallExpr, KiePMMLOutputField outputField) {
        boolean toReturn = commonEvaluateMethodCallExpr(methodCallExpr, "build", new NodeList<>(), MethodCallExpr.class);
        MethodCallExpr resultFeatureScopeExpr = (MethodCallExpr) methodCallExpr.getScope().get();
        NodeList<Expression> expectedArguments = NodeList.nodeList(new NameExpr(RESULT_FEATURE.class.getName() + "." + outputField.getResultFeature().toString()));
        toReturn &= commonEvaluateMethodCallExpr(resultFeatureScopeExpr, "withResultFeature", expectedArguments, MethodCallExpr.class);
        MethodCallExpr targetFieldScopeExpr = (MethodCallExpr) resultFeatureScopeExpr.getScope().get();
        expectedArguments = NodeList.nodeList(new StringLiteralExpr(outputField.getTargetField().get()));
        toReturn &= commonEvaluateMethodCallExpr(targetFieldScopeExpr, "withTargetField", expectedArguments, MethodCallExpr.class);
        MethodCallExpr valueScopeExpr = (MethodCallExpr) targetFieldScopeExpr.getScope().get();
        expectedArguments = NodeList.nodeList(new StringLiteralExpr(outputField.getValue().toString()));
        toReturn &= commonEvaluateMethodCallExpr(valueScopeExpr, "withValue", expectedArguments, MethodCallExpr.class);
        MethodCallExpr rankScopeExpr = (MethodCallExpr) valueScopeExpr.getScope().get();
        expectedArguments = NodeList.nodeList(new IntegerLiteralExpr(outputField.getRank()));
        toReturn &= commonEvaluateMethodCallExpr(rankScopeExpr, "withRank", expectedArguments, MethodCallExpr.class);
        MethodCallExpr builderScopeExpr = (MethodCallExpr) rankScopeExpr.getScope().get();
        expectedArguments = NodeList.nodeList(new StringLiteralExpr(outputField.getName()), new NameExpr("Collections.emptyList()"));
        toReturn &= commonEvaluateMethodCallExpr(builderScopeExpr, "builder", expectedArguments, NameExpr.class);
        toReturn &= builderScopeExpr.getName().equals(new SimpleName("builder"));
        toReturn &= builderScopeExpr.getScope().get().equals(new NameExpr("KiePMMLOutputField"));
        return toReturn;
    }

    private boolean commonEvaluateMethodCallExpr(MethodCallExpr toEvaluate, String name, NodeList<Expression> expectedArguments, Class<? extends Expression> expectedScopeType) {
        boolean toReturn = Objects.equals(new SimpleName(name), toEvaluate.getName());
        toReturn &= expectedArguments.size() == toEvaluate.getArguments().size();
        for (int i = 0; i < expectedArguments.size(); i++) {
            toReturn &= expectedArguments.get(i).equals(toEvaluate.getArgument(i));
        }
        if (expectedScopeType != null) {
            toReturn &= toEvaluate.getScope().isPresent() && toEvaluate.getScope().get().getClass().equals(expectedScopeType);
        }
        return toReturn;
    }

    /**
     * Return a <code>List&lt;MethodCallExpr&gt;</code> where every element <b>scope' name</b> is <code>scope</code>
     * and every element <b>name</b> is <code>method</code>
     * @param blockStmt
     * @param expectedSize
     * @param scope
     * @param method
     * @return
     */
    private List<MethodCallExpr> getMethodCallExprList(BlockStmt blockStmt, int expectedSize, String scope, String method) {
        Stream<Statement> statementStream = getStatementStream(blockStmt, expectedSize);
        return statementStream
                .filter(Statement::isExpressionStmt)
                .map(expressionStmt -> ((ExpressionStmt) expressionStmt).getExpression())
                .filter(expression -> expression instanceof MethodCallExpr)
                .map(expression -> (MethodCallExpr) expression)
                .filter(methodCallExpr -> evaluateMethodCallExpr(methodCallExpr, scope, method))
                .collect(Collectors.toList());
    }

    /**
     * Verify the <b>scope' name</b> scope of the given <code>MethodCallExpr</code> is <code>scope</code>
     * and the <b>name</b> of the given <code>MethodCallExpr</code> is <code>method</code>
     * @param methodCallExpr
     * @param scope
     * @param method
     * @return
     */
    private boolean evaluateMethodCallExpr(MethodCallExpr methodCallExpr, String scope, String method) {
        return methodCallExpr.getScope().isPresent() &&
                methodCallExpr.getScope().get().isNameExpr() &&
                ((NameExpr) methodCallExpr.getScope().get()).getName().asString().equals(scope) &&
                methodCallExpr.getName().asString().equals(method);
    }

    private Stream<Statement> getStatementStream(BlockStmt blockStmt, int expectedSize) {
        final NodeList<Statement> statements = blockStmt.getStatements();
        assertEquals(expectedSize, statements.size());
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        statements.iterator(),
                        Spliterator.ORDERED), false);
    }

    private void commonVerifyDerivedFieldMethodMap(DerivedField toVerify, Map<String, MethodDeclaration> derivedFieldsMethodMap) {
        assertNotNull(derivedFieldsMethodMap);
        assertTrue(derivedFieldsMethodMap.containsKey(toVerify.getName().getValue()));
    }
}