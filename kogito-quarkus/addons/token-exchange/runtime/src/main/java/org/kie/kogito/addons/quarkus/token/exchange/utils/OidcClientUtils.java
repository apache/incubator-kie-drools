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
package org.kie.kogito.addons.quarkus.token.exchange.utils;

import io.quarkus.arc.Arc;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.runtime.configuration.ConfigurationException;

/**
 * Utility class for OIDC client operations in OAuth2 token exchange.
 */
public final class OidcClientUtils {

    private OidcClientUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets the OIDC client for token exchange operations.
     * 
     * @param authName The authentication name to get the client for
     * @return The OIDC client for the specified auth name
     * @throws ConfigurationException if no client is found
     */
    public static OidcClient getExchangeTokenClient(String authName) {
        OidcClients clients = Arc.container().instance(OidcClients.class).get();
        if (clients == null) {
            throw new ConfigurationException("No OIDC client was found. Hint: make sure the dependency io.quarkus:quarkus-oidc-client is provided and/or configure it in the properties.");
        }

        OidcClient exchangeTokenClient = clients.getClient(authName);
        if (exchangeTokenClient == null) {
            throw new ConfigurationException("No OIDC client was found for %s. Hint: configure it in the properties.".formatted(authName));
        }
        return exchangeTokenClient;
    }

    /**
     * Gets the exchange token property for the given grant type.
     * 
     * @param exchangeTokenGrantType The grant type to get the property for
     * @return The exchange token property
     * @throws ConfigurationException if the grant type is not supported
     */
    public static String getExchangeTokenProperty(OidcClientConfig.Grant.Type exchangeTokenGrantType) {
        String exchangeTokenProperty;

        if (exchangeTokenGrantType == OidcClientConfig.Grant.Type.EXCHANGE) {
            exchangeTokenProperty = "subject_token";
        } else if (exchangeTokenGrantType == OidcClientConfig.Grant.Type.JWT) {
            exchangeTokenProperty = "assertion";
        } else {
            throw new ConfigurationException("Token exchange is required but OIDC client is configured to use the %s grantType".formatted(exchangeTokenGrantType.getGrantType()));
        }
        return exchangeTokenProperty;
    }
}