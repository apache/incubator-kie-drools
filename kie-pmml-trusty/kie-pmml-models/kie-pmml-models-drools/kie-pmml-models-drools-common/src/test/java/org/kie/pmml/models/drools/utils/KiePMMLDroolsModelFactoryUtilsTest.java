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
package org.kie.pmml.models.drools.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.github.javaparser.ast.expr.Expression;
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
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.models.drools.dto.DroolsCompilationDTO;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateAssignExpr;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLDroolsModelFactoryUtilsTest {

    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    @BeforeAll
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(TEMPLATE_SOURCE);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(TEMPLATE_CLASS_NAME).get();
    }

    @Test
    void getKiePMMLModelCompilationUnit() {
        DataDictionary dataDictionary = new DataDictionary();
        String targetFieldString = "target.field";
         String targetFieldName =targetFieldString;
        dataDictionary.addDataFields(new DataField(targetFieldName, OpType.CONTINUOUS, DataType.DOUBLE));
        String modelName = "ModelName";
        TreeModel model = new TreeModel();
        model.setModelName(modelName);
        model.setMiningFunction(MiningFunction.CLASSIFICATION);
        MiningField targetMiningField = new MiningField(targetFieldName);
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(targetMiningField);
        model.setMiningSchema(miningSchema);
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        fieldTypeMap.put(targetFieldString, new KiePMMLOriginalTypeGeneratedType(targetFieldString,
                getSanitizedClassName(targetFieldString)));
        String packageName = "net.test";
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(model);
        final CommonCompilationDTO<TreeModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(packageName,
                                                                       pmml,
                                                                       model,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final DroolsCompilationDTO<TreeModel> droolsCompilationDTO =
                DroolsCompilationDTO.fromCompilationDTO(source, fieldTypeMap);
        CompilationUnit retrieved =
                KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit(droolsCompilationDTO, TEMPLATE_SOURCE,
                        TEMPLATE_CLASS_NAME);
        assertThat(retrieved.getPackageDeclaration().get().getNameAsString()).isEqualTo(droolsCompilationDTO.getPackageName());
        ConstructorDeclaration constructorDeclaration =
                retrieved.getClassByName(modelName).get().getDefaultConstructor().get();
        MINING_FUNCTION miningFunction = MINING_FUNCTION.CLASSIFICATION;
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetFieldString));
        assignExpressionMap.put("miningFunction",
                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        String expectedKModulePackageName = getSanitizedPackageName(packageName + "." + modelName);
        assignExpressionMap.put("kModulePackageName", new StringLiteralExpr(expectedKModulePackageName));
        assertThat(commonEvaluateAssignExpr(constructorDeclaration.getBody(), assignExpressionMap)).isTrue();
        int expectedMethodCallExprs = assignExpressionMap.size() + fieldTypeMap.size() + 1; // The last "1" is for
        // the super invocation
        commonEvaluateFieldTypeMap(constructorDeclaration.getBody(), fieldTypeMap, expectedMethodCallExprs);
    }

    @Test
    void setConstructor() {
        Model model = new TreeModel();
        PMML_MODEL pmmlModel = PMML_MODEL.byName(model.getClass().getSimpleName());
        ConstructorDeclaration constructorDeclaration = MODEL_TEMPLATE.getDefaultConstructor().get();
        SimpleName tableName = new SimpleName("TABLE_NAME");
        String targetField = "TARGET_FIELD";
        MINING_FUNCTION miningFunction = MINING_FUNCTION.CLASSIFICATION;
        String kModulePackageName = getSanitizedPackageName("kModulePackageName");
        KiePMMLDroolsModelFactoryUtils.setConstructor(model, constructorDeclaration, tableName, targetField,
                miningFunction,
                kModulePackageName);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("miningFunction",
                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(pmmlModel.getClass().getName() + "." + pmmlModel.name()));
        assignExpressionMap.put("kModulePackageName", new StringLiteralExpr(kModulePackageName));
        assertThat(commonEvaluateConstructor(constructorDeclaration, tableName.asString(),
                superInvocationExpressionsMap,
                assignExpressionMap)).isTrue();
    }

    @Test
    void addFieldTypeMapPopulation() {
        BlockStmt blockStmt = new BlockStmt();
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        IntStream.range(0, 3).forEach(index -> {
            String key = "KEY-" + index;
            KiePMMLOriginalTypeGeneratedType value = new KiePMMLOriginalTypeGeneratedType("ORIGINALTYPE-" + index,
                    "GENERATEDTYPE-" + index);
            fieldTypeMap.put(key, value);
        });
        KiePMMLDroolsModelFactoryUtils.addFieldTypeMapPopulation(blockStmt, fieldTypeMap);
        commonEvaluateFieldTypeMap(blockStmt, fieldTypeMap, fieldTypeMap.size());
    }

    private void commonEvaluateFieldTypeMap(BlockStmt blockStmt,
                                            Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                            int expectedMethodCallSize) {
        List<MethodCallExpr> retrieved = getMethodCallExprList(blockStmt, expectedMethodCallSize, "fieldTypeMap",
                                                               "put");
        for (Map.Entry<String, KiePMMLOriginalTypeGeneratedType> entry : fieldTypeMap.entrySet()) {
            assertThat(retrieved.stream()
                               .map(MethodCallExpr::getArguments)
                               .anyMatch(arguments -> evaluateFieldTypeMapPopulation(entry, arguments))).isTrue();
        }
    }

    private boolean evaluateFieldTypeMapPopulation(Map.Entry<String, KiePMMLOriginalTypeGeneratedType> entry,
                                                   NodeList<Expression> arguments) {
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
        assertThat(statements).hasSize(expectedSize);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        statements.iterator(),
                        Spliterator.ORDERED), false);
    }
}