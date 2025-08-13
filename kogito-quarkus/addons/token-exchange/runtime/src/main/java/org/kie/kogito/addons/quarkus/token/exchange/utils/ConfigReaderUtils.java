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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkiverse.openapi.generator.providers.CredentialsContext;

/**
 * Utility class for reading configuration properties related to OAuth2 token exchange and caching.
 */
public final class ConfigReaderUtils {
    public static final String CANONICAL_TOKEN_EXCHANGE_ENABLED_PROPERTY_NAME = "sonataflow.security.auth.%s.token-exchange.enabled";
    private static final String CANONICAL_PROACTIVE_REFRESH_PROPERTY_NAME = "sonataflow.security.auth.%s.token-exchange.proactive-refresh-seconds";
    private static final String CANONICAL_MONITOR_RATE_PROPERTY_NAME = "sonataflow.security.auth.token-exchange.monitor-rate-seconds";
    public static final long DEFAULT_PROACTIVE_REFRESH_SECONDS = Duration.of(5, ChronoUnit.MINUTES).getSeconds();
    public static final long DEFAULT_MONITOR_RATE_SECONDS = Duration.of(1, ChronoUnit.MINUTES).getSeconds();

    private ConfigReaderUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Gets the canonical property name for proactive refresh configuration.
     * 
     * @param authName The authentication name
     * @return The property name in format: sonataflow.security.{authName}.token-exchange.proactive-refresh-seconds
     */
    public static String getCanonicalProactiveRefreshConfigPropertyName(String authName) {
        return String.format(CANONICAL_PROACTIVE_REFRESH_PROPERTY_NAME, authName);
    }

    /**
     * Gets the configured proactive refresh seconds for the given auth name.
     * 
     * @param authName The authentication name
     * @return The configured buffer seconds, or default (300) if not configured
     */
    public static long getProactiveRefreshSeconds(String authName) {
        return ConfigProvider.getConfig()
                .getOptionalValue(getCanonicalProactiveRefreshConfigPropertyName(authName), Long.class)
                .orElse(DEFAULT_PROACTIVE_REFRESH_SECONDS);
    }

    /**
     * Gets the token exchange enabled property value for the given auth name.
     * 
     * @param input The credentials context
     * @return The token exchange enabled property value
     */
    public static Optional<Boolean> getTokenExchangeEnabledPropertyValue(CredentialsContext input) {
        return ConfigProvider.getConfig().getOptionalValue(getCanonicalTokenExchangeEnabledConfigPropertyName(input.getAuthName()), Boolean.class);
    }

    /**
     * Gets the canonical token exchange enabled property name for the given auth name.
     * 
     * @param authName The authentication name
     * @return The property name in format: sonataflow.security.{authName}.token-exchange.enabled
     */
    public static String getCanonicalTokenExchangeEnabledConfigPropertyName(String authName) {
        return String.format(CANONICAL_TOKEN_EXCHANGE_ENABLED_PROPERTY_NAME, authName);
    }

    /**
     * Gets the configured monitor rate seconds.
     *
     * @return The configured monitor rate seconds, or default (60) if not configured
     */
    public static long getMonitorRateSeconds() {
        return ConfigProvider.getConfig()
                .getOptionalValue(CANONICAL_MONITOR_RATE_PROPERTY_NAME, Long.class)
                .orElse(DEFAULT_MONITOR_RATE_SECONDS);
    }
}