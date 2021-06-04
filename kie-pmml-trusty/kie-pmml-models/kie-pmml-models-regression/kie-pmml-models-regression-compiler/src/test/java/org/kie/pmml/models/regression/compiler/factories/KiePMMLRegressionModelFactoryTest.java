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

package org.kie.pmml.models.regression.compiler.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.regression.model.KiePMMLRegressionClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getMiningSchema;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRegressionModel;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRegressionTable;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory.KIE_PMML_REGRESSION_MODEL_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory.KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA;

public class KiePMMLRegressionModelFactoryTest {

    private static final String PACKAGE_NAME = "packagename";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;

    private static final String modelName = "firstModel";
    private static final double tableIntercept =  3.5;
    private static final Object tableTargetCategory = "professional";
    private static List<RegressionTable> regressionTables;
    private static List<DataField> dataFields;
    private static List<MiningField> miningFields;
    private static MiningField targetMiningField;
    private static DataDictionary dataDictionary;
    private static TransformationDictionary transformationDictionary;
    private static MiningSchema miningSchema;
    private static RegressionModel regressionModel;

    @BeforeClass
    public static void setup() {
        Random random = new Random();
        Set<String> fieldNames = new HashSet<>();
        regressionTables = IntStream.range(0, 3).mapToObj(i -> {
                                                              List<CategoricalPredictor> categoricalPredictors = new ArrayList<>();
                                                              List<NumericPredictor> numericPredictors = new ArrayList<>();
                                                              List<PredictorTerm> predictorTerms = new ArrayList<>();
                                                              IntStream.range(0, 3).forEach(j -> {
                                                                  String catFieldName = "CatPred-" + j;
                                                                  String numFieldName = "NumPred-" + j;
                                                                  categoricalPredictors.add(getCategoricalPredictor(catFieldName, random.nextDouble(), random.nextDouble()));
                                                                  numericPredictors.add(getNumericPredictor(numFieldName, random.nextInt(), random.nextDouble()));
                                                                  predictorTerms.add(getPredictorTerm("PredTerm-" + j, random.nextDouble(), Arrays.asList(catFieldName, numFieldName)));
                                                                  fieldNames.add(catFieldName);
                                                                  fieldNames.add(numFieldName);
                                                              });
                                                              return getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, tableIntercept + random.nextDouble(), tableTargetCategory + "-" + i);
                                                          }
        ).collect(Collectors.toList());
        dataFields = new ArrayList<>();
        miningFields = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            dataFields.add(getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING));
            miningFields.add(getMiningField(fieldName, MiningField.UsageType.ACTIVE));
        });
        targetMiningField = miningFields.get(0);
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        dataDictionary = getDataDictionary(dataFields);
        transformationDictionary = new TransformationDictionary();
        miningSchema = getMiningSchema(miningFields);
        regressionModel = getRegressionModel(modelName, MiningFunction.REGRESSION, miningSchema, regressionTables);
        COMPILATION_UNIT = getFromFileName(KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_REGRESSION_MODEL_TEMPLATE).get();
    }

    @Test
    public void getKiePMMLRegressionModelClasses() throws IOException, IllegalAccessException, InstantiationException {
        KiePMMLRegressionModel retrieved = KiePMMLRegressionModelFactory.getKiePMMLRegressionModelClasses(dataDictionary,
                                                                                                          transformationDictionary,
                                                                                                          regressionModel,
                                                                                                          PACKAGE_NAME,
                                                                                                          new HasClassLoaderMock());
        assertNotNull(retrieved);
        assertEquals(regressionModel.getModelName(), retrieved.getName());
        assertEquals(MINING_FUNCTION.byName(regressionModel.getMiningFunction().value()), retrieved.getMiningFunction());
        assertEquals(miningFields.get(0).getName().getValue(), retrieved.getTargetField());
        final KiePMMLRegressionTable regressionTable = retrieved.getRegressionTable();
        assertNotNull(regressionTable);
        assertTrue(regressionTable instanceof KiePMMLRegressionClassificationTable);
        evaluateCategoricalRegressionTable((KiePMMLRegressionClassificationTable) regressionTable);
    }

    @Test
    public void getKiePMMLRegressionModelSourcesMap() throws IOException {
        Map<String, String> retrieved = KiePMMLRegressionModelFactory.getKiePMMLRegressionModelSourcesMap(dataDictionary, transformationDictionary, regressionModel, PACKAGE_NAME);
        assertNotNull(retrieved);
        int expectedSize = regressionTables.size()
                + 2; // One for classification and one for the whole model
        assertEquals(expectedSize, retrieved.size());
    }

    @Test
    public void getRegressionTablesMap() throws IOException {
        String targetFieldName = "targetFieldName";
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionModelFactory
                .getRegressionTablesMap(dataDictionary,
                                        regressionModel,
                                        targetFieldName,
                                        Collections.emptyList(),
                                        PACKAGE_NAME);
        int expectedSize = regressionTables.size() + 1; // One for classification
        assertEquals(expectedSize, retrieved.size());
        final Collection<KiePMMLTableSourceCategory> values = retrieved.values();
        regressionTables.forEach(regressionTable ->
                                         assertTrue(values.stream().anyMatch(kiePMMLTableSourceCategory -> kiePMMLTableSourceCategory.getCategory().equals(regressionTable.getTargetCategory()))));

    }

    @Test
    public void setConstructor() {
        String nestedTable = "NestedTable";
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(regressionModel.getMiningFunction().value());
        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        KiePMMLRegressionModelFactory.setConstructor(regressionModel,
                                                     dataDictionary,
                                                     transformationDictionary,
                                                     modelTemplate,
                                                     nestedTable);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", regressionModel.getModelName())));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetMiningField.getName().getValue()));
        assignExpressionMap.put("miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL", new NameExpr(PMML_MODEL.class.getName() + "." + PMML_MODEL.REGRESSION_MODEL.name()));
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(nestedTable);
        assignExpressionMap.put("regressionTable", objectCreationExpr);
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        assertTrue(commonEvaluateConstructor(constructorDeclaration, getSanitizedClassName(regressionModel.getModelName()), superInvocationExpressionsMap, assignExpressionMap));
    }

    @Test
    public void isRegression() {
        assertTrue(KiePMMLRegressionModelFactory.isRegression(MiningFunction.REGRESSION, null, null));
        assertTrue(KiePMMLRegressionModelFactory.isRegression(MiningFunction.REGRESSION, "TARGET", OpType.CONTINUOUS));
        assertFalse(KiePMMLRegressionModelFactory.isRegression(MiningFunction.REGRESSION, "TARGET", OpType.CATEGORICAL));
        assertFalse(KiePMMLRegressionModelFactory.isRegression(MiningFunction.CLASSIFICATION, null, null));
    }

    private void evaluateCategoricalRegressionTable(KiePMMLRegressionClassificationTable regressionTable) {
        assertEquals(REGRESSION_NORMALIZATION_METHOD.byName(regressionModel.getNormalizationMethod().value()), regressionTable.getRegressionNormalizationMethod());
        assertEquals(OP_TYPE.CATEGORICAL, regressionTable.getOpType());
        final Map<String, KiePMMLRegressionTable> categoryTableMap = regressionTable.getCategoryTableMap();
        for (RegressionTable originalRegressionTable : regressionTables) {
            assertTrue(categoryTableMap.containsKey(originalRegressionTable.getTargetCategory().toString()));
            evaluateRegressionTable(categoryTableMap.get(originalRegressionTable.getTargetCategory().toString()), originalRegressionTable);
        }
    }

    private void evaluateRegressionTable(KiePMMLRegressionTable regressionTable, RegressionTable originalRegressionTable) {
        assertEquals(originalRegressionTable.getIntercept(), regressionTable.getIntercept());
        final Map<String, SerializableFunction<Double, Double>> numericFunctionMap = regressionTable.getNumericFunctionMap();
        for (NumericPredictor numericPredictor : originalRegressionTable.getNumericPredictors()) {
            assertTrue(numericFunctionMap.containsKey(numericPredictor.getName().getValue()));
        }
        final Map<String, SerializableFunction<Object, Double>> categoricalFunctionMap = regressionTable.getCategoricalFunctionMap();
        for (CategoricalPredictor categoricalPredictor : originalRegressionTable.getCategoricalPredictors()) {
            assertTrue(categoricalFunctionMap.containsKey(categoricalPredictor.getName().getValue()));
        }
        final Map<String, SerializableFunction<Map<String, Object>, Double>> predictorTermsFunctionMap = regressionTable.getPredictorTermsFunctionMap();
        for (PredictorTerm predictorTerm : originalRegressionTable.getPredictorTerms()) {
            assertTrue(predictorTermsFunctionMap.containsKey(predictorTerm.getName().getValue()));
        }
    }
}