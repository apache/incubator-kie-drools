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

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.process.service.client.BasicAuthenticationCredentials;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthenticationFilterTest {

    private static final String USER = "user";
    private static final String PASSWORD = "password";

    @Mock
    private ClientRequestContext requestContext;

    @Mock
    private MultivaluedMap<String, Object> multivaluedMap;

    @Captor
    private ArgumentCaptor<Object> headerValueCaptor;

    @Test
    void filter() throws IOException {
        doReturn(multivaluedMap).when(requestContext).getHeaders();
        BasicAuthenticationCredentials credentials = BasicAuthenticationCredentials.newBuilder()
                .user(USER)
                .password(PASSWORD)
                .build();
        String expectedToken = USER + ":" + PASSWORD;
        String expectedValue = "Basic " + Base64.getEncoder().encodeToString(expectedToken.getBytes(StandardCharsets.UTF_8));
        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(credentials);
        filter.filter(requestContext);
        verify(multivaluedMap).add(eq(HttpHeaders.AUTHORIZATION), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).hasToString(expectedValue);
    }
}
