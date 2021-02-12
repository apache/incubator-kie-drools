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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateGreaterThanArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateGreaterThanEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateIsNullArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateLessThanArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateLessThanEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.pagination.PaginationArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringEqualArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringInArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringIsNullArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringLikeArgument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.TestUtil.parseZonedDateTime;

class ArgumentFactoryTest {

    private static final String VALUE = "VALUE";
    private static final boolean IS_NULL_VALUE = true;
    private static final ZonedDateTime DATE_VALUE = parseZonedDateTime("2020-12-01T07:54:56.883Z");
    private static final int OFFSET = 0;
    private static final int LIMIT = 1;

    @Test
    void newStringIn() {
        List<String> values = Collections.singletonList(VALUE);
        StringInArgument argument = ArgumentFactory.newStringIn(Collections.singletonList(VALUE));
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(values);
    }

    @Test
    void newStringEqual() {
        StringEqualArgument argument = ArgumentFactory.newStringEqual(VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(VALUE);
    }

    @Test
    void newStringIsNull() {
        StringIsNullArgument argument = ArgumentFactory.newStringIsNull(IS_NULL_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(IS_NULL_VALUE);
    }

    @Test
    void newStringLike() {
        StringLikeArgument argument = ArgumentFactory.newStringLike(VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(VALUE);
    }

    @Test
    void newDateGreaterThan() {
        DateGreaterThanArgument argument = ArgumentFactory.newDateGreaterThan(DATE_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(DATE_VALUE);
    }

    @Test
    void newDateGreaterThanEqual() {
        DateGreaterThanEqualArgument argument = ArgumentFactory.newDateGreaterThanEqual(DATE_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(DATE_VALUE);
    }

    @Test
    void newDateEqual() {
        DateEqualArgument argument = ArgumentFactory.newDateEqual(DATE_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(DATE_VALUE);
    }

    @Test
    void newDateLessThan() {
        DateLessThanArgument argument = ArgumentFactory.newDateLessThan(DATE_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(DATE_VALUE);
    }

    @Test
    void newDateLessThanEqual() {
        DateLessThanEqualArgument argument = ArgumentFactory.newDateLessThanEqual(DATE_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(DATE_VALUE);
    }

    @Test
    void newDateIsNull() {
        DateIsNullArgument argument = ArgumentFactory.newDateIsNull(IS_NULL_VALUE);
        assertThat(argument).isNotNull();
        assertThat(argument.getValue()).isEqualTo(IS_NULL_VALUE);
    }

    @Test
    void newPagination() {
        PaginationArgument argument = ArgumentFactory.newPagination(OFFSET, LIMIT);
        assertThat(argument).isNotNull();
        assertThat(argument.getOffset()).isEqualTo(OFFSET);
        assertThat(argument.getLimit()).isEqualTo(LIMIT);
    }
}
