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

package org.kie.kogito.index.graphql;

import graphql.schema.DataFetchingEnvironment;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.ProcessInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class GraphQLSchemaManagerTest {

    GraphQLSchemaManager schemaManager = new GraphQLSchemaManager();

    @Test
    public void testNullServiceUrl() {
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv(null, null))).isNull();
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("travels", null))).isNull();
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orders", null))).isNull();
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orderItems", null))).isNull();
    }

    @Test
    public void testNullProcessIdServiceUrl() {
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("travels", "/travels"))).isNull();
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orders", "/orders"))).isNull();
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orderItems", "/orderItems"))).isNull();
    }

    @Test
    public void testUrlProcessIdServiceUrl() {
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("travels", "http://localhost:8080/travels"))).isEqualTo("http://localhost:8080");
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orders", "http://localhost:8080/orders"))).isEqualTo("http://localhost:8080");
        assertThat(schemaManager.getProcessInstanceServiceUrl(getEnv("demo.orderItems", "http://localhost:8080/orderItems"))).isEqualTo("http://localhost:8080");
    }

    private DataFetchingEnvironment getEnv(String processId, String endpoint) {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getSource()).thenReturn(getProcessInstance(processId, endpoint));
        return env;
    }

    private ProcessInstance getProcessInstance(String processId, String endpoint) {
        ProcessInstance pi = new ProcessInstance();
        pi.setProcessId(processId);
        pi.setEndpoint(endpoint);
        return pi;
    }
}
