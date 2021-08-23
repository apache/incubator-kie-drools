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

package org.kie.pmml.mining.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.pmml.PMML4Result;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.kie.pmml.api.enums.ResultCode.FAIL;
import static org.kie.pmml.api.enums.ResultCode.OK;

@RunWith(Parameterized.class)
public class MultiplePredicatesMiningTest extends AbstractPMMLTest {

    private static final String FILE_NAME = "MultipleMining.pmml";
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

    public MultiplePredicatesMiningTest(String residenceState,
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

  @BeforeClass
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME);
    }

    @Parameterized.Parameters
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

    @Test
    public void testPredicatesMining() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("residenceState", residenceState);
        inputData.put("validLicense", validLicense);
        inputData.put("occupation", occupation);
        inputData.put("categoricalY", categoricalY);
        inputData.put("categoricalX", categoricalX);
        inputData.put("variable", variable);
        inputData.put("age", age);
        PMML4Result pmml4Result = evaluate(pmmlRuntime, inputData, MODEL_NAME);

        if (expectedResult != null) {
            Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNotNull();
            Assertions.assertThat(pmml4Result.getResultCode()).isEqualTo(OK.getName());
        } else {
            Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isNull();
            Assertions.assertThat(pmml4Result.getResultCode()).isEqualTo(FAIL.getName());
        }
        Assertions.assertThat(pmml4Result.getResultVariables().get(TARGET_FIELD)).isEqualTo(expectedResult);
    }
}
