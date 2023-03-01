/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.api.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "myExtension", "myëxtension" })
    void validateExtensionNameUnsuccessful(String name) {
        assertThatThrownBy(() -> EventUtils.validateExtensionName(name))
                .hasMessageStartingWith("Invalid attribute or extension name:");
    }

    @Test
    void validateExtensionNameNullUnsuccessful() {
        validateExtensionNameUnsuccessful(null);
    }

    @ParameterizedTest
    @ValueSource(strings = { "successful", "value1", "value3", "v" })
    void validateExtensionNameSuccessful(String name) {
        assertThatNoException().isThrownBy(() -> EventUtils.validateExtensionName(name));
    }
}
