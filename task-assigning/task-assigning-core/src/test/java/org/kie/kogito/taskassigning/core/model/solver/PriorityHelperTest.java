/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.core.model.solver;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriorityHelperTest {

    @Test
    void isHighLevel() {
        Stream.of("0", "1", "2").forEach(level -> assertThat(PriorityHelper.isHighLevel(level)).isTrue());
    }

    @Test
    void isMediumLevel() {
        Stream.of("3", "4", "5", "6").forEach(level -> assertThat(PriorityHelper.isMediumLevel(level)).isTrue());
    }

    @Test
    void isLowLevel() {
        Stream.of("7", "8", "9", "10").forEach(level -> assertThat(PriorityHelper.isLowLevel(level)).isTrue());
    }
}
