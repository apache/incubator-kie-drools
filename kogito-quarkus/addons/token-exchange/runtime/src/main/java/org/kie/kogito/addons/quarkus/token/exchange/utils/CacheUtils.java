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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.providers.CredentialsContext;

public final class CacheUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

    private CacheUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Builds cache key in format: processInstanceId:authName
     * 
     * @param input The credentials context containing request headers and auth name
     * @return Cache key in format processInstanceId:authName
     * @throws IllegalStateException if kogitoprocinstanceid header is missing
     */
    public static String buildCacheKey(CredentialsContext input) {
        String authName = input.getAuthName();
        String processInstanceId = input.getRequestContext().getHeaderString("kogitoprocinstanceid");

        if (processInstanceId == null || processInstanceId.isEmpty()) {
            throw new IllegalStateException(
                    "OAuth2 token exchange requires 'kogitoprocinstanceid' header but it was not found in the request. " +
                            "This header should be automatically set by the OpenAPI work item handler during serverless workflow execution. " +
                            "Auth context: '" + authName + "'. " +
                            "Please verify that the request is being made from within a serverless workflow process.");
        }

        String cacheKey = processInstanceId + ":" + authName;
        LOGGER.debug("Built cache key: '{}' for auth '{}' and process instance '{}'", cacheKey, authName, processInstanceId);
        return cacheKey;
    }

    /**
     * Builds cache key in format: processInstanceId:authName
     * 
     * @param processInstanceId The process instance ID
     * @param authName The authentication name
     * @return Cache key in format processInstanceId:authName
     */
    public static String buildCacheKey(String processInstanceId, String authName) {
        String cacheKey = processInstanceId + ":" + authName;
        LOGGER.debug("Built cache key: '{}' for auth '{}' and process instance '{}'", cacheKey, authName, processInstanceId);
        return cacheKey;
    }

    /**
     * Extracts authName from cache key in format: processInstanceId:authName
     * 
     * @param cacheKey The cache key
     * @return The extracted authName
     */
    public static String extractAuthNameFromCacheKey(String cacheKey) {
        int colonIndex = cacheKey.indexOf(':');
        if (colonIndex == -1) {
            LOGGER.warn("Cache key '{}' does not contain ':' separator, treating as authName", cacheKey);
            return cacheKey;
        }
        return cacheKey.substring(colonIndex + 1);
    }

    /**
     * Extracts processInstanceId from cache key in format: processInstanceId:authName
     * 
     * @param cacheKey The cache key
     * @return The extracted processInstanceId
     */
    public static String extractProcessInstanceIdFromCacheKey(String cacheKey) {
        int colonIndex = cacheKey.indexOf(':');
        if (colonIndex == -1) {
            LOGGER.warn("Cache key '{}' does not contain ':' separator, cannot extract process instance ID", cacheKey);
            return null;
        }
        return cacheKey.substring(0, colonIndex);
    }

}
