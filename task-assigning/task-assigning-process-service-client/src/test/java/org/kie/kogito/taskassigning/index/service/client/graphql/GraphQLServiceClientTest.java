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

import java.util.Arrays;

import javax.inject.Inject;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.auth.NoAuthenticationCredentials;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance.Field.ID;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance.Field.INPUTS;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance.Field.NAME;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance.Field.POTENTIAL_GROUPS;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance.Field.STARTED;
import static org.kie.kogito.taskassigning.index.service.client.graphql.WireMockGraphQLResource.USER_TASKS_QUERY_FAILURE_MOCK;
import static org.kie.kogito.taskassigning.index.service.client.graphql.WireMockGraphQLResource.USER_TASKS_QUERY_MOCK;
import static org.kie.kogito.taskassigning.index.service.client.graphql.WireMockGraphQLResource.USER_TASKS_QUERY_MOCK_RESULT;

@QuarkusTest
@QuarkusTestResource(WireMockGraphQLResource.class)
class GraphQLServiceClientTest {

    private static final String SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK = "Tasks at position: %s are expected to have the same value for field: %s";
    private static final String QUERY_NAME = "UserTaskInstances";

    @Inject
    GraphQLServiceClientFactory graphQLServiceClientFactory;

    @Test
    void executeQuery() {
        GraphQLServiceClient client = graphQLServiceClientFactory.newClient(createServiceConfig(),
                                                                            NoAuthenticationCredentials.INSTANCE);

        UserTaskInstance[] result = client.executeQuery(QUERY_NAME, USER_TASKS_QUERY_MOCK, UserTaskInstance[].class);
        assertResults(result, USER_TASKS_QUERY_MOCK_RESULT);
    }

    @Test
    void executeQueryWithFailure() {
        GraphQLServiceClient client = graphQLServiceClientFactory.newClient(createServiceConfig(),
                                                                            NoAuthenticationCredentials.INSTANCE);

        assertThatThrownBy(() -> client.executeQuery(QUERY_NAME, USER_TASKS_QUERY_FAILURE_MOCK, UserTaskInstance[].class))
                .hasMessageStartingWith("An error was produced during query execution:");
    }

    private static void assertResults(UserTaskInstance[] result, WireMockGraphQLResource.UserTaskInstanceMock[] expectedResult) {
        assertThat(result)
                .isNotEmpty()
                .hasSize(expectedResult.length);

        for (int i = 0; i < result.length; i++) {
            assertTaskEquals(result[i], expectedResult[i], i);
        }
    }

    private static void assertTaskEquals(UserTaskInstance task, WireMockGraphQLResource.UserTaskInstanceMock expectedTask, int index) {
        assertThat(task.getId())
                .as(SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK, index, ID.getName())
                .isEqualTo(expectedTask.getId());
        assertThat(task.getName())
                .as(SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK, index, NAME.getName())
                .isEqualTo(expectedTask.getName());
        assertThat(task.getStarted())
                .as(SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK, index, STARTED.getName())
                .isEqualTo(expectedTask.getStarted());
        assertThat(task.getPotentialGroups())
                .as(SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK, index, POTENTIAL_GROUPS.getName())
                .isEqualTo(Arrays.asList(expectedTask.getPotentialGroups()));
        assertThat(task.getInputs())
                .as(SAME_VALUE_EXPECTED_FOR_FIELD_AT_TASK, index, INPUTS.getName())
                .hasToString(expectedTask.getInputs());
    }

    private GraphQLServiceClientConfig createServiceConfig() {
        String serviceUrl = System.getProperty(WireMockGraphQLResource.GRAPHQL_SERVICE_URL) + "/graphql";
        return GraphQLServiceClientConfig.newBuilder()
                .serviceUrl(serviceUrl)
                .build();
    }
}
