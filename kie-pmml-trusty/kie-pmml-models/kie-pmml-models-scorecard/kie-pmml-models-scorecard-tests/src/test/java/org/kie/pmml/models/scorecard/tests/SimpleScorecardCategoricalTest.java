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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleScorecardCategoricalTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "Simple-Scorecard_Categorical";
    private static final String MODEL_NAME = "SimpleScorecardCategorical";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";
    private static final String[] CATEGORY = new String[]{"classA", "classB", "classC", "classD", "classE", "NA"};
    private static PMMLRuntime pmmlRuntime;

    private String input1;
    private String input2;
    private double score;
    private String reasonCode1;
    private String reasonCode2;

    public void initSimpleScorecardCategoricalTest(String input1, String input2, double score, String reasonCode1,
                                          String reasonCode2) {
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
                {"classA", "classB", 25, "Input1ReasonCode", null},
                {"classA", "classA", -15, "Input1ReasonCode", "Input2ReasonCode"},
                {"classB", "classB", 87, null, null},
                {"classB", "classA", 47, "Input2ReasonCode", null},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSimpleScorecardCategorical(String input1, String input2, double score, String reasonCode1, String reasonCode2) {
        initSimpleScorecardCategoricalTest(input1, input2, score, reasonCode1, reasonCode2);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSimpleScorecardCategoricalVerifyNoException(String input1, String input2, double score, String reasonCode1, String reasonCode2) {
        initSimpleScorecardCategoricalTest(input1, input2, score, reasonCode1, reasonCode2);
        getSamples().stream().map(sample -> evaluate(pmmlRuntime, sample, FILE_NAME_NO_SUFFIX,  MODEL_NAME))
                .forEach((x) -> assertThat(x).isNotNull());
    }

    @MethodSource("data")
    @ParameterizedTest
    void testSimpleScorecardCategoricalVerifyNoReasonCodeWithoutScore(String input1, String input2, double score, String reasonCode1, String reasonCode2) {
        initSimpleScorecardCategoricalTest(input1, input2, score, reasonCode1, reasonCode2);
        getSamples().stream().map(sample -> evaluate(pmmlRuntime, sample, FILE_NAME_NO_SUFFIX, MODEL_NAME))
                .filter(pmml4Result -> pmml4Result.getResultVariables().get(TARGET_FIELD) == null)
                .forEach(pmml4Result -> {
                    assertThat(pmml4Result.getResultVariables()).doesNotContainKey(REASON_CODE1_FIELD);
                    assertThat(pmml4Result.getResultVariables()).doesNotContainKey(REASON_CODE2_FIELD);
                });
    }

    private List<Map<String, Object>> getSamples() {
        return IntStream.range(0, 10).boxed().map(i -> new HashMap<String, Object>() {
            {
                put("input1", CATEGORY[i % CATEGORY.length]);
                put("input2", CATEGORY[Math.abs(CATEGORY.length - i) % CATEGORY.length]);
            }
        }).collect(Collectors.toList());
    }
}
