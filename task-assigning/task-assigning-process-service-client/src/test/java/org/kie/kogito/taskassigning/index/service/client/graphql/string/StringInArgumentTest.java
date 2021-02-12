/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.graphql.string;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

class StringInArgumentTest extends AbstractStringArgumentTest<List<String>, StringInArgument> {

    @Override
    protected StringInArgument createArgument() {
        return new StringInArgument(Collections.emptyList());
    }

    @Override
    protected StringInArgument createArgument(List<String> value) {
        return new StringInArgument(value);
    }

    @Override
    StringArgument.Condition expectedCondition() {
        return StringArgument.Condition.IN;
    }

    protected Stream<Arguments> createTestValues() {
        return Stream.of(
                Arguments.of(new TestArgument<>(Collections.emptyList(), "{\"in\":[]}")),
                Arguments.of(new TestArgument<>(Arrays.asList("A", "B", "C"), "{\"in\":[\"A\",\"B\",\"C\"]}"))
        );
    }
}
