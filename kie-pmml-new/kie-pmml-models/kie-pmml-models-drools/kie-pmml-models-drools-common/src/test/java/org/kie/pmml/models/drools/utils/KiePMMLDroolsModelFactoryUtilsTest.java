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

package org.kie.pmml.models.drools.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.tree.TreeModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLDroolsModelFactoryUtilsTest {

    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeClass
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(TEMPLATE_SOURCE);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    public void getKiePMMLModelCompilationUnit() {
        DataDictionary dataDictionary = new DataDictionary();
        String targetFieldString = "target.field";
        FieldName targetFieldName = FieldName.create(targetFieldString);
        dataDictionary.addDataFields(new DataField(targetFieldName, OpType.CONTINUOUS, DataType.DOUBLE));
        String modelName = "ModelName";
        Model model = new TreeModel();
        model.setModelName(modelName);
        model.setMiningFunction(MiningFunction.CLASSIFICATION);
        MiningField targetMiningField = new MiningField(targetFieldName);
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(targetMiningField);
        model.setMiningSchema(miningSchema);
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        fieldTypeMap.put(targetFieldString, new KiePMMLOriginalTypeGeneratedType(targetFieldString, getSanitizedClassName(targetFieldString)));
        String packageName = "net.test";
        CompilationUnit retrieved = KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit(dataDictionary, model, fieldTypeMap, packageName, TEMPLATE_SOURCE, TEMPLATE_CLASS_NAME);
        assertEquals(packageName, retrieved.getPackageDeclaration().get().getNameAsString());
        ConstructorDeclaration constructorDeclaration = retrieved.getClassByName(modelName).get().getDefaultConstructor().get();
        MINING_FUNCTION miningFunction = MINING_FUNCTION.CLASSIFICATION;
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetFieldString));
        assignExpressionMap.put("miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        commonEvaluateAssignExpr(constructorDeclaration.getBody(), assignExpressionMap);
        int expectedMethodCallExprs = assignExpressionMap.size() + fieldTypeMap.size() + 1; // The last "1" is for the super invocation
        commonEvaluateFieldTypeMap(constructorDeclaration.getBody(), fieldTypeMap, expectedMethodCallExprs);
    }

    @Test
    public void setConstructor() {
        Model model = new TreeModel();
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        SimpleName tableName = new SimpleName("TABLE_NAME");
        String targetField = "TARGET_FIELD";
        MINING_FUNCTION miningFunction = MINING_FUNCTION.CLASSIFICATION;
        KiePMMLDroolsModelFactoryUtils.setConstructor(model, constructorDeclaration, tableName, targetField, miningFunction);
        assertEquals(tableName, constructorDeclaration.getName());
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        commonEvaluateAssignExpr(constructorDeclaration.getBody(), assignExpressionMap);
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
        KiePMMLDroolsModelFactoryUtils.addOutputFieldsPopulation(blockStmt, outputFields);
        List<MethodCallExpr> retrieved = getMethodCallExprList(blockStmt, outputFields.size(), "outputFields", "add");
        for (KiePMMLOutputField outputField : outputFields) {
            assertTrue(retrieved.stream()
                               .filter(methodCallExpr -> methodCallExpr.getArguments().size() == 1)
                               .map(methodCallExpr -> methodCallExpr.getArgument(0))
                               .filter(Expression::isMethodCallExpr)
                               .map(expressionArgument -> (MethodCallExpr) expressionArgument)
                               .anyMatch(methodCallExpr -> {
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
                               }));
        }
    }

    @Test
    public void addFieldTypeMapPopulation() {
        BlockStmt blockStmt = new BlockStmt();
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        IntStream.range(0, 3).forEach(index -> {
            String key = "KEY-" + index;
            KiePMMLOriginalTypeGeneratedType value = new KiePMMLOriginalTypeGeneratedType("ORIGINALTYPE-" + index, "GENERATEDTYPE-" + index);
            fieldTypeMap.put(key, value);
        });
        KiePMMLDroolsModelFactoryUtils.addFieldTypeMapPopulation(blockStmt, fieldTypeMap);
        commonEvaluateFieldTypeMap(blockStmt, fieldTypeMap, fieldTypeMap.size());
    }

    private void commonEvaluateAssignExpr(BlockStmt blockStmt, Map<String, Expression> assignExpressionMap) {
        List<AssignExpr> retrieved = blockStmt.findAll(AssignExpr.class);
        for (Map.Entry<String, Expression> entry : assignExpressionMap.entrySet()) {
            assertTrue(retrieved.stream()
                               .filter(assignExpr -> assignExpr.getTarget().asNameExpr().equals(new NameExpr(entry.getKey())))
                               .anyMatch(assignExpr -> assignExpr.getValue().equals(entry.getValue())));
        }
    }

    private void commonEvaluateFieldTypeMap(BlockStmt blockStmt, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, int expectedMethodCallSize) {
        List<MethodCallExpr> retrieved = getMethodCallExprList(blockStmt, expectedMethodCallSize, "fieldTypeMap", "put");
        for (Map.Entry<String, KiePMMLOriginalTypeGeneratedType> entry : fieldTypeMap.entrySet()) {
            assertTrue(retrieved.stream()
                               .map(MethodCallExpr::getArguments)
                               .anyMatch(arguments -> {
                                   boolean toReturn = arguments.size() == 2;
                                   Expression firstArgument = arguments.get(0);
                                   Expression secondArgument = arguments.get(1);
                                   toReturn &= firstArgument.isStringLiteralExpr() && ((StringLiteralExpr) firstArgument).getValue().equals(entry.getKey());
                                   toReturn &= secondArgument.isObjectCreationExpr() &&
                                           ((ObjectCreationExpr) secondArgument).getArgument(0).isStringLiteralExpr() &&
                                           ((StringLiteralExpr) ((ObjectCreationExpr) secondArgument).getArgument(0)).getValue().equals(entry.getValue().getOriginalType()) &&
                                           ((ObjectCreationExpr) secondArgument).getArgument(1).isStringLiteralExpr() &&
                                           ((StringLiteralExpr) ((ObjectCreationExpr) secondArgument).getArgument(1)).getValue().equals(entry.getValue().getGeneratedType());
                                   return toReturn;
                               }));
        }
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

    private List<MethodCallExpr> getMethodCallExprList(BlockStmt blockStmt, int expectedSize, String scope, String method) {
        Stream<Statement> statementStream = getStatementStream(blockStmt, expectedSize);
        return statementStream
                .filter(Statement::isExpressionStmt)
                .map(expressionStmt -> ((ExpressionStmt) expressionStmt).getExpression())
                .filter(expression -> expression instanceof MethodCallExpr)
                .map(expression -> (MethodCallExpr) expression)
                .filter(methodCallExpr ->
                                methodCallExpr.getScope().isPresent() &&
                                        methodCallExpr.getScope().get().isNameExpr() &&
                                        ((NameExpr) methodCallExpr.getScope().get()).getName().asString().equals(scope) &&
                                        methodCallExpr.getName().asString().equals(method))
                .collect(Collectors.toList());
    }

    private Stream<Statement> getStatementStream(BlockStmt blockStmt, int expectedSize) {
        final NodeList<Statement> statements = blockStmt.getStatements();
        assertEquals(expectedSize, statements.size());
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        statements.iterator(),
                        Spliterator.ORDERED), false);
    }
}