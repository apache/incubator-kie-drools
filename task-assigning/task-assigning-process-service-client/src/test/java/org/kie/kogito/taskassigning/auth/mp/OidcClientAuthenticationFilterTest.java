/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.auth.mp;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OidcClientAuthenticationFilterTest {

    private static final String OIDC_ACCESS_TOKEN = "OIDC_ACCESS_TOKEN";

    @Mock
    private ClientRequestContext requestContext;

    @Mock
    private MultivaluedMap<String, Object> multivaluedMap;

    @Mock
    private TokenManager tokenManager;

    @Captor
    private ArgumentCaptor<Object> headerValueCaptor;

    @Test
    void filter() throws IOException {
        doReturn(multivaluedMap).when(requestContext).getHeaders();
        doReturn(OIDC_ACCESS_TOKEN).when(tokenManager).getAccessTokenString();
        String expectedValue = "Bearer " + OIDC_ACCESS_TOKEN;
        OidcClientAuthenticationFilter filter = new OidcClientAuthenticationFilter(tokenManager);
        filter.filter(requestContext);
        verify(multivaluedMap).add(eq(HttpHeaders.AUTHORIZATION), headerValueCaptor.capture());
        assertThat(headerValueCaptor.getValue()).hasToString(expectedValue);
    }
}
