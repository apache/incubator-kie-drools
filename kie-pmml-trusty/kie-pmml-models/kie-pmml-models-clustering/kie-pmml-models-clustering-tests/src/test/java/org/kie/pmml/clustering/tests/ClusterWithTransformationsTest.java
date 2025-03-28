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
package org.kie.pmml.clustering.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ClusterWithTransformationsTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "ClusterWithTransformations";
    private static final String MODEL_NAME = "ClusterWithTransformations";
    private static final String TARGET_FIELD = "class";
    private static final String OUT_NORMCONTINUOUS_FIELD = "out_normcontinuous_field";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";
    private static final String OUT_TEXT_INDEX_NORMALIZATION_FIELD = "out_text_index_normalization_field";
    private static final String TEXT_INPUT = "Testing the app for a few days convinced me the interfaces are " +
            "excellent!";

    private static PMMLRuntime pmmlRuntime;

    private double sepalLength;
    private double sepalWidth;
    private double petalLength;
    private double petalWidth;
    private String irisClass;
    private double outNormcontinuousField;

    public void initClusterWithTransformationsTest(double sepalLength, double sepalWidth, double petalLength,
                                          double petalWidth, String irisClass, double outNormcontinuousField) {
        this.sepalLength = sepalLength;
        this.sepalWidth = sepalWidth;
        this.petalLength = petalLength;
        this.petalWidth = petalWidth;
        this.irisClass = irisClass;
        this.outNormcontinuousField = outNormcontinuousField;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4.4, 3.0, 1.3, 0.2, "3", 4.966666666666667},
                {5.0, 3.3, 1.4, 0.2, "3", 5.433333333333334},
                {7.0, 3.2, 4.7, 1.4, "2", 6.950000000000001},
                {5.7, 2.8, 4.1, 1.3, "4", 5.937500000000001},
                {6.3, 3.3, 6.0, 2.5, "1", 6.1625},
                {6.7, 3.0, 5.2, 2.3, "1", 6.575}
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testClusterWithTransformations(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisClass, double outNormcontinuousField) throws Exception {
        initClusterWithTransformationsTest(sepalLength, sepalWidth, petalLength, petalWidth, irisClass, outNormcontinuousField);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("sepal_length", sepalLength);
        inputData.put("sepal_width", sepalWidth);
        inputData.put("petal_length", petalLength);
        inputData.put("petal_width", petalWidth);
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", 34);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

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
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isEqualTo(1.0);
    }

    @MethodSource("data")
    @ParameterizedTest
    void testClusterWithTransformationsWithoutRequired(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisClass, double outNormcontinuousField) {
        initClusterWithTransformationsTest(sepalLength, sepalWidth, petalLength, petalWidth, irisClass, outNormcontinuousField);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("sepal_length", sepalLength);
            inputData.put("sepal_width", sepalWidth);
            inputData.put("petal_length", petalLength);
            inputData.put("petal_width", petalWidth);
            inputData.put("text_input", TEXT_INPUT);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testClusterWithTransformationsConvertible(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisClass, double outNormcontinuousField) {
        initClusterWithTransformationsTest(sepalLength, sepalWidth, petalLength, petalWidth, irisClass, outNormcontinuousField);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("sepal_length", String.valueOf(sepalLength));
        inputData.put("sepal_width", String.valueOf(sepalWidth));
        inputData.put("petal_length", String.valueOf(petalLength));
        inputData.put("petal_width", String.valueOf(petalWidth));
        inputData.put("text_input", TEXT_INPUT);
        inputData.put("input3", "34");
        assertThat(evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME)).isNotNull();
    }

    @MethodSource("data")
    @ParameterizedTest
    void testClusterWithTransformationsNotConvertible(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisClass, double outNormcontinuousField) {
        initClusterWithTransformationsTest(sepalLength, sepalWidth, petalLength, petalWidth, irisClass, outNormcontinuousField);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("sepal_length", sepalLength);
            inputData.put("sepal_width", sepalWidth);
            inputData.put("petal_length", petalLength);
            inputData.put("petal_width", petalWidth);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", true);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testClusterWithTransformationsInvalidValue(double sepalLength, double sepalWidth, double petalLength, double petalWidth, String irisClass, double outNormcontinuousField) {
        initClusterWithTransformationsTest(sepalLength, sepalWidth, petalLength, petalWidth, irisClass, outNormcontinuousField);
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            final Map<String, Object> inputData = new HashMap<>();
            inputData.put("sepal_length", sepalLength);
            inputData.put("sepal_width", sepalWidth);
            inputData.put("petal_length", petalLength);
            inputData.put("petal_width", petalWidth);
            inputData.put("text_input", TEXT_INPUT);
            inputData.put("input3", 4.1);
            evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);
        });
    }
}
