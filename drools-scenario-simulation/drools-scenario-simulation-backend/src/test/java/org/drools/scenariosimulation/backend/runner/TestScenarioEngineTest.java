/*
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
package org.drools.scenariosimulation.backend.runner;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestScenarioEngineTest {

    @ParameterizedTest
    @MethodSource("getScesimFileNameData")
    void getScesimFileName(String path, String expectedFileName, Class<?> expectedException) {
        if (expectedException != null) {
            assertThatThrownBy(() ->
                TestScenarioEngine.getScesimFileName(path)
            ).isInstanceOf(expectedException);
        } else {
            assertThat(TestScenarioEngine.getScesimFileName(path)).isEqualTo(expectedFileName);
        }
    }

    private static Object[][] getScesimFileNameData() {
        return new Object[][]{
                {"src/test/Test.scesim", "Test", null},
                {"src\\test\\Test.scesim", "Test", null},
                {"Test.scesim", "Test", null},
                {"src/test/Test.1.scesim", "Test.1", null},
                {"src\\test\\Test.1.scesim", "Test.1", null},
                {"Test", "Test", null},
                {null, null, NullPointerException.class},
        };
    }


}
