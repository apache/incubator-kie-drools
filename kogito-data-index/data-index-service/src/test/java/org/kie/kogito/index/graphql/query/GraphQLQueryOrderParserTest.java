/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql.query;

import java.util.Arrays;
import java.util.List;

import graphql.execution.MergedField;
import graphql.language.Argument;
import graphql.language.EnumValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;
import org.kie.kogito.persistence.api.query.SortDirection;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.persistence.api.query.QueryFilterFactory.orderBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphQLQueryOrderParserTest {

    private static DataFetchingEnvironment mockDataFetchingEnvironment(List<Argument> arguments) {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        MergedField mergedFiled = mock(MergedField.class);
        when(mergedFiled.getArguments()).thenReturn(arguments);
        when(env.getMergedField()).thenReturn(mergedFiled);
        return env;
    }

    @Test
    public void testNull() {
        assertThat(new GraphQLQueryOrderByParser().apply(null)).isEmpty();
    }

    @Test
    public void testEmpty() {
        DataFetchingEnvironment env = mockDataFetchingEnvironment(emptyList());

        assertThat(new GraphQLQueryOrderByParser().apply(env)).isEmpty();
    }

    @Test
    public void testNonMatchingArgument() {
        DataFetchingEnvironment env = mockDataFetchingEnvironment(singletonList(Argument.newArgument().name("where").build()));

        assertThat(new GraphQLQueryOrderByParser().apply(env)).isEmpty();
    }

    @Test
    public void testSortSingleArgument() {
        DataFetchingEnvironment env = mockDataFetchingEnvironment(singletonList(
                Argument.newArgument().name("orderBy").value(
                        ObjectValue.newObjectValue().objectField(
                                ObjectField.newObjectField().name("start").value(EnumValue.newEnumValue("ASC").build()
                                ).build()
                        ).build()
                ).build()));

        assertThat(new GraphQLQueryOrderByParser().apply(env))
                .hasSize(1)
                .first()
                .matches(s -> s.getAttribute().equals("start"))
                .matches(s -> SortDirection.ASC == s.getSort());
    }

    @Test
    public void testSortArgumentOrder() {
        DataFetchingEnvironment env = mockDataFetchingEnvironment(singletonList(
                Argument.newArgument().name("orderBy").value(
                        ObjectValue.newObjectValue()
                                .objectFields(
                                        Arrays.asList(
                                                ObjectField.newObjectField().name("start").value(EnumValue.newEnumValue("ASC").build()).build(),
                                                ObjectField.newObjectField().name("end").value(EnumValue.newEnumValue("DESC").build()).build()
                                        )
                                )
                                .build()

                ).build()
        ));

        assertThat(new GraphQLQueryOrderByParser().apply(env))
                .hasSize(2)
                .containsExactly(
                        orderBy("start", SortDirection.ASC),
                        orderBy("end", SortDirection.DESC)
                );
    }

    @Test
    public void testSortArgumentUsingChildEntity() {
        DataFetchingEnvironment env = mockDataFetchingEnvironment(singletonList(
                Argument.newArgument().name("orderBy").value(
                        ObjectValue.newObjectValue()
                                .objectFields(
                                        Arrays.asList(
                                                ObjectField.newObjectField().name("nodes").value(
                                                        ObjectValue.newObjectValue().objectField(
                                                                ObjectField.newObjectField().name("name").value(EnumValue.newEnumValue("DESC").build()).build()
                                                        ).build()
                                                ).build(),
                                                ObjectField.newObjectField().name("start").value(EnumValue.newEnumValue("ASC").build()).build()
                                        )
                                )
                                .build()

                ).build()
        ));

        assertThat(new GraphQLQueryOrderByParser().apply(env))
                .hasSize(2)
                .containsExactly(
                        orderBy("nodes.name", SortDirection.DESC),
                        orderBy("start", SortDirection.ASC)
                );
    }
}
