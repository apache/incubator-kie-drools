/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractArgumentTest<V, T extends Argument> {

    protected abstract T createArgument();

    protected abstract T createArgument(V value);

    protected abstract String expectedTypeId();

    @ParameterizedTest
    @MethodSource("createTestValues")
    void asJson(TestArgument<V> testArgument) {
        T argument = createArgument(testArgument.getValue());
        assertThat(argument.asJson()).hasToString(testArgument.getExpectedJson());
    }

    protected abstract Stream<Arguments> createTestValues();

    @Test
    void getTypeId() {
        T argument = createArgument();
        assertThat(argument.getTypeId()).isEqualTo(expectedTypeId());
    }

    protected static class TestArgument<V> {

        private V value;
        private String expectedJson;

        public TestArgument(V value, String expectedJson) {
            this.value = value;
            this.expectedJson = expectedJson;
        }

        public V getValue() {
            return value;
        }

        public String getExpectedJson() {
            return expectedJson;
        }
    }
}
