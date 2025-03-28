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
package org.kie.pmml.mining.tests;

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

public class MiningModelSummedTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MiningModelSummed";
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

    public void initMiningModelSummedTest(double input1,
                                 double input2,
                                 double input3,
                                 double expectedResult) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {200.0, -1.0, 2.0, -299.0},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMiningModelSummed(double input1, double input2, double input3, double expectedResult) throws Exception {
        initMiningModelSummedTest(input1, input2, input3, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(INPUT1, input1);
        inputData.put(INPUT2, input2);
        inputData.put(INPUT3, input3);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
