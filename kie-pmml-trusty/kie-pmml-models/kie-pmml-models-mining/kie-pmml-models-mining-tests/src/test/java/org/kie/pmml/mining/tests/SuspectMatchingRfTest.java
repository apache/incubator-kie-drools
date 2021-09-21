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

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Percentage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

@RunWith(Parameterized.class)
public class SuspectMatchingRfTest extends AbstractPMMLTest {

    private static final Percentage TOLERANCE_PERCENTAGE = Percentage.withPercentage(0.000001);
    private static final String FILE_NAME = "SuspectMatchingRf.pmml";
    private static final String MODEL_NAME = "SuspectMatchingRf";
    private static final String TARGET_FIELD = "FlagMatchDecision";
    private static final String LEGAL_GIVEN_NAME_ONE = "LEGAL_GIVEN_NAME_ONE";
    private static final String LEGAL_LAST_NAME = "LEGAL_LAST_NAME";
    private static final String LEGAL_NAME = "LEGAL_NAME";
    private static final String OTHER_LAST_NAME = "OTHER_LAST_NAME";
    private static final String OTHER_NAME = "OTHER_NAME";
    private static final String REG_ADDRESS = "REG_ADDRESS";
    private static final String OTH_ADDRESS = "OTH_ADDRESS";
    private static final String MOBILE = "MOBILE";
    private static final String PHONE = "PHONE";
    private static final String EMAIL = "EMAIL";
    private static final String ABN = "ABN";
    private static final String DRLIC = "DRLIC";
    private static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
    private static final String GENDER = "GENDER";

    private static final String PROBABILITY_0 = "probability(0)";
    private static final String PROBABILITY_1 = "probability(1)";
    private static final String PROBABILITY_2 = "probability(2)";

    private static PMMLRuntime pmmlRuntime;

    private float dateOfBirth;
    private float legalGivenNameOne;
    private float otherName;
    private float email;
    private float regAddress;
    private float mobile;
    private float othAddress;
    private float gender;
    private float phone;
    private float legalLastName;
    private float drlic;
    private float abn;
    private float otherLastName;
    private float legalName;
    private int expectedResult;
    private double p0;
    private double p1;
    private double p2;

    public SuspectMatchingRfTest(float dateOfBirth,
                                 float legalGivenNameOne,
                                 float otherName,
                                 float email,
                                 float regAddress,
                                 float mobile,
                                 float othAddress,
                                 float gender,
                                 float phone,
                                 float legalLastName,
                                 float drlic,
                                 float abn,
                                 float otherLastName,
                                 float legalName,
                                 int expectedResult,
                                 double p0,
                                 double p1,
                                 double p2) {
        this.dateOfBirth = dateOfBirth;
        this.legalGivenNameOne = legalGivenNameOne;
        this.otherName = otherName;
        this.email = email;
        this.regAddress = regAddress;
        this.mobile = mobile;
        this.othAddress = othAddress;
        this.gender = gender;
        this.phone = phone;
        this.legalLastName = legalLastName;
        this.drlic = drlic;
        this.abn = abn;
        this.otherLastName = otherLastName;
        this.legalName = legalName;
        this.expectedResult = expectedResult;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
    }

    @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {6.9f, 3.1f, 5.1f, 2.3f, 34.6f, 12.4f, 3.4f, 23.7f, 4.5f, 55.1f, 4.99f, 3.5f, 95.1f, 33.1f,
                        1,
                        0.0, 0.7, 0.3},
                {5.8f, 2.6f, 4.0f, 1.2f, 34f, 3.11f, 341.23f, 5.444f, 34.88f, 123.4f, 7.9f, 10.44f, 32.4f, 1.9f,
                        1,
                        0.0, 0.7, 0.3}
        });
    }

    @Test
    public void testSuspectMatchingRf() throws Exception {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(LEGAL_GIVEN_NAME_ONE, legalGivenNameOne);
        inputData.put(LEGAL_LAST_NAME, legalLastName);
        inputData.put(LEGAL_NAME, legalName);
        inputData.put(OTHER_LAST_NAME, otherLastName);
        inputData.put(OTHER_NAME, otherName);
        inputData.put(REG_ADDRESS, regAddress);
        inputData.put(OTH_ADDRESS, othAddress);
        inputData.put(MOBILE, mobile);
        inputData.put(PHONE, phone);
        inputData.put(EMAIL, email);
        inputData.put(ABN, abn);
        inputData.put(DRLIC, drlic);
        inputData.put(DATE_OF_BIRTH, dateOfBirth);
        inputData.put(GENDER, gender);

        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_0)).isNotNull();
        Assertions.assertThat((double)pmml4Result.getResultVariables().get(PROBABILITY_0)).isCloseTo(p0, TOLERANCE_PERCENTAGE);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_1)).isNotNull();
        Assertions.assertThat((double)pmml4Result.getResultVariables().get(PROBABILITY_1)).isCloseTo(p1, TOLERANCE_PERCENTAGE);
        Assertions.assertThat(pmml4Result.getResultVariables().get(PROBABILITY_2)).isNotNull();
        Assertions.assertThat((double)pmml4Result.getResultVariables().get(PROBABILITY_2)).isCloseTo(p2, TOLERANCE_PERCENTAGE);
    }
}
