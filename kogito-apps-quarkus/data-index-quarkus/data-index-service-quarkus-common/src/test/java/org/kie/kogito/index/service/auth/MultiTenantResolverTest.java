/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MultiTenantResolverTest {

    private static final String GRAPH_UI_PATH = System.getProperty("kogito.data-index.vertx-graphql.ui.path", "/graphiql");
    private static final String GRAPH_UI_TENANT = System.getProperty("kogito.data-index.vertx-graphql.ui.tenant", "web-app-tenant");

    @Mock
    RoutingContext routingContextMock;

    @Mock
    HttpServerRequest requestMock;

    @InjectMocks
    MultiTenantResolver multiTenantResolver;

    @BeforeEach
    public void setup() {
        when(routingContextMock.request()).thenReturn(requestMock);
        givenTenantIsConfigured();
    }

    @Test
    void resolveGraphiqlTenantTest() {
        when(requestMock.path()).thenReturn(GRAPH_UI_PATH);
        assertThat(multiTenantResolver.resolve(routingContextMock)).isEqualTo(GRAPH_UI_TENANT);
    }

    @Test
    void resolveGraphiqlTenantRootTest() {
        when(requestMock.path()).thenReturn("/");
        assertThat(multiTenantResolver.resolve(routingContextMock)).isEqualTo(GRAPH_UI_TENANT);
    }

    @Test
    void resolveGraphqlTenantTest() {
        when(requestMock.path()).thenReturn("/graphql");
        assertThat(multiTenantResolver.resolve(routingContextMock)).isNull();
    }

    @Test
    void resolveGraphUITenantTest() {
        multiTenantResolver.graphUIPath = "/graphiql";
        when(requestMock.path()).thenReturn("/graphiql/");
        assertThat(multiTenantResolver.resolve(routingContextMock)).isEqualTo(GRAPH_UI_TENANT);
    }

    @Test
    void resolveOtherPathTest() {
        when(requestMock.path()).thenReturn("/other");
        assertThat(multiTenantResolver.resolve(routingContextMock)).isNull();
    }

    private void givenTenantIsConfigured() {
        multiTenantResolver.graphUITenantId = GRAPH_UI_TENANT;
        multiTenantResolver.graphUIPath = GRAPH_UI_PATH;
    }
}
