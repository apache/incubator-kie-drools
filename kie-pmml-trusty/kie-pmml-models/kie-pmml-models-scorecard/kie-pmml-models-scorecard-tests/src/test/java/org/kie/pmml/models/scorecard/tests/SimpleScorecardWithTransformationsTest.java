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
package org.kie.pmml.models.scorecard.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleScorecardWithTransformationsTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "SimpleScorecardWithTransformations";
    private static final String MODEL_NAME = "SimpleScorecardWithTransformations";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";
    private static final String OUT_DER_INPUT1 = "out_der_input1";
    private static final String OUT_DER_INPUT2 = "out_der_input2";
    private static final String OUT_DER_CONSTANT = "out_der_constant";
    private static final String CONSTANT = "constant";
    private static final String OUT_NORMDISCRETE_FIELD = "out_normdiscrete_field";
    private static final String OUT_DISCRETIZE_FIELD = "out_discretize_field";
    private static final String OUT_MAPVALUED_FIELD = "out_mapvalued_field";
    private static final String OUT_TEXT_INDEX_NORMALIZATION_FIELD = "out_text_index_normalization_field";
    private static final String TEXT_INPUT = "Testing the app for a few days convinced me the interfaces are " +
            "excellent!";

    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private double score;
    private String reasonCode1;
    private String reasonCode2;

    public void initSimpleScorecardWithTransformationsTest(double input1, double input2, double score, String reasonCode1, String reasonCode2) {
        this.input1 = input1;
        this.input2 = input2;
        this.score = score;
        this.reasonCode1 = reasonCode1;
        this.reasonCode2 = reasonCode2;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {5, 5, 25, "Input1ReasonCode", null},
                {5, -10, -15, "Input1ReasonCode", "Input2ReasonCode"},
                {20.5, 4, 87, null, null},
                {23.5, -12, 47, "Input2ReasonCode", null},
                {10, -5, -15, "Input1ReasonCode", "Input2ReasonCode"},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSimpleScorecard(double input1, double input2, double score, String reasonCode1, String reasonCode2) throws Exception {
        initSimpleScorecardWithTransformationsTest(input1, input2, score, reasonCode1, reasonCode2);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        inputData.put("text_input", TEXT_INPUT);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);

        assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_INPUT1)).isEqualTo(input1);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_INPUT2)).isEqualTo(input2);
        assertThat(pmml4Result.getResultVariables().get(OUT_DER_CONSTANT)).isEqualTo(CONSTANT);
        if (reasonCode1 != null) {
            assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isNotNull();
            if (reasonCode1.equals("Input1ReasonCode")) {
                assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("1.0");
            } else {
                assertThat(pmml4Result.getResultVariables().get(OUT_NORMDISCRETE_FIELD)).isEqualTo("0.0");
            }
        }
        assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isNotNull();
        if (input1 > 4.2 && input1 < 9.8) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("abc");
        } else if (input1 >= 15.4 && input1 < 22.1) {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("def");
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_DISCRETIZE_FIELD)).isEqualTo("defaultValue");
        }
        if (reasonCode1 == null) {
            assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isNull();
        } else {
            assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isNotNull();
            String expected;
            switch (reasonCode1) {
                case "Input1ReasonCode":
                    expected = "RES-1";
                    break;
                case "Input2ReasonCode":
                    expected = "RES-2";
                    break;
                default:
                    throw new Exception("Unexpected reasonCode1 " + reasonCode1);
            }
            assertThat(pmml4Result.getResultVariables().get(OUT_MAPVALUED_FIELD)).isEqualTo(expected);
            assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isNotNull();
            assertThat(pmml4Result.getResultVariables().get(OUT_TEXT_INDEX_NORMALIZATION_FIELD)).isEqualTo(1.0);
        }
    }
}
