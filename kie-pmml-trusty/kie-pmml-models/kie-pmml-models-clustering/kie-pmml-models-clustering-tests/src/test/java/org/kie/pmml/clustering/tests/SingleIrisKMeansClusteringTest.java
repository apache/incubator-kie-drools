/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.clustering.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class SingleIrisKMeansClusteringTest extends AbstractPMMLTest {

    private static final String MODEL_NAME = "SingleIrisKMeansClustering";
    private static final String TARGET_FIELD = "class";

    private static final String OUT_NORMCONTINUOUS_FIELD = "out_normcontinuous_field";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";

    private static final String PREDICTED_CLUSTER_NAME_FIELD = "predicted_cluster_name";
    private static final String PREDICTED_CLUSTER_INDEX_FIELD = "predicted_cluster_index";
    private static final String PREDICTED_CLUSTER_AFFINITY_FIELD = "predicted_cluster_affinity";

    protected PMMLRuntime pmmlRuntime;

    private final double sepalLength;
    private final double sepalWidth;
    private final double petalLength;
    private final double petalWidth;
    private final double outNormcontinuousField;
    private final String predictedDisplayValue;
    private final int predictedEntityId;
    private final double predictedAffinity;
    private final String irisClass;
    private final String modelFileName;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4.4, 3.0, 1.3, 0.2, 4.966666666666667, "cluster_3", 3, 0.570791999999999300, "3", "SingleIrisKMeansClustering.pmml"},
                {4.4, 3.0, 1.3, 0.2, 4.966666666666667, "cluster_3", 3, 0.570791999999999300, "C_THREE", "SingleIrisKMeansClustering_id.pmml"},
                {5.0, 3.3, 1.4, 0.2, 5.433333333333334, "cluster_3", 3, 0.019992000000000173, "3", "SingleIrisKMeansClustering.pmml"},
                {5.0, 3.3, 1.4, 0.2, 5.433333333333334, "cluster_3", 3, 0.019992000000000173, "C_THREE", "SingleIrisKMeansClustering_id.pmml"},
                {7.0, 3.2, 4.7, 1.4, 6.950000000000001, "cluster_2", 2, 0.760178465199283600, "2", "SingleIrisKMeansClustering.pmml"},
                {7.0, 3.2, 4.7, 1.4, 6.950000000000001, "cluster_2", 2, 0.760178465199283600, "C_TWO", "SingleIrisKMeansClustering_id.pmml"},
                {5.7, 2.8, 4.1, 1.3, 5.937500000000001, "cluster_4", 4, 0.092633744855966940, "4", "SingleIrisKMeansClustering.pmml"},
                {5.7, 2.8, 4.1, 1.3, 5.937500000000001, "cluster_4", 4, 0.092633744855966940, "C_FOUR", "SingleIrisKMeansClustering_id.pmml"},
                {6.3, 3.3, 6.0, 2.5, 6.162500000000000, "cluster_1", 1, 0.574580078125001700, "1", "SingleIrisKMeansClustering.pmml"},
                {6.3, 3.3, 6.0, 2.5, 6.162500000000000, "cluster_1", 1, 0.574580078125001700, "C_ONE", "SingleIrisKMeansClustering_id.pmml"},
                {6.7, 3.0, 5.2, 2.3, 6.575000000000000, "cluster_1", 1, 0.502080078124998400, "1", "SingleIrisKMeansClustering.pmml"},
                {6.7, 3.0, 5.2, 2.3, 6.575000000000000, "cluster_1", 1, 0.502080078124998400, "C_ONE", "SingleIrisKMeansClustering_id.pmml"}
        });
    }

    public SingleIrisKMeansClusteringTest(
            double sepalLength, double sepalWidth, double petalLength, double petalWidth, double outNormcontinuousField,
            String predictedDisplayValue, int predictedEntityId, double predictedAffinity, String irisClass, String modelFileName) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.irisClass = irisClass;
        this.outNormcontinuousField = outNormcontinuousField;
        this.predictedDisplayValue = predictedDisplayValue;
        this.predictedEntityId = predictedEntityId;
        this.predictedAffinity = predictedAffinity;
        this.modelFileName = modelFileName;
    }

    @Before
    public void setupClass() {
        pmmlRuntime = getPMMLRuntime(modelFileName);
    }

    @Test
    public void testLogisticRegressionIrisData() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("sepal_length", sepalLength);
        inputData.put("sepal_width", sepalWidth);
        inputData.put("petal_length", petalLength);
        inputData.put("petal_width", petalWidth);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(irisClass);

        assertThat(pmml4Result.getResultVariables().get(OUT_NORMCONTINUOUS_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_NORMCONTINUOUS_FIELD)).isEqualTo(outNormcontinuousField);
        assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isNotNull();
        if (irisClass.equals("1")) {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo(1.0);
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo(0.0);
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isNotNull();
        if (sepalLength > 4.7 && sepalLength < 5.2) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("abc");
        } else if (sepalLength >= 5.6 && sepalLength < 5.9) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("def");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("defaultValue");
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isNotNull();
        String expected;
        switch (irisClass) {
            case "1":
            case "C_ONE":
                expected = "virginica";
                break;
            case "2":
            case "C_TWO":
                expected = "versicolor";
                break;
            case "3":
            case "C_THREE":
                expected = "setosa";
                break;
            case "4":
            case "C_FOUR":
                expected = "unknown";
                break;
            default:
                throw new Exception("Unexpected irisClass " + irisClass);
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isEqualTo(expected);

        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_NAME_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_NAME_FIELD)).isEqualTo(predictedDisplayValue);

        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_INDEX_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_INDEX_FIELD)).isEqualTo(predictedEntityId);

        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_AFFINITY_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(PREDICTED_CLUSTER_AFFINITY_FIELD)).isEqualTo(predictedAffinity);
    }
}
