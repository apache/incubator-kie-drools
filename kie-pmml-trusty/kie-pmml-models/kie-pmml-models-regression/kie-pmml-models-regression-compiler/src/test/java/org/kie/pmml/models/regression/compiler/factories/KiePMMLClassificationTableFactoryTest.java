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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.KiePMMLClassificationTable;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLClassificationTableFactory.GETKIEPMML_TABLE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLClassificationTableFactory.KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLClassificationTableFactory.KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE_JAVA;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLClassificationTableFactory.SUPPORTED_NORMALIZATION_METHODS;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLClassificationTableFactory.UNSUPPORTED_NORMALIZATION_METHODS;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLClassificationTableFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private static final String TEST_01_SOURCE = "KiePMMLClassificationTableFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLClassificationTableFactoryTest_02.txt";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;
    private static MethodDeclaration STATIC_GETTER_METHOD;

    @BeforeClass
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE).get();
        STATIC_GETTER_METHOD = MODEL_TEMPLATE.getMethodsByName(GETKIEPMML_TABLE).get(0);
    }

    @Test
    public void getClassificationTable() {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTableProf, regressionTableCler);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField targetMiningField = new MiningField();
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        targetMiningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(targetMiningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());
        KiePMMLClassificationTable retrieved =
                KiePMMLClassificationTableFactory.getClassificationTable(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertEquals(regressionModel.getRegressionTables().size(), retrieved.getCategoryTableMap().size());
        regressionModel.getRegressionTables().forEach(regressionTable ->
                                                              assertTrue(retrieved.getCategoryTableMap().containsKey(regressionTable.getTargetCategory().toString())));
        assertEquals(regressionModel.getNormalizationMethod().value(),
                     retrieved.getRegressionNormalizationMethod().getName());
        assertEquals(OP_TYPE.CATEGORICAL, retrieved.getOpType());
        boolean isBinary = regressionModel.getRegressionTables().size() == 2;
        assertEquals(isBinary, retrieved.isBinary());
        assertEquals(isBinary, retrieved.isBinary());
        assertEquals(targetMiningField.getName().getValue(), retrieved.getTargetField());
    }

    @Test
    public void getClassificationTableBuilders() {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTableProf, regressionTableCler);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());
        Map<String, KiePMMLTableSourceCategory> retrieved =
                KiePMMLClassificationTableFactory.getClassificationTableBuilders(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertEquals(3, retrieved.size());
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));

        Map<String, String> sources = retrieved.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                                                                                             stringKiePMMLTableSourceCategoryEntry -> stringKiePMMLTableSourceCategoryEntry.getValue().getSource()));
        commonValidateCompilation(sources);
    }

    @Test
    public void getClassificationTableBuilder() {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTableProf, regressionTableCler);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());
        final LinkedHashMap<String,
                KiePMMLTableSourceCategory> regressionTablesMap = new LinkedHashMap<>();

        regressionModel.getRegressionTables().forEach(regressionTable -> {
            String key =
                    compilationDTO.getPackageName() + "." + regressionTable.getTargetCategory().toString().toUpperCase();
            KiePMMLTableSourceCategory value = new KiePMMLTableSourceCategory("",
                                                                              regressionTable.getTargetCategory().toString());
            regressionTablesMap.put(key, value);
        });
        Map.Entry<String, String> retrieved =
                KiePMMLClassificationTableFactory.getClassificationTableBuilder(compilationDTO, regressionTablesMap);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void getProbabilityMapUnsupportedFunction() {
        KiePMMLClassificationTableFactory.UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            try {
                KiePMMLClassificationTableFactory.getProbabilityMapFunction(normalizationMethod, false);
            } catch (Throwable t) {
                assertTrue(t instanceof KiePMMLInternalException);
                String expected = String.format("Unsupported NormalizationMethod %s",
                                                normalizationMethod);
                assertEquals(expected, t.getMessage());
            }
            try {
                KiePMMLClassificationTableFactory.getProbabilityMapFunction(normalizationMethod, true);
            } catch (Throwable t) {
                assertTrue(t instanceof KiePMMLInternalException);
                String expected = String.format("Unsupported NormalizationMethod %s",
                                                normalizationMethod);
                assertEquals(expected, t.getMessage());
            }
        });
    }

    @Test
    public void getProbabilityMapSupportedFunction() {
        KiePMMLClassificationTableFactory.SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod ->
                                                                                          assertThat(KiePMMLClassificationTableFactory.getProbabilityMapFunction(normalizationMethod, false)).isNotNull());
        KiePMMLClassificationTableFactory.SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod ->
                                                                                          assertThat(KiePMMLClassificationTableFactory.getProbabilityMapFunction(normalizationMethod, true)).isNotNull());
    }

    @Test
    public void setStaticGetter() throws IOException {
        String variableName = "variableName";
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTableProf, regressionTableCler);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());

        final LinkedHashMap<String,
                KiePMMLTableSourceCategory> regressionTablesMap = new LinkedHashMap<>();

        regressionModel.getRegressionTables().forEach(regressionTable -> {
            String key = "defpack." + regressionTable.getTargetCategory().toString().toUpperCase();
            KiePMMLTableSourceCategory value = new KiePMMLTableSourceCategory("",
                                                                              regressionTable.getTargetCategory().toString());
            regressionTablesMap.put(key, value);
        });
        final MethodDeclaration staticGetterMethod = STATIC_GETTER_METHOD.clone();
        KiePMMLClassificationTableFactory.setStaticGetter(compilationDTO,
                                                          regressionTablesMap,
                                                          staticGetterMethod,
                                                          variableName);
        String text = getFileContent(TEST_02_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertTrue(JavaParserUtils.equalsNode(expected, staticGetterMethod));
    }

    @Test
    public void getProbabilityMapFunctionExpressionWithSupportedMethods() {
        SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            Expression retrieved =
                    KiePMMLClassificationTableFactory.getProbabilityMapFunctionExpression(normalizationMethod,
                                                                                          false);
            try {
                String text = getFileContent(TEST_01_SOURCE);
                Expression expected = JavaParserUtils.parseExpression(String.format(text,
                                                                                    normalizationMethod.name()));
                assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    public void getProbabilityMapFunctionExpressionWithUnSupportedMethods() {
        UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            try {
                KiePMMLClassificationTableFactory.getProbabilityMapFunctionExpression(normalizationMethod,
                                                                                      false);
                fail("Expecting KiePMMLInternalException with normalizationMethod " + normalizationMethod);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLInternalException);
            }
        });
    }

    @Test
    public void getProbabilityMapFunctionSupportedExpression() throws IOException {
        MethodReferenceExpr retrieved =
                KiePMMLClassificationTableFactory.getProbabilityMapFunctionSupportedExpression(RegressionModel.NormalizationMethod.CAUCHIT, true);
        String text = getFileContent(TEST_01_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text,
                                                                            RegressionModel.NormalizationMethod.CAUCHIT.name()));
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
    }

    private OutputField getOutputField(String name, ResultFeature resultFeature, String targetField) {
        OutputField toReturn = new OutputField();
        toReturn.setName(FieldName.create(name));
        toReturn.setResultFeature(resultFeature);
        if (targetField != null) {
            toReturn.setTargetField(FieldName.create(targetField));
        }
        return toReturn;
    }
}