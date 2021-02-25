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
package org.kie.kogito.taskassigning.index.service.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.index.service.client.graphql.GraphQLServiceClient;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstancesQueryBuilder;
import org.kie.kogito.taskassigning.index.service.client.impl.DataIndexServiceClientImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.TestUtil.parseZonedDateTime;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DataIndexServiceClientTest {

    private static final String STARTED_AFTER = "2020-11-30T13:05:56.656Z";
    private static final String STARTED = "STARTED";
    private static final String READY = "Ready";
    private static final String COMPLETED = "Completed";
    private static final int OFFSET = 0;
    private static final int LIMIT = 1;

    @Mock
    private GraphQLServiceClient queryServiceClient;

    private DataIndexServiceClient client;

    @BeforeEach
    void setUp() {
        client = new DataIndexServiceClientImpl(queryServiceClient);
    }

    @Test
    void findTasks() {
        UserTaskInstance[] mockedResult = new UserTaskInstance[] { new UserTaskInstance(), new UserTaskInstance(), new UserTaskInstance() };
        doReturn(mockedResult).when(queryServiceClient).executeQuery(eq(UserTaskInstancesQueryBuilder.QUERY_NAME), anyString(), eq(UserTaskInstance[].class));
        List<UserTaskInstance> result = client.findTasks(Arrays.asList(READY, COMPLETED), parseZonedDateTime(STARTED_AFTER), STARTED, true, OFFSET, LIMIT);
        verify(queryServiceClient).executeQuery(eq(UserTaskInstancesQueryBuilder.QUERY_NAME), anyString(), eq(UserTaskInstance[].class));
        assertThat(result).hasSize(mockedResult.length);
        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i)).isEqualTo(mockedResult[i]);
        }
    }

    @Test
    void close() throws IOException {
        client.close();
        verify(queryServiceClient).close();
    }
}
