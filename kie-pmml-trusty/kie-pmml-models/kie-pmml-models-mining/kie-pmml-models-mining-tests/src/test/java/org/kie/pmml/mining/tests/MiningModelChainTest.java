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

public class MiningModelChainTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MiningModelChain";
    private static final String MODEL_NAME = "SampleModelChainMine";
    private static final String TARGET_FIELD = "qualificationLevel";
    private final String AGE = "age";
    private final String OCCUPATION = "occupation";
    private final String RESIDENCESTATE = "residenceState";
    private final String VALIDLICENSE = "validLicense";
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;
    private static PMMLRuntime pmmlRuntime;

    private String expectedResult;

    public void initMiningModelChainTest(double age,
                                String occupation,
                                String residenceState,
                                boolean validLicense,
                                String expectedResult) {
        this.age = age;
        this.occupation = occupation;
        this.residenceState = residenceState;
        this.validLicense = validLicense;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {25.0, "ASTRONAUT", "AP", true, "Barely"},
                {2.3, "PROGRAMMER", "KN", true, "Unqualified"},
                {333.56, "INSTRUCTOR", "TN", false, "Well"},
                {0.12, "ASTRONAUT", "KN", true, "Unqualified"},
                {122.12, "TEACHER", "TN", false, "Well"},
                {11.33, "INSTRUCTOR", "AP", false, "Unqualified"},
                {423.2, "SKYDIVER", "KN", true, "Barely"},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMiningModelChain(double age, String occupation, String residenceState, boolean validLicense, String expectedResult) throws Exception {
        initMiningModelChainTest(age, occupation, residenceState, validLicense, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(AGE, age);
        inputData.put(OCCUPATION, occupation);
        inputData.put(RESIDENCESTATE, residenceState);
        inputData.put(VALIDLICENSE, validLicense);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
