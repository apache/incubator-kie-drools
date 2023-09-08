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
package org.kie.pmml.models.drools.scorecard.tests;

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

public class CompoundNestedPredicateScorecardTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "CompoundNestedPredicateScorecard";
    private static final String MODEL_NAME = "CompoundNestedPredicateScorecard";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private String input2;
    private double score;
    private String reasonCode1;
    private String reasonCode2;

    public void initCompoundNestedPredicateScorecardTest(double input1, String input2, double score,
                                                String reasonCode1, String reasonCode2) {
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
                {-50, "classB", -8, "characteristic2ReasonCode", null},
                {-50, "classD", -8, "characteristic2ReasonCode", null},
                {-9, "classB", 75, "characteristic1ReasonCode", null},
                {25.4, "classB", 75, "characteristic1ReasonCode", null},
                {-7, "classA", -8, "characteristic2ReasonCode", null},
                {-7, "classC", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode"},
                {5, "classB", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode"},
                {7.4, "classB", -15.5, "characteristic1ReasonCode", "characteristic2ReasonCode"},
                {12, "classB", 75, "characteristic1ReasonCode", null},
                {12, "classD", 75, "characteristic1ReasonCode", null},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testCompoundNestedPredicateScorecard(double input1, String input2, double score, String reasonCode1, String reasonCode2) {
        initCompoundNestedPredicateScorecardTest(input1, input2, score, reasonCode1, reasonCode2);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
    }
}
