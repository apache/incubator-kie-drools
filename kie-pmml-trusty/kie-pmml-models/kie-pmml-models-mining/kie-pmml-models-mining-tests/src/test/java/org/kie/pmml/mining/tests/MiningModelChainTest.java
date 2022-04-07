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
public class MiningModelChainTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "MiningModelChain.pmml";
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

    public MiningModelChainTest(double age,
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

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
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

    @Test
    public void testMiningModelChain() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(AGE, age);
        inputData.put(OCCUPATION, occupation);
        inputData.put(RESIDENCESTATE, residenceState);
        inputData.put(VALIDLICENSE, validLicense);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
