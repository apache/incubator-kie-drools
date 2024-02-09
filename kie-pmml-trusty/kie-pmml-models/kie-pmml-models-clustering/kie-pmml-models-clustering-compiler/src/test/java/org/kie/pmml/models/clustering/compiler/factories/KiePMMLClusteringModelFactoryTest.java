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
package org.kie.pmml.models.clustering.compiler.factories;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
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
import static org.drools.util.FileUtils.getFileContent;

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

    @BeforeAll
    public static void setup() {
        Set<String> fieldNames = new HashSet<>();
        clusteringFields = new ArrayList<>();
        clusters = new ArrayList<>();
        IntStream.range(0, 3).forEach(i -> {
            ClusteringField clusteringField = getRandomClusteringField();
            clusteringFields.add(clusteringField);
            fieldNames.add(clusteringField.getField());
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
    void getKiePMMLClusteringModel() {
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "fileName");
        KiePMMLClusteringModel retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLClusteringModel(ClusteringCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(clusteringModel.getModelName());
        assertThat(retrieved.getModelClass().getName()).isEqualTo(clusteringModel.getModelClass().value());
        List<KiePMMLCluster> retrievedClusters = retrieved.getClusters();
        assertThat(retrievedClusters).hasSameSizeAs(clusteringModel.getClusters());
        IntStream.range(0, clusteringModel.getClusters().size()).forEach(i -> commonEvaluateKiePMMLCluster(retrievedClusters.get(i), clusteringModel.getClusters().get(i)));
        List<KiePMMLClusteringField> retrievedClusteringFields = retrieved.getClusteringFields();
        assertThat(retrievedClusters).hasSameSizeAs(clusteringModel.getClusters());
        IntStream.range(0, clusteringModel.getClusters().size()).forEach(i -> commonEvaluateKiePMMLCluster(retrievedClusters.get(i), clusteringModel.getClusters().get(i)));
        assertThat(retrievedClusteringFields).hasSameSizeAs(clusteringModel.getClusteringFields());
        IntStream.range(0, clusteringModel.getClusteringFields().size()).forEach(i -> commonEvaluateKiePMMLClusteringField(retrievedClusteringFields.get(i), clusteringModel.getClusteringFields().get(i)));
        commonEvaluateKiePMMLComparisonMeasure(retrieved.getComparisonMeasure(),
                clusteringModel.getComparisonMeasure());
        commonEvaluateKiePMMLMissingValueWeights(retrieved.getMissingValueWeights(),
                clusteringModel.getMissingValueWeights());
    }

    @Test
    void getKiePMMLClusteringModelSourcesMap() {
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new PMMLCompilationContextMock(), "fileName");
        Map<String, String> retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLClusteringModelSourcesMap(ClusteringCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSize(1);
    }

    @Test
    void getKiePMMLCluster() {
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
    void getKiePMMLClusteringField() {
        ClusteringField clusteringField = new ClusteringField();
        final Random random = new Random();
        clusteringField.setField("TEXT");
        clusteringField.setFieldWeight(random.nextDouble());
        clusteringField.setCenterField(getRandomEnum(ClusteringField.CenterField.values()));
        clusteringField.setCompareFunction(getRandomEnum(CompareFunction.values()));
        KiePMMLClusteringField retrieved = KiePMMLClusteringModelFactory.getKiePMMLClusteringField(clusteringField);
        commonEvaluateKiePMMLClusteringField(retrieved, clusteringField);
    }

    @Test
    void getKiePMMLComparisonMeasure() {
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
        assertThat(retrieved.getAggregateFunction()).isEqualTo(KiePMMLAggregateFunction.EUCLIDEAN);
        commonEvaluateKiePMMLComparisonMeasure(retrieved, comparisonMeasure);
    }

    @Test
    void getKiePMMLMissingValueWeights() {
        assertThat(KiePMMLClusteringModelFactory.getKiePMMLMissingValueWeights(null)).isNull();
        KiePMMLMissingValueWeights retrieved =
                KiePMMLClusteringModelFactory.getKiePMMLMissingValueWeights(new MissingValueWeights());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getValues()).isNotNull();
        assertThat(retrieved.getValues()).isEmpty();
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
    void setStaticGetter() throws IOException {

        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        final CommonCompilationDTO<ClusteringModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       clusteringModel,
                                                                       new PMMLCompilationContextMock(), "fileName"); // fileName hardcoded inside TEST_01_SOURCE
        String expectedModelClass =
                KiePMMLClusteringModel.ModelClass.class.getCanonicalName() + "." + clusteringModel.getModelClass().name();
        ComparisonMeasure comparisonMeasure = clusteringModel.getComparisonMeasure();
        String expectedKind =
                KiePMMLComparisonMeasure.Kind.class.getCanonicalName() + "." + comparisonMeasure.getKind().name();
        String expectedAggregateFunction =
                KiePMMLAggregateFunction.class.getCanonicalName() + "." + AGGREGATE_FN_MAP.get(comparisonMeasure.getMeasure().getClass()).name();

        String expectedCompareFunction =
                KiePMMLCompareFunction.class.getCanonicalName() + "." + comparisonMeasure.getCompareFunction().name();
        String expectedTargetField =targetMiningField.getName();

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
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    private void commonEvaluateKiePMMLCluster(KiePMMLCluster retrieved, Cluster cluster) {
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isPresent();
        assertThat(retrieved.getId().get()).isEqualTo(cluster.getId());
        assertThat(retrieved.getName()).isPresent();
        assertThat(retrieved.getName().get()).isEqualTo(cluster.getName());
        commonEvaluateDoubles(retrieved.getValues(), cluster.getArray());
    }

    private void commonEvaluateKiePMMLClusteringField(KiePMMLClusteringField retrieved,
                                                      ClusteringField clusteringField) {
        assertThat(retrieved).isNotNull();
        boolean isCenterField = clusteringField.getCenterField() == ClusteringField.CenterField.TRUE;
        assertThat(retrieved.getField()).isEqualTo(clusteringField.getField());
        assertThat(retrieved.getFieldWeight()).isEqualTo(clusteringField.getFieldWeight());
        assertThat(retrieved.getCenterField()).isEqualTo(isCenterField);
        assertThat(retrieved.getCompareFunction()).isPresent();
        assertThat(retrieved.getCompareFunction().get().getName()).isEqualTo(clusteringField.getCompareFunction().value());
    }

    private void commonEvaluateKiePMMLComparisonMeasure(KiePMMLComparisonMeasure retrieved,
                                                        ComparisonMeasure comparisonMeasure) {
        assertThat(retrieved.getKind().getName()).isEqualTo(comparisonMeasure.getKind().value());
        assertThat(retrieved.getCompareFunction().getName()).isEqualTo(comparisonMeasure.getCompareFunction().value());
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
            assertThat(retrievedValues).hasSameSizeAs(doubleValues);
            IntStream.range(0, doubleValues.size()).forEach(i -> {
                assertThat(retrievedValues.get(i)).isEqualTo(doubleValues.get(i));
            });
        }
    }
}