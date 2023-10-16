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
package org.kie.kogito.index.service.vertx;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.QuarkusHttpUser;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;

import graphql.GraphQL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VertxRouterSetupTest {

    @Mock
    Router routerMock;

    @Mock
    Route routeMock;

    @Mock
    RoutingContext routingContextMock;

    @Mock
    GraphQL graphQLMock;

    @Mock
    Vertx vertx;

    @InjectMocks
    @Spy
    VertxRouterSetup vertxRouterSetup;

    @BeforeEach
    public void setup() {
        lenient().when(routerMock.route()).thenReturn(routeMock);
        lenient().when(routerMock.route(anyString())).thenReturn(routeMock);
    }

    @Test
    public void testAuthEnabledTrue() {
        vertxRouterSetup.authEnabled = true;
        vertxRouterSetup.graphUIPath = "/graphiql";
        vertxRouterSetup.setupRouter(routerMock);

        verify(vertxRouterSetup).addGraphiqlRequestHeader(any());
    }

    @Test
    public void testAuthEnabledFalse() {
        vertxRouterSetup.authEnabled = false;
        vertxRouterSetup.graphUIPath = "/graphiql";
        vertxRouterSetup.setupRouter(routerMock);

        verify(vertxRouterSetup, never()).addGraphiqlRequestHeader(any());
    }

    @Test
    public void testAddGraphiqlRequestHeader() {
        GraphiQLHandler graphiQLHandlerMock = mock(GraphiQLHandler.class);
        String token = "TEST_TOKEN";
        QuarkusHttpUser quarkusHttpUser = mock(QuarkusHttpUser.class);
        SecurityIdentity securityIdentity = mock(SecurityIdentity.class);
        AccessTokenCredential accessTokenCredential = mock(AccessTokenCredential.class);

        when(routingContextMock.user()).thenReturn(quarkusHttpUser);
        when(quarkusHttpUser.getSecurityIdentity()).thenReturn(securityIdentity);
        when(securityIdentity.getCredential(AccessTokenCredential.class)).thenReturn(accessTokenCredential);
        when(accessTokenCredential.getToken()).thenReturn(token);

        vertxRouterSetup.addGraphiqlRequestHeader(graphiQLHandlerMock);

        ArgumentCaptor<Function<RoutingContext, MultiMap>> functionCaptor = ArgumentCaptor.forClass(Function.class);
        verify(graphiQLHandlerMock).graphiQLRequestHeaders(functionCaptor.capture());
        MultiMap headers = functionCaptor.getValue().apply(routingContextMock);

        assertThat(headers.get(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + token);
    }
}
