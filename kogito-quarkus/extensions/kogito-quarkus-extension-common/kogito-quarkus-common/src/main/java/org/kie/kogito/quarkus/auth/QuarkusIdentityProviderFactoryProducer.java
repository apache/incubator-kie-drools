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

import java.util.List;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviderFactory;
import org.kie.kogito.auth.impl.IdentityProviderFactoryImpl;
import org.kie.kogito.auth.impl.KogitoAuthConfig;
import org.kie.kogito.quarkus.config.KogitoRuntimeConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusIdentityProviderFactoryProducer {

    private final KogitoRuntimeConfig config;
    private final IdentityProvider identityProvider;

    QuarkusIdentityProviderFactoryProducer() {
        this(null, null);
    }

    @Inject
    public QuarkusIdentityProviderFactoryProducer(KogitoRuntimeConfig config, IdentityProvider identityProvider) {
        this.config = config;
        this.identityProvider = identityProvider;
    }

    @Produces
    public IdentityProviderFactory get() {
        String[] rolesThatAllowImpersonation = config.authConfig().rolesThatAllowImpersonation().map(value -> value.split(",")).orElse(new String[] {});

        return new IdentityProviderFactoryImpl(identityProvider, new KogitoAuthConfig(config.authConfig().enabled(), List.of(rolesThatAllowImpersonation)));
    }
}
