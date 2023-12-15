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
package org.kie.kogito.svg.dataindex;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.svg.ProcessSVGException;
import org.kie.kogito.svg.auth.SpringBootAuthHelper;
import org.kie.kogito.svg.auth.impl.JwtPrincipalAuthTokenReader;
import org.kie.kogito.svg.auth.impl.OIDCPrincipalAuthTokenReader;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpringBootDataIndexClientTest {

    private String PROCESS_INSTANCE_ID = "pId";
    private final static String jsonString = "{\n" +
            "  \"data\": {\n" +
            "    \"ProcessInstances\": [\n" +
            "      {\n" +
            "        \"id\": \"piId\",\n" +
            "        \"processId\": \"processId\",\n" +
            "        \"nodes\": [\n" +
            "          {\n" +
            "            \"definitionId\": \"_9861B686-DF6B-4B1C-B370-F9898EEB47FD\",\n" +
            "            \"exit\": \"2020-10-11T06:49:47.26Z\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"definitionId\": \"_8B62D3CA-5D03-4B2B-832B-126469288BB4\",\n" +
            "            \"exit\": null\n" +
            "          }\n" +
            "        ]\n" +
            "      } " +
            "    ]\n" +
            "  }\n" +
            "}";

    private SpringBootDataIndexClient client;
    final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        client = buildClient(Optional.empty());
    }

    private SpringBootDataIndexClient buildClient(Optional<SpringBootAuthHelper> authHelper) {
        return new SpringBootDataIndexClient("data-indexURL", restTemplate, objectMapper, authHelper);
    }

    @Test
    public void testGetNodeInstancesFromResponse() throws JsonProcessingException {
        JsonNode response = objectMapper.readTree(jsonString);
        List<NodeInstance> nodes = client.getNodeInstancesFromResponse(response);
        assertThat(nodes).hasSize(2).containsExactly(
                new NodeInstance(true, "_9861B686-DF6B-4B1C-B370-F9898EEB47FD"),
                new NodeInstance(false, "_8B62D3CA-5D03-4B2B-832B-126469288BB4"));
    }

    @Test
    public void testGetEmptyNodeInstancesFromResponse() throws JsonProcessingException {
        String emptyResults = "{ \"data\": { \"ProcessInstances\": [] }}";
        JsonNode response = objectMapper.readTree(emptyResults);
        List<NodeInstance> nodes = client.getNodeInstancesFromResponse(response);
        assertThat(nodes).isEmpty();
    }

    @Test
    public void testGetNodeInstancesFromProcessInstanceOkResponse() {
        when(restTemplate.postForEntity(eq("data-indexURL/graphql"), any(HttpEntity.class), eq(String.class))).thenReturn(ResponseEntity.ok(jsonString));

        List<NodeInstance> nodes = client.getNodeInstancesFromProcessInstance(PROCESS_INSTANCE_ID, "authHeader");
        assertThat(nodes).hasSize(2).containsExactly(
                new NodeInstance(true, "_9861B686-DF6B-4B1C-B370-F9898EEB47FD"),
                new NodeInstance(false, "_8B62D3CA-5D03-4B2B-832B-126469288BB4"));
    }

    @Test
    public void testGetNodeInstancesFromProcessInstance() {
        when(restTemplate.postForEntity(eq("data-indexURL/graphql"), any(HttpEntity.class), eq(String.class))).thenThrow(HttpClientErrorException.NotFound.class);
        assertThatThrownBy(() -> client.getNodeInstancesFromProcessInstance(PROCESS_INSTANCE_ID, "authHeader")).isInstanceOf(ProcessSVGException.class);
    }

    @Test
    public void testAuthHeaderWithSecurityContextOidcUserPrincipal() {
        String token = "testToken";
        SecurityContext securityContextMock = mock(SecurityContext.class);
        Authentication authenticationMock = mock(Authentication.class);
        OidcUser principalMock = mock(OidcUser.class);
        OidcIdToken tokenMock = mock(OidcIdToken.class);

        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(principalMock);
        when(principalMock.getIdToken()).thenReturn(tokenMock);
        when(tokenMock.getTokenValue()).thenReturn(token);

        SecurityContextHolder.setContext(securityContextMock);
        client = buildClient(Optional.of(new SpringBootAuthHelper(List.of(new OIDCPrincipalAuthTokenReader(), new JwtPrincipalAuthTokenReader()))));
        assertThat(client.getAuthHeader("")).isEqualTo("Bearer " + token);
    }

    @Test
    public void testAuthHeaderWithSecurityContextJwtPrincipal() {
        String token = "testToken";
        SecurityContext securityContextMock = mock(SecurityContext.class);
        Authentication authenticationMock = mock(Authentication.class);
        Jwt principalMock = mock(Jwt.class);

        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(principalMock);
        when(principalMock.getTokenValue()).thenReturn(token);

        SecurityContextHolder.setContext(securityContextMock);
        client = buildClient(Optional.of(new SpringBootAuthHelper(List.of(new OIDCPrincipalAuthTokenReader(), new JwtPrincipalAuthTokenReader()))));
        assertThat(client.getAuthHeader("")).isEqualTo("Bearer " + token);
    }

    @Test
    public void testAuthHeaderWithoutSecurityContext() {
        String authHeader = "Bearer testToken";
        client = buildClient(Optional.of(new SpringBootAuthHelper(List.of(new OIDCPrincipalAuthTokenReader(), new JwtPrincipalAuthTokenReader()))));
        assertThat(client.getAuthHeader(authHeader)).isEqualTo(authHeader);
    }
}
