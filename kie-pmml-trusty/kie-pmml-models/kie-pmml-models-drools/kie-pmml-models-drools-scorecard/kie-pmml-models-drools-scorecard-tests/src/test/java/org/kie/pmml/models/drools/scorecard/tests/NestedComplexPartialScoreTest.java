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

package org.kie.pmml.models.drools.scorecard.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class NestedComplexPartialScoreTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "NestedComplexPartialScore.pmml";
    private static final String MODEL_NAME = "NestedComplexPartialScoreScorecard";
    private static final String TARGET_FIELD = "Score";
    private static final String REASON_CODE1_FIELD = "Reason Code 1";
    private static final String REASON_CODE2_FIELD = "Reason Code 2";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private double score;
    private String reasonCode1;
    private String reasonCode2;

    public NestedComplexPartialScoreTest(double input1, double input2, double score,
                                        String reasonCode1, String reasonCode2) {
        this.input1 = input1;
        this.input2 = input2;
        this.score = score;
        this.reasonCode1 = reasonCode1;
        this.reasonCode2 = reasonCode2;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(MODEL_NAME, FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                { -1005.5, 10200, -15, "characteristic2ReasonCode", null },
                { -1001, 4, -3969, "characteristic2ReasonCode", null },
                { 2, 1002, 959, "characteristic2ReasonCode", null },
                { 10, 20, 235, null, null },
                { -2, 3, 0, "characteristic1ReasonCode", "characteristic2ReasonCode" },
        });
    }

    @Test
    @Ignore
    public void testNestedComplexPartialScore() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(score);
        /* TODO: Uncomment when reason codes are implemented
        Assertions.assertThat(pmml4Result.getResultVariables().get(REASON_CODE1_FIELD)).isEqualTo(reasonCode1);
        Assertions.assertThat(pmml4Result.getResultVariables().get(REASON_CODE2_FIELD)).isEqualTo(reasonCode2);
        Assertions.assertThat(pmml4Result.getResultVariables().get(REASON_CODE3_FIELD)).isEqualTo(reasonCode3);
         */
    }
}
