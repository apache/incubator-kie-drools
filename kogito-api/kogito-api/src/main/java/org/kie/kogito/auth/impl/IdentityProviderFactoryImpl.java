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

package org.kie.kogito.auth.impl;

import java.util.Collection;
import java.util.Collections;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviderFactory;
import org.kie.kogito.auth.IdentityProviders;

public class IdentityProviderFactoryImpl implements IdentityProviderFactory {

    private final IdentityProvider identityProvider;
    private final KogitoAuthConfig config;

    public IdentityProviderFactoryImpl(IdentityProvider identityProvider, KogitoAuthConfig config) {
        this.identityProvider = identityProvider;
        this.config = config;
    }

    @Override
    public IdentityProvider getOrImpersonateIdentity(String user, Collection<String> roles) {

        if (!config.isEnabled()) {
            return IdentityProviders.of(user, roles);
        }

        if (!Collections.disjoint(config.getRolesThatAllowImpersonation(), identityProvider.getRoles())
                && user != null && !user.isBlank()
                && !identityProvider.getName().equals(user)) {
            return IdentityProviders.of(user, roles);
        }

        return identityProvider;
    }

    @Override
    public IdentityProvider getIdentity(String user, Collection<String> roles) {

        if (!config.isEnabled()) {
            return IdentityProviders.of(user, roles);
        }
        return identityProvider;
    }
}
