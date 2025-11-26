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
package org.kie.kogito.addons.jwt;

import java.util.Base64;

import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * JWT Token Parser utility for extracting claims from JWT tokens
 * Used by SonataFlow workflows to parse JWT tokens and access claims
 */
public class JwtTokenParser {

    private static final String BEARER = "Bearer ";

    /**
     * Parses a JWT token and returns the payload as a JsonNode
     * 
     * @param token The JWT token string (can include "Bearer " prefix)
     * @return JsonNode containing the JWT payload/claims
     * @throws RuntimeException if token parsing fails
     */
    public JsonNode parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }

        // Remove "Bearer " prefix if present
        String cleanToken = token.startsWith(BEARER) ? token.substring(BEARER.length()) : token;

        try {
            // Parse JWT token without signature verification (for claim extraction only)
            // In production, you might want to verify signatures with proper keys
            String[] parts = cleanToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT token format");
            }

            // Decode the payload (second part) using Base64
            StringBuilder payloadBuilder = new StringBuilder(parts[1]);
            // Add padding if necessary using StringBuilder for efficiency
            while (payloadBuilder.length() % 4 != 0) {
                payloadBuilder.append('=');
            }

            // Parse the JSON payload directly from bytes using ObjectMapperFactory singleton
            return ObjectMapperFactory.get().readTree(Base64.getUrlDecoder().decode(payloadBuilder.toString()));

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts a specific claim from a JWT token
     * 
     * @param token The JWT token string
     * @param claimName The name of the claim to extract
     * @return The claim value as a JsonNode, or null if not found
     */
    public JsonNode extractClaim(String token, String claimName) {
        JsonNode payload = parseToken(token);
        return payload.get(claimName);
    }

    /**
     * Extracts user information from standard JWT claims
     * 
     * @param token The JWT token string
     * @return JsonNode containing user info (sub, preferred_username, email, etc.)
     */
    public JsonNode extractUser(String token) {
        JsonNode payload = parseToken(token);
        ObjectNode userInfo = ObjectMapperFactory.get().createObjectNode();

        // Standard JWT claims for user identification
        if (payload.has("sub")) {
            userInfo.set("sub", payload.get("sub"));
        }
        if (payload.has("preferred_username")) {
            userInfo.set("preferred_username", payload.get("preferred_username"));
        }
        if (payload.has("email")) {
            userInfo.set("email", payload.get("email"));
        }
        if (payload.has("name")) {
            userInfo.set("name", payload.get("name"));
        }
        if (payload.has("given_name")) {
            userInfo.set("given_name", payload.get("given_name"));
        }
        if (payload.has("family_name")) {
            userInfo.set("family_name", payload.get("family_name"));
        }

        return userInfo;
    }
}
