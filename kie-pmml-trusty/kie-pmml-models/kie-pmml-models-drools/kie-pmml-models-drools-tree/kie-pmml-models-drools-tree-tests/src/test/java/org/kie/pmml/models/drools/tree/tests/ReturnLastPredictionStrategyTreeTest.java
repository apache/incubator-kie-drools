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

package org.kie.pmml.models.drools.tree.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class ReturnLastPredictionStrategyTreeTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "ReturnLastPredictionStrategyTree.pmml";
    private static final String MODEL_NAME = "ReturnLastPredictionStrategyTreeModel";
    private static final String TARGET_FIELD = "Predicted_result";
    private static PMMLRuntime pmmlRuntime;

    private double input1;
    private double input2;
    private double input3;
    private String expectedResult;

    public ReturnLastPredictionStrategyTreeTest(double input1, double input2, double input3, String expectedResult) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.expectedResult = expectedResult;
    }

  @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {9, 0, 0, "classA"},
                {5, -5, 5, "classB"},
                {5, 7, 0, "classC"},
                {0, 7, 0, "classC"},
                {0, 7, 12, "classB"},
        });
    }

    @Test
    public void testReturnLastPredictionStrategy() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("input1", input1);
        inputData.put("input2", input2);
        inputData.put("input3", input3);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
