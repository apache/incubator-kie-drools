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
import java.util.Optional;
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
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.api.enums.RESULT_FEATURE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomMiningField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomOutputField;
import static org.kie.pmml.compiler.commons.utils.DerivedFieldFunctionUtils.getDerivedFieldsMethodMap;
import static org.kie.pmml.compiler.commons.utils.DerivedFieldFunctionUtils.getDiscretizeMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelFactoryUtilsTest {

    private static final String SOURCE = "TransformationsSample.pmml";
    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static PMML pmmlModel;
    private static TreeModel model;
    private ConstructorDeclaration constructorDeclaration;
    private ExplicitConstructorInvocationStmt superInvocation;
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
        assertNotNull(constructorDeclaration.getBody());
        Optional<ExplicitConstructorInvocationStmt> optSuperInvocation =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(constructorDeclaration.getBody());
        assertTrue(optSuperInvocation.isPresent());
        superInvocation = optSuperInvocation.get();
        assertEquals("Template", constructorDeclaration.getName().asString()); // as in the original template
        assertEquals("super(name, Collections.emptyList(), operator, second);", superInvocation.toString()); // as in
        // the original template
        assertTrue(compilationUnit.getClassByName(TEMPLATE_CLASS_NAME).isPresent());
        classOrInterfaceDeclaration = compilationUnit.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    public void setConstructorSuperNameInvocation() {
        String generatedClassName = "generatedClassName";
        String name = "newName";
        KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation(generatedClassName,
                                                                   constructorDeclaration,
                                                                   name);
        commonVerifySuperInvocation(generatedClassName, name);
    }

    @Test
    public void setKiePMMLModelConstructor() {
        String generatedClassName = "generatedClassName";
        String name = "newName";
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                             getRandomDataField()))
                .collect(Collectors.toList());
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                             getRandomDataField()))
                .collect(Collectors.toList());
        KiePMMLModelFactoryUtils.setKiePMMLModelConstructor(generatedClassName,
                                                            constructorDeclaration,
                                                            name,
                                                            miningFields,
                                                            outputFields);
        commonVerifySuperInvocation(generatedClassName, name);
        List<MethodCallExpr> retrieved = getMethodCallExprList(constructorDeclaration.getBody(), miningFields.size(), "miningFields",
                                                               "add");
        MethodCallExpr addMethodCall = retrieved.get(0);
        NodeList<Expression> arguments = addMethodCall.getArguments();
        commonVerifyMiningFieldsObjectCreation(arguments, miningFields);
        retrieved = getMethodCallExprList(constructorDeclaration.getBody(), outputFields.size(), "outputFields",
                                                               "add");
        addMethodCall = retrieved.get(0);
        arguments = addMethodCall.getArguments();
        commonVerifyOutputFieldsObjectCreation(arguments, outputFields);
    }

    @Test
    public void addKiePMMLOutputFieldsPopulation() {
        BlockStmt blockStmt = new BlockStmt();
        List<KiePMMLOutputField> outputFields = IntStream.range(0, 3)
                .mapToObj(index -> KiePMMLOutputField.builder("OUTPUTFIELD-" + index, Collections.emptyList())
                        .withRank(new Random().nextInt(3))
                        .withValue("VALUE-" + index)
                        .withTargetField("TARGETFIELD-" + index)
                        .build())
                .collect(Collectors.toList());
        KiePMMLModelFactoryUtils.addKiePMMLOutputFieldsPopulation(blockStmt, outputFields);
        List<MethodCallExpr> retrieved = getMethodCallExprList(blockStmt, outputFields.size(), "kiePMMLOutputFields",
                                                               "add");
        for (KiePMMLOutputField outputField : outputFields) {
            assertTrue(retrieved.stream()
                               .filter(methodCallExpr -> methodCallExpr.getArguments().size() == 1)
                               .map(methodCallExpr -> methodCallExpr.getArgument(0))
                               .filter(Expression::isMethodCallExpr)
                               .map(expressionArgument -> (MethodCallExpr) expressionArgument)
                               .anyMatch(methodCallExpr -> evaluateKiePMMLOutputFieldPopulation(methodCallExpr, outputField)));
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
    public void getMiningFieldsObjectCreations() {
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = KiePMMLModelFactoryUtils.getMiningFieldsObjectCreations(miningFields);
        commonVerifyMiningFieldsObjectCreation(retrieved, miningFields);
    }

    @Test
    public void createIntervalsExpression() {
        List<Interval> intervals = IntStream.range(0, 3)
                .mapToObj(i -> {
                    int leftMargin = new Random().nextInt(40);
                    int rightMargin = leftMargin +13;
                    return new Interval(leftMargin, rightMargin);
                })
                .collect(Collectors.toList());
        Expression retrieved = KiePMMLModelFactoryUtils.createIntervalsExpression(intervals);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof MethodCallExpr);
        MethodCallExpr mtdExp = (MethodCallExpr) retrieved;
        String expected = "java.util.Arrays";
        assertEquals(expected, mtdExp.getScope().get().asNameExpr().toString());
        expected = "asList";
        assertEquals(expected, mtdExp.getName().asString());
        NodeList<Expression> arguments = mtdExp.getArguments();
        assertEquals(intervals.size(), arguments.size());
        arguments.forEach(argument -> {
            assertTrue(argument instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertEquals(Interval.class.getCanonicalName(), objCrt.getType().asString());
            Optional<Interval> intervalOpt = intervals.stream()
                    .filter(interval -> String.valueOf(interval.getLeftMargin()).equals(objCrt.getArgument(0).asNameExpr().toString()) &&
                            String.valueOf(interval.getRightMargin()).equals(objCrt.getArgument(1).asNameExpr().toString()) )
                    .findFirst();
            assertTrue(intervalOpt.isPresent());
        });
    }

    @Test
    public void getObjectCreationExprFromInterval() {
        Interval interval = new Interval(null, -14);
        ObjectCreationExpr retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        NodeList<Expression> arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertTrue(arguments.get(0) instanceof NullLiteralExpr);
        assertEquals(String.valueOf(interval.getRightMargin()), arguments.get(1).asNameExpr().toString());
        interval = new Interval(-13, 10);
        retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertEquals(String.valueOf(interval.getLeftMargin()), arguments.get(0).asNameExpr().toString());
        assertEquals(String.valueOf(interval.getRightMargin()), arguments.get(1).asNameExpr().toString());
        interval = new Interval(-13, null);
        retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertEquals(String.valueOf(interval.getLeftMargin()), arguments.get(0).asNameExpr().toString());
        assertTrue(arguments.get(1) instanceof NullLiteralExpr);
    }

    @Test
    public void getOutputFieldsObjectCreations() {
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = KiePMMLModelFactoryUtils.getOutputFieldsObjectCreations(outputFields);
        commonVerifyOutputFieldsObjectCreation(retrieved, outputFields);
    }

    @Test
    public void populateTransformationsInConstructor() {
        final AtomicInteger arityCounter = new AtomicInteger(0);
        final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap =
                getDerivedFieldsMethodMap(pmmlModel.getTransformationDictionary().getDerivedFields(), arityCounter);
        final Map<String, MethodDeclaration> localDerivedFieldsMethodMap =
                getDerivedFieldsMethodMap(model.getLocalTransformations().getDerivedFields(), arityCounter);
        KiePMMLModelFactoryUtils.populateTransformationsInConstructor(constructorDeclaration,
                                                                      commonDerivedFieldsMethodMap,
                                                                      localDerivedFieldsMethodMap);
        pmmlModel.getTransformationDictionary().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, commonDerivedFieldsMethodMap, "commonTransformationsMap"));
        model.getLocalTransformations().getDerivedFields().forEach(derivedField -> commonVerifyDerivedFieldTransformation(derivedField, localDerivedFieldsMethodMap, "localTransformationsMap"));
    }

    private void commonVerifyMiningFieldsObjectCreation(List <Expression> toVerify, List<MiningField> miningFields) {
        toVerify.forEach(expression -> {
            assertTrue(expression instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) expression;
            assertEquals(MiningField.class.getCanonicalName(), objCrt.getType().asString());
            Optional<MiningField> miningFieldOpt = miningFields.stream()
                    .filter(miningField -> miningField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertTrue(miningFieldOpt.isPresent());
            MiningField miningField = miningFieldOpt.get();
            assertEquals(MiningField.class.getCanonicalName(), objCrt.getType().asString());
            String expected = FIELD_USAGE_TYPE.class.getCanonicalName() + "." + miningField.getUsageType();
            assertEquals(expected, objCrt.getArgument(1).asNameExpr().toString());
            expected = DATA_TYPE.class.getCanonicalName() + "." + miningField.getDataType();
            assertEquals(expected, objCrt.getArgument(3).asNameExpr().toString());
            expected = "java.util.Arrays.asList()";
            assertEquals(expected, objCrt.getArgument(5).asMethodCallExpr().toString());
            assertEquals(expected, objCrt.getArgument(6).asMethodCallExpr().toString());
            });
    }

    private void commonVerifyOutputFieldsObjectCreation(List <Expression> toVerify, List<OutputField> outputFields) {
        toVerify.forEach(argument -> {
            assertTrue(argument instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertEquals(OutputField.class.getCanonicalName(),  objCrt.getType().asString());
            Optional<OutputField> outputFieldOpt = outputFields.stream()
                    .filter(miningField -> miningField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertTrue(outputFieldOpt.isPresent());
            OutputField outputField = outputFieldOpt.get();
            String expected = OP_TYPE.class.getCanonicalName() + "." + outputField.getOpType();
            assertEquals(expected, objCrt.getArgument(1).asNameExpr().toString());
            expected = DATA_TYPE.class.getCanonicalName() + "." + outputField.getDataType();
            assertEquals(expected, objCrt.getArgument(2).asNameExpr().toString());
            expected = outputField.getTargetField();
            assertEquals(expected, objCrt.getArgument(3).asStringLiteralExpr().asString());
            expected = RESULT_FEATURE.class.getCanonicalName() + "." + outputField.getResultFeature();
            assertEquals(expected, objCrt.getArgument(4).asNameExpr().toString());
            expected = "java.util.Arrays.asList()";
            assertEquals(expected, objCrt.getArgument(5).asMethodCallExpr().toString());
        });
    }

    private void commonVerifySuperInvocation(String generatedClassName, String name) {
        assertEquals(generatedClassName, constructorDeclaration.getName().asString()); // modified by invocation
        String expected = String.format("super(\"%s\", Collections.emptyList(), operator, second);", name);
        assertEquals(expected, superInvocation.toString()); // modified by invocation
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

    private void commonVerifyDerivedFieldTransformation(DerivedField toVerify,
                                                        Map<String, MethodDeclaration> derivedFieldsMethodMap,
                                                        String mapToVerify) {
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
                assertEquals(derivedFieldsMethodMap.get(fieldName).getName().asString(),
                             ((MethodReferenceExpr) methodCallExpr.getArgument(1)).getIdentifier());
            }
            return true;
        }));
    }

    private boolean evaluateKiePMMLOutputFieldPopulation(MethodCallExpr methodCallExpr, KiePMMLOutputField outputField) {
        boolean toReturn = commonEvaluateMethodCallExpr(methodCallExpr, "build", new NodeList<>(),
                                                        MethodCallExpr.class);
        MethodCallExpr resultFeatureScopeExpr = (MethodCallExpr) methodCallExpr.getScope().get();
        NodeList<Expression> expectedArguments =
                NodeList.nodeList(new NameExpr(RESULT_FEATURE.class.getName() + "." + outputField.getResultFeature().toString()));
        toReturn &= commonEvaluateMethodCallExpr(resultFeatureScopeExpr, "withResultFeature", expectedArguments,
                                                 MethodCallExpr.class);
        MethodCallExpr targetFieldScopeExpr = (MethodCallExpr) resultFeatureScopeExpr.getScope().get();
        expectedArguments = NodeList.nodeList(new StringLiteralExpr(outputField.getTargetField().get()));
        toReturn &= commonEvaluateMethodCallExpr(targetFieldScopeExpr, "withTargetField", expectedArguments,
                                                 MethodCallExpr.class);
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

    private boolean commonEvaluateMethodCallExpr(MethodCallExpr toEvaluate, String name,
                                                 NodeList<Expression> expectedArguments,
                                                 Class<? extends Expression> expectedScopeType) {
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
    private List<MethodCallExpr> getMethodCallExprList(BlockStmt blockStmt, int expectedSize, String scope,
                                                       String method) {
        Stream<Statement> statementStream = getStatementStream(blockStmt);
        List<MethodCallExpr> toReturn =  statementStream
                .filter(Statement::isExpressionStmt)
                .map(expressionStmt -> ((ExpressionStmt) expressionStmt).getExpression())
                .filter(expression -> expression instanceof MethodCallExpr)
                .map(expression -> (MethodCallExpr) expression)
                .filter(methodCallExpr -> evaluateMethodCallExpr(methodCallExpr, scope, method))
                .collect(Collectors.toList());
        assertEquals(expectedSize, toReturn.size());
        return toReturn;
    }

    /**
     * Return a <code>List&lt;MethodCallExpr&gt;</code> where every element <b>scope' name</b> is <code>scope</code>
     * and every element <b>name</b> is <code>method</code>
     * @param blockStmt
     * @param scope
     * @param method
     * @return
     */
    private List<MethodCallExpr> getMethodCallExprList(BlockStmt blockStmt, String scope,
                                                       String method) {
        Stream<Statement> statementStream = getStatementStream(blockStmt);
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

    private Stream<Statement> getStatementStream(BlockStmt blockStmt) {
        final NodeList<Statement> statements = blockStmt.getStatements();
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        statements.iterator(),
                        Spliterator.ORDERED), false);
    }

    private void commonVerifyDerivedFieldMethodMap(DerivedField toVerify,
                                                   Map<String, MethodDeclaration> derivedFieldsMethodMap) {
        assertNotNull(derivedFieldsMethodMap);
        assertTrue(derivedFieldsMethodMap.containsKey(toVerify.getName().getValue()));
    }
}