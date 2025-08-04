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
package org.kie.kogito.serverless.workflow.openapi;

import java.util.Collections;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.ConfigCredentialsProvider;
import io.quarkiverse.openapi.generator.providers.CredentialsContext;
import io.quarkus.arc.Arc;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClientException;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.oidc.client.Tokens;
import io.quarkus.runtime.configuration.ConfigurationException;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Specializes;
import jakarta.ws.rs.core.HttpHeaders;

import static io.quarkiverse.openapi.generator.providers.AbstractAuthProvider.getHeaderName;

@RequestScoped
@Alternative
@Specializes
@Priority(200)
public class OpenApiCustomCredentialProvider extends ConfigCredentialsProvider {
    private static final String CANONICAL_EXCHANGE_TOKEN_PROPERTY_NAME = "sonataflow.security.auth.%s.exchange-token";

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiCustomCredentialProvider.class);

    @Override
    public Optional<String> getOauth2BearerToken(CredentialsContext input) {
        LOGGER.debug("Calling OpenApiCustomCredentialProvider.getOauth2BearerToken for {}", input.getAuthName());
        String authorizationHeaderName = Optional.ofNullable(getHeaderName(input.getOpenApiSpecId(), input.getAuthName())).orElse(HttpHeaders.AUTHORIZATION);
        boolean exchangeToken = ConfigProvider.getConfig().getOptionalValue(getCanonicalExchangeTokenConfigPropertyName(input.getAuthName()), Boolean.class).orElse(false);
        if (exchangeToken) {
            String accessToken = input.getRequestContext().getHeaderString(authorizationHeaderName);

            if (ConversionUtils.isEmpty(accessToken)) {
                throw new ConfigurationException("An access token is required in the header %s (default is %s) but none was provided".formatted(authorizationHeaderName, HttpHeaders.AUTHORIZATION));
            }

            LOGGER.info("Oauth2 token exchange enabled for {}, will generate a tokens...", input.getAuthName());
            OidcClients clients = Arc.container().instance(OidcClients.class).get();
            if (clients == null) {
                throw new ConfigurationException("No OIDC client was found. Hint: make sure the dependency io.quarkus:quarkus-oidc-client is provided and/or configure it in the properties.");
            }

            OidcClient exchangeTokenClient = clients.getClient(input.getAuthName());
            if (exchangeTokenClient == null) {
                throw new ConfigurationException("No OIDC client was found for %s. Hint: configure it in the properties.".formatted(input.getAuthName()));
            }
            return Optional.of(exchangeTokenIfNeeded(accessToken, exchangeTokenClient, input.getAuthName()));
        }
        return Optional.empty();
    }

    private String exchangeTokenIfNeeded(String token, OidcClient exchangeTokenClient, String authName) {
        OidcClientConfig.Grant.Type exchangeTokenGrantType = ConfigProvider.getConfig().getValue("quarkus.oidc-client.%s.grant.type".formatted(authName), OidcClientConfig.Grant.Type.class);
        try {
            Tokens tokens = exchangeTokenClient.getTokens(Collections.singletonMap(getExchangeTokenProperty(exchangeTokenGrantType), token)).await().indefinitely();

            //TODO store the refresh token in an expiring cache
            //TODO store the access token in an expiring cache
            //TODO cache should expire before access/refresh token expire so they can be refreshed before (need to decode the JWT claim)
            return tokens.getAccessToken();
        } catch (OidcClientException e) {
            // TODO try to refresh the access token with the cached refresh token
            LOGGER.error("Error while exchanging oauth2 token. The provided input token will be used without exchange.", e);
        }

        return token;
    }

    private static String getExchangeTokenProperty(OidcClientConfig.Grant.Type exchangeTokenGrantType) {
        switch (exchangeTokenGrantType) {
            case EXCHANGE:
                return "subject_token";
            case JWT:
                return "assertion";
        }
        throw new ConfigurationException("Token exchange is required but OIDC client is configured to use the %s grantType".formatted(exchangeTokenGrantType.getGrantType()));

    }

    public static String getCanonicalExchangeTokenConfigPropertyName(String authName) {
        return String.format(CANONICAL_EXCHANGE_TOKEN_PROPERTY_NAME, authName);
    }
}
