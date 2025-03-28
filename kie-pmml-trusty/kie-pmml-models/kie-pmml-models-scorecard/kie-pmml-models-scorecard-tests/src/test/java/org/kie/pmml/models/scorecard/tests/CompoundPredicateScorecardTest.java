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

public class CompoundPredicateScorecardTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "CompoundPredicateScorecard";
    private static final String MODEL_NAME = "CompoundPredicateScorecard";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";
    private static final String REASON_CODE3_FIELD = "Reason Code 3";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private String input3;
    private String input4;
    private double score;
    private String reasonCode1;
    private String reasonCode2;
    private String reasonCode3;

    public void initCompoundPredicateScorecardTest(double input1, double input2, String input3, String input4, double score,
                                          String reasonCode1, String reasonCode2, String reasonCode3) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.score = score;
        this.reasonCode1 = reasonCode1;
        this.reasonCode2 = reasonCode2;
        this.reasonCode3 = reasonCode3;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {-21.5, -7, "classA", "classB", -93, null, null, null},
                {-7, -7, "classA", "classB", -93, null, null, null},
                {2, 3.5, "classA", "classB", -68, "characteristic1ReasonCode", null, null},
                {-8, 3, "classA", "classB", -58, "characteristic1ReasonCode", null, null},
                {-8, -12.5, "classB", "classB", 135, "characteristic3ReasonCode", null, null},
                {-8, 3, "classB", "classB", 170, "characteristic3ReasonCode", "characteristic1ReasonCode", null},
                {5, 3, "classB", "classB", 160, "characteristic3ReasonCode", "characteristic1ReasonCode", null},
                {-8, -50, "classC", "classC", 230.5, "characteristic3ReasonCode", "characteristic2ReasonCode", null},
                {-8, 3, "classC", "classC", 265.5, "characteristic3ReasonCode", "characteristic2ReasonCode", "characteristic1ReasonCode"},
                {5, 3, "classC", "classC", 255.5, "characteristic3ReasonCode", "characteristic2ReasonCode", "characteristic1ReasonCode"},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testCompoundPredicateScorecard(double input1, double input2, String input3, String input4, double score, String reasonCode1, String reasonCode2, String reasonCode3) {
        initCompoundPredicateScorecardTest(input1, input2, input3, input4, score, reasonCode1, reasonCode2, reasonCode3);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        inputData.put("input3", input3);
        inputData.put("input4", input4);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE3_FIELD)).isEqualTo(reasonCode3);
    }
}
