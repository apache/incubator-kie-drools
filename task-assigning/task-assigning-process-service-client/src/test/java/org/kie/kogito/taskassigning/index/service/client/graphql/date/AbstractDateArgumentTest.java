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

package org.kie.kogito.taskassigning.index.service.client.graphql.date;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.taskassigning.index.service.client.graphql.AbstractArgumentTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.TestUtil.parseZonedDateTime;

abstract class AbstractDateArgumentTest<V, T extends DateArgument<V>> extends AbstractArgumentTest<V, T> {

    static final ZonedDateTime VALUE = parseZonedDateTime("2020-12-01T07:54:56.883Z");

    @ParameterizedTest
    @MethodSource("createTestValues")
    void getValue(TestArgument<V> testArgument) {
        T argument = createArgument(testArgument.getValue());
        assertThat(argument.getValue()).isEqualTo(testArgument.getValue());
    }

    @Test
    void getCondition() {
        assertThat(createArgument().getCondition()).isEqualTo(expectedCondition());
    }

    abstract DateArgument.Condition expectedCondition();

    @Override
    protected String expectedTypeId() {
        return DateArgument.TYPE_ID;
    }
}
