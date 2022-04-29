package org.kie.pmml.models.clustering.compiler.factories;/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompareFunction;
import org.dmg.pmml.ComparisonMeasure;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Euclidean;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.Cluster;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
import org.dmg.pmml.clustering.MissingValueWeights;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.clustering.compiler.dto.ClusteringCompilationDTO;
import org.kie.pmml.models.clustering.model.KiePMMLAggregateFunction;
import org.kie.pmml.models.clustering.model.KiePMMLCluster;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringField;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLCompareFunction;
import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;
import org.kie.pmml.models.clustering.model.KiePMMLMissingValueWeights;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getArray;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getClusteringModel;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getMiningSchema;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomCluster;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomClusteringField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomEnum;
import static org.kie.pmml.compiler.api.utils.ModelUtils.getObjectsFromArray;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.AGGREGATE_FN_MAP;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringModelFactory.KIE_PMML_CLUSTERING_MODEL_TEMPLATE;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringModelFactory.KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLClusteringModelFactoryTest {

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;
    private static final String TEST_01_SOURCE = "KiePMMLClusteringModelFactoryTest_01.txt";

    private static final String modelName = "firstModel";
    private static List<ClusteringField> clusteringFields;
    private static List<Cluster> clusters;
    private static List<DataField> dataFields;
    private static List<MiningField> miningFields;
    private static MiningField targetMiningField;
    private static DataDictionary dataDictionary;
    private static TransformationDictionary transformationDictionary;
    private static MiningSchema miningSchema;
    private static ClusteringModel clusteringModel;
    private static PMML pmml;

    @BeforeClass
    public static void setup() {
        Set<String> fieldNames = new HashSet<>();
        clusteringFields = new ArrayList<>();
        clusters = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            ClusteringField clusteringField = getRandomClusteringField();
            clusteringFields.add(clusteringField);
            fieldNames.add(clusteringField.getField().getValue());
            clusters.add(getRandomCluster());
        });

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
        clusteringModel = getClusteringModel(modelName, MiningFunction.CLUSTERING, miningSchema, clusteringFields,
                                             clusters);
        COMPILATION_UNIT = getFromFileName(KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_CLUSTERING_MODEL_TEMPLATE).get();
        pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.setTransformationDictionary(transformationDictionary);
        pmml.addModels(clusteringModel);
    }

    @Test
    public void getKiePMMLClusteringModel() {
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new HasClassLoaderMock());
        KiePMMLClusteringModel retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLClusteringModel(ClusteringCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        assertEquals(clusteringModel.getModelName(), retrieved.getName());
        assertEquals(clusteringModel.getModelClass().value(), retrieved.getModelClass().getName());
        List<KiePMMLCluster> retrievedClusters = retrieved.getClusters();
        assertEquals(clusteringModel.getClusters().size(), retrievedClusters.size());
        IntStream.range(0, clusteringModel.getClusters().size()).forEach(i -> commonEvaluateKiePMMLCluster(retrievedClusters.get(i), clusteringModel.getClusters().get(i)));
        List<KiePMMLClusteringField> retrievedClusteringFields = retrieved.getClusteringFields();
        assertEquals(clusteringModel.getClusters().size(), retrievedClusters.size());
        IntStream.range(0, clusteringModel.getClusters().size()).forEach(i -> commonEvaluateKiePMMLCluster(retrievedClusters.get(i), clusteringModel.getClusters().get(i)));
        assertEquals(clusteringModel.getClusteringFields().size(), retrievedClusteringFields.size());
        IntStream.range(0, clusteringModel.getClusteringFields().size()).forEach(i -> commonEvaluateKiePMMLClusteringField(retrievedClusteringFields.get(i), clusteringModel.getClusteringFields().get(i)));
        commonEvaluateKiePMMLComparisonMeasure(retrieved.getComparisonMeasure(),
                                               clusteringModel.getComparisonMeasure());
        commonEvaluateKiePMMLMissingValueWeights(retrieved.getMissingValueWeights(),
                                                 clusteringModel.getMissingValueWeights());
    }

    @Test
    public void getKiePMMLClusteringModelSourcesMap() {
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new HasClassLoaderMock());
        Map<String, String> retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLClusteringModelSourcesMap(ClusteringCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        assertEquals(1, retrieved.size());
    }

    @Test
    public void getKiePMMLCluster() {
        Cluster cluster = new Cluster();
        cluster.setId("ID");
        cluster.setName("NAME");
        final Random random = new Random();
        final List<Double> doubleValues =
                IntStream.range(0, 3).mapToObj(i -> random.nextDouble()).collect(Collectors.toList());
        final List<String> values = doubleValues.stream().map(String::valueOf).collect(Collectors.toList());
        Array array = getArray(Array.Type.REAL, values);
        cluster.setArray(array);
        KiePMMLCluster retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLCluster(cluster);
        commonEvaluateKiePMMLCluster(retrieved, cluster);
    }

    @Test
    public void getKiePMMLClusteringField() {
        ClusteringField clusteringField = new ClusteringField();
        final Random random = new Random();
        clusteringField.setField(FieldName.create("TEXT"));
        clusteringField.setFieldWeight(random.nextDouble());
        clusteringField.setCenterField(getRandomEnum(ClusteringField.CenterField.values()));
        clusteringField.setCompareFunction(getRandomEnum(CompareFunction.values()));
        KiePMMLClusteringField retrieved = KiePMMLClusteringModelFactory.getKiePMMLClusteringField(clusteringField);
        commonEvaluateKiePMMLClusteringField(retrieved, clusteringField);
    }

    @Test
    public void getKiePMMLComparisonMeasure() {
        ComparisonMeasure comparisonMeasure = new ComparisonMeasure();
        getRandomEnum(ComparisonMeasure.Kind.values());
        comparisonMeasure.setKind(getRandomEnum(ComparisonMeasure.Kind.values()));
        comparisonMeasure.setCompareFunction(getRandomEnum(CompareFunction.values()));
        Random random = new Random();
        comparisonMeasure.setMinimum(random.nextInt(10));
        comparisonMeasure.setMaximum(comparisonMeasure.getMinimum().intValue() + random.nextInt(10));
        comparisonMeasure.setMeasure(new Euclidean());
        KiePMMLComparisonMeasure retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLComparisonMeasure(comparisonMeasure);
        assertEquals(KiePMMLAggregateFunction.EUCLIDEAN, retrieved.getAggregateFunction());
        commonEvaluateKiePMMLComparisonMeasure(retrieved, comparisonMeasure);
    }

    @Test
    public void getKiePMMLMissingValueWeights() {
        assertNull(KiePMMLClusteringModelFactory.getKiePMMLMissingValueWeights(null));
        KiePMMLMissingValueWeights retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLMissingValueWeights(new MissingValueWeights());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getValues()).isNotNull();
        assertTrue(retrieved.getValues().isEmpty());
        MissingValueWeights missingValueWeights = new MissingValueWeights();
        final Random random = new Random();
        final List<Double> doubleValues =
                IntStream.range(0, 3).mapToObj(i -> random.nextDouble()).collect(Collectors.toList());
        final List<String> values = doubleValues.stream().map(String::valueOf).collect(Collectors.toList());
        Array array = getArray(Array.Type.REAL, values);
        missingValueWeights.setArray(array);
        retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLMissingValueWeights(missingValueWeights);
        commonEvaluateKiePMMLMissingValueWeights(retrieved, missingValueWeights);
    }

    @Test
    public void setStaticGetter() throws IOException {

        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new HasClassLoaderMock());
        String expectedModelClass =
                KiePMMLClusteringModel.ModelClass.class.getCanonicalName() + "." + clusteringModel.getModelClass().name();
        ComparisonMeasure comparisonMeasure = clusteringModel.getComparisonMeasure();
        String expectedKind =
                KiePMMLComparisonMeasure.Kind.class.getCanonicalName() + "." + comparisonMeasure.getKind().name();
        String expectedAggregateFunction =
                KiePMMLAggregateFunction.class.getCanonicalName() + "." + AGGREGATE_FN_MAP.get(comparisonMeasure.getMeasure().getClass()).name();

        String expectedCompareFunction =
                KiePMMLCompareFunction.class.getCanonicalName() + "." + comparisonMeasure.getCompareFunction().name();
        String expectedTargetField = targetMiningField.getName().getValue();

        KiePMMLClusteringModelFactory.setStaticGetter(compilationDTO,
                                                      modelTemplate);

        MethodDeclaration retrieved = modelTemplate.getMethodsByName(GET_MODEL).get(0);
        String text = String.format(getFileContent(TEST_01_SOURCE),
                                    expectedModelClass,
                                    expectedKind,
                                    expectedAggregateFunction,
                                    expectedCompareFunction,
                                    expectedTargetField);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
    }

    private void commonEvaluateKiePMMLCluster(KiePMMLCluster retrieved, Cluster cluster) {
        assertThat(retrieved).isNotNull();
        assertTrue(retrieved.getId().isPresent());
        assertEquals(cluster.getId(), retrieved.getId().get());
        assertTrue(retrieved.getName().isPresent());
        assertEquals(cluster.getName(), retrieved.getName().get());
        commonEvaluateDoubles(retrieved.getValues(), cluster.getArray());
    }

    private void commonEvaluateKiePMMLClusteringField(KiePMMLClusteringField retrieved,
                                                      ClusteringField clusteringField) {
        assertThat(retrieved).isNotNull();
        boolean isCenterField = clusteringField.getCenterField() == ClusteringField.CenterField.TRUE;
        assertEquals(clusteringField.getField().getValue(), retrieved.getField());
        assertEquals(clusteringField.getFieldWeight(), retrieved.getFieldWeight());
        assertEquals(isCenterField, retrieved.getCenterField());
        assertTrue(retrieved.getCompareFunction().isPresent());
        assertEquals(clusteringField.getCompareFunction().value(), retrieved.getCompareFunction().get().getName());
    }

    private void commonEvaluateKiePMMLComparisonMeasure(KiePMMLComparisonMeasure retrieved,
                                                        ComparisonMeasure comparisonMeasure) {
        assertEquals(comparisonMeasure.getKind().value(), retrieved.getKind().getName());
        assertEquals(comparisonMeasure.getCompareFunction().value(), retrieved.getCompareFunction().getName());
    }

    private void commonEvaluateKiePMMLMissingValueWeights(KiePMMLMissingValueWeights retrieved,
                                                          MissingValueWeights missingValueWeights) {
        assertThat(retrieved).isNotNull();
        commonEvaluateDoubles(retrieved.getValues(), missingValueWeights.getArray());
    }

    private void commonEvaluateDoubles(final List<Double> retrievedValues, final Array array) {
        if (array != null) {
            final List<Object> doubleValues = getObjectsFromArray(array);
            assertThat(retrievedValues).isNotNull();
            assertEquals(doubleValues.size(), retrievedValues.size());
            IntStream.range(0, doubleValues.size()).forEach(i -> {
                assertEquals(doubleValues.get(i), retrievedValues.get(i));
            });
        }
    }
}