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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.pmml.api.models.PMMLStep;
import org.kie.pmml.api.runtime.PMMLListener;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.evaluator.core.implementations.PMMLRuntimeStep;
import org.kie.pmml.models.tests.AbstractPMMLTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MiningListenerTest extends AbstractPMMLTest {

    private static final String FILE_NAME_NO_SUFFIX = "MultipleMining";
    private static final String MODEL_NAME = "MixedMining";
    private static PMMLRuntime pmmlRuntime;

    private String categoricalX;
    private String categoricalY;
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;

    public void initMiningListenerTest(String categoricalX,
                              String categoricalY,
                              double age,
                              String occupation,
                              String residenceState,
                              boolean validLicense) {
        this.categoricalX = categoricalX;
        this.categoricalY = categoricalY;
        this.age = age;
        this.occupation = occupation;
        this.residenceState = residenceState;
        this.validLicense = validLicense;
    }

    @BeforeAll
    public static void setupClass() {
        pmmlRuntime = getPMMLRuntime(FILE_NAME_NO_SUFFIX);
    }

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"red", "classA", 25.0, "ASTRONAUT", "AP", true},
                {"blue", "classA", 2.3, "PROGRAMMER", "KN", true},
                {"yellow", "classC", 333.56, "INSTRUCTOR", "TN", false},
                {"orange", "classB", 0.12, "ASTRONAUT", "KN", true},
                {"green", "classC", 122.12, "TEACHER", "TN", false},
                {"green", "classB", 11.33, "INSTRUCTOR", "AP", false},
                {"orange", "classB", 423.2, "SKYDIVER", "KN", true},
        });
    }

    @MethodSource("data")
    @ParameterizedTest
    void testMixedMining(String categoricalX, String categoricalY, double age, String occupation, String residenceState, boolean validLicense) {
        initMiningListenerTest(categoricalX, categoricalY, age, occupation, residenceState, validLicense);
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("categoricalX", categoricalX);
        inputData.put("categoricalY", categoricalY);
        inputData.put("age", age);
        inputData.put("occupation", occupation);
        inputData.put("residenceState", residenceState);
        inputData.put("validLicense", validLicense);
        Set<PMMLListener> pmmlListeners = IntStream.range(0, 3)
                .mapToObj(i -> getPMMLListener()).collect(Collectors.toSet());
        evaluate(pmmlRuntime, inputData, FILE_NAME_NO_SUFFIX, MODEL_NAME, pmmlListeners);
        final List<PMMLStep> retrieved = ((PMMLListenerTest) pmmlListeners.iterator().next()).getSteps();
        retrieved
                .stream()
                .filter(pmmlStep -> !(pmmlStep instanceof PMMLRuntimeStep))
                .collect(Collectors.toList())
                .forEach(this::commonValidateStep);
        commonValidateListeners(pmmlListeners, retrieved);
    }

    private void commonValidateStep(final PMMLStep toValidate) {
        Map<String, Object> retrieved = toValidate.getInfo();
        assertThat(retrieved.containsKey("SEGMENT")).isTrue();
        assertThat(retrieved.containsKey("RESULT CODE")).isTrue();
        assertThat(retrieved.containsKey("MODEL")).isTrue();
        assertThat(retrieved.containsKey("RESULT")).isTrue();
    }
}
