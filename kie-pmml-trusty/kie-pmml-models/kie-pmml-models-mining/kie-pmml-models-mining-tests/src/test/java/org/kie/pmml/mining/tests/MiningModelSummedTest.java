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

package org.kie.pmml.mining.tests;

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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class MiningModelSummedTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "MiningModelSummed.pmml";
    private static final String MODEL_NAME = "MiningModelSummed";
    private static final String TARGET_FIELD = "result";
    private final String INPUT1 = "input1";
    private final String INPUT2 = "input2";
    private final String INPUT3 = "input3";
    private double input1;
    private double input2;
    private double input3;
    private static PMMLRuntime pmmlRuntime;

    private double expectedResult;

    public MiningModelSummedTest(double input1,
                                 double input2,
                                 double input3,
                                 double expectedResult) {
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
                {200.0, -1.0, 2.0, -299.0},
        });
    }

    @Test
    public void testMiningModelSummed() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(INPUT1, input1);
        inputData.put(INPUT2, input2);
        inputData.put(INPUT3, input3);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
