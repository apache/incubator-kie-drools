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
import static org.kie.pmml.api.enums.ResultCode.FAIL;
import static org.kie.pmml.api.enums.ResultCode.OK;

public class PredicatesMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MiningModel_Predicates";
    private static final String MODEL_NAME = "PredicatesMining";
    private static final String TARGET_FIELD = "categoricalResult";
    private static PMMLRuntime pmmlRuntime;

    private String categoricalX;
    private String categoricalY;
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;
    private double variable;
    private Double expectedResult;

    public void initPredicatesMiningTest(String residenceState,
                                boolean validLicense,
                                String occupation,
                                String categoricalY,
                                String categoricalX,
                                double variable,
                                double age,
                                Double expectedResult) {
        this.residenceState = residenceState;
        this.validLicense = validLicense;
        this.occupation = occupation;
        this.categoricalY = categoricalY;
        this.categoricalX = categoricalX;
        this.variable = variable;
        this.age = age;
        this.expectedResult = expectedResult;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"AP", true, "ASTRONAUT", "classA", "red", 23.6, 25.0, 21.345},
                {"KN", true, "PROGRAMMER", "classA", "blue", 9.12, 2.3, -0.10000000000000053},
                {"TN", false, "INSTRUCTOR", "classC", "yellow", 333.12, 33.56, null},
                {"KN", true, "ASTRONAUT", "classB", "orange", 1.23, 30.12, 22.3725},
                {"TN", false, "TEACHER", "classC", "green", 12.34, 22.12, 32.9},
                {"AP", false, "INSTRUCTOR", "classB", "green", 2.2, 11.33, 12.899999999999999},
                {"KN", true, "SKYDIVER", "classB", "orange", 9.12, 42.2, 11.448333333333332},
                {"AP", false, "TEACHER", "classA", "yellow", 11.2, 12.1, -103.35},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testPredicatesMining(String residenceState, boolean validLicense, String occupation, String categoricalY, String categoricalX, double variable, double age, Double expectedResult) {
        initPredicatesMiningTest(residenceState, validLicense, occupation, categoricalY, categoricalX, variable, age, expectedResult);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("residenceState", residenceState);
        inputData.put("validLicense", validLicense);
        inputData.put("occupation", occupation);
        inputData.put("categoricalY", categoricalY);
        inputData.put("categoricalX", categoricalX);
        inputData.put("variable", variable);
        inputData.put("age", age);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME);

        if (expectedResult != null) {
            assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
            assertThat(pmml4Result.getResultCode()).isEqualTo(OK.getName());
        } else {
            assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNull();
            assertThat(pmml4Result.getResultCode()).isEqualTo(FAIL.getName());
        }
        assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
