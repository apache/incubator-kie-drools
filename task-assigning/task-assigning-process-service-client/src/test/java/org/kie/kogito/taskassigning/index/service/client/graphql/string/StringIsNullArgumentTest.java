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

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

class StringIsNullArgumentTest extends AbstractStringArgumentTest<Boolean, StringIsNullArgument> {

    @Override
    protected StringIsNullArgument createArgument() {
        return new StringIsNullArgument(true);
    }

    @Override
    protected StringIsNullArgument createArgument(Boolean value) {
        return new StringIsNullArgument(value);
    }

    @Override
    StringArgument.Condition expectedCondition() {
        return StringArgument.Condition.IS_NULL;
    }

    protected Stream<Arguments> createTestValues() {
        return Stream.of(
                Arguments.of(new TestArgument<>(Boolean.TRUE, "{\"isNull\":\"true\"}")),
                Arguments.of(new TestArgument<>(Boolean.FALSE, "{\"isNull\":\"false\"}"))
        );
    }
}
