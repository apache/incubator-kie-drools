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
package org.kie.kogito.quarkus.auth;

import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.auth.AuthTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.SecurityIdentity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusAuthTokenProvider implements AuthTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(QuarkusAuthTokenProvider.class);

    @Inject
    Instance<SecurityIdentity> identity;

    private Optional<SecurityIdentity> getIdentity() {
        return identity.isResolvable() ? Optional.of(identity.get()) : Optional.empty();
    }

    @Override
    public Optional<String> getAuthToken() {
        return getIdentity()
                .filter(securityIdentity -> !securityIdentity.isAnonymous())
                .map(securityIdentity -> securityIdentity.getCredential(TokenCredential.class))
                .filter(Objects::nonNull)
                .map(tokenCredential -> {
                    logger.debug("Token retrieved from Quarkus SecurityIdentity");
                    return tokenCredential.getToken();
                })
                .or(() -> {
                    logger.debug("No token available from SecurityIdentity");
                    return Optional.empty();
                });
    }
}
