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

package org.kie.addons.springboot.auth;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.addons.springboot.auth.impl.JwtPrincipalAuthTokenReader;
import org.kie.addons.springboot.auth.impl.OIDCPrincipalAuthTokenReader;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.addons.springboot.auth.SpringBootAuthTokenHelper.BEARER_TOKEN_TEMPLATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpringBootAuthTokenHelperTest {

    private static final String AUTH_TOKEN = "this is the token";

    @Spy
    private JwtPrincipalAuthTokenReader jwtPrincipalAuthTokenReader;

    @Spy
    private OIDCPrincipalAuthTokenReader oidcPrincipalAuthTokenReader;

    private SpringBootAuthTokenHelper springBootAuthTokenHelper;

    @BeforeEach
    public void setup() {
        this.springBootAuthTokenHelper = new SpringBootAuthTokenHelper(List.of(jwtPrincipalAuthTokenReader, oidcPrincipalAuthTokenReader));
    }

    @Test
    public void testReadTokenWithoutSecurityContext() {
        assertThat(springBootAuthTokenHelper.getAuthToken())
                .isEmpty();

        verify(jwtPrincipalAuthTokenReader, never()).readToken(any());
        verify(oidcPrincipalAuthTokenReader, never()).readToken(any());
    }

    @Test
    public void testReadJwtToken() {
        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue()).thenReturn(AUTH_TOKEN);

        initSecurityContext(jwt);

        assertThat(springBootAuthTokenHelper.getAuthToken())
                .hasValue(BEARER_TOKEN_TEMPLATE.formatted(AUTH_TOKEN));

        verify(jwtPrincipalAuthTokenReader, times(1)).readToken(any());
        verify(oidcPrincipalAuthTokenReader, never()).readToken(any());
    }

    @Test
    public void testReadOidcUser() {
        OidcIdToken token = mock(OidcIdToken.class);
        when(token.getTokenValue()).thenReturn(AUTH_TOKEN);

        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getIdToken()).thenReturn(token);

        initSecurityContext(oidcUser);

        assertThat(springBootAuthTokenHelper.getAuthToken())
                .hasValue(BEARER_TOKEN_TEMPLATE.formatted(AUTH_TOKEN));

        verify(jwtPrincipalAuthTokenReader, never()).readToken(any());
        verify(oidcPrincipalAuthTokenReader, times(1)).readToken(any());
    }

    private void initSecurityContext(Object principal) {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
