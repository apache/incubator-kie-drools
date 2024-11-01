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

package org.kie.kogito.spring.auth;

import java.util.List;
import java.util.Optional;

import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.auth.IdentityProviderFactory;
import org.kie.kogito.auth.impl.IdentityProviderFactoryImpl;
import org.kie.kogito.auth.impl.KogitoAuthConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringIdentityProviderFactoryProducer {

    @Bean
    public IdentityProviderFactory produce(IdentityProvider identityProvider,
            @Value("${" + IdentityProviderFactory.KOGITO_SECURITY_AUTH_ENABLED + ":false}") boolean enabled,
            @Value("${" + IdentityProviderFactory.KOGITO_SECURITY_AUTH_IMPERSONATION_ALLOWED_FOR_ROLES + ":#{null}}") Optional<String> configRolesThatAllowImpersonation) {
        String[] rolesThatAllowImpersonation = configRolesThatAllowImpersonation.map(roles -> roles.split(",")).orElse(new String[] {});
        return new IdentityProviderFactoryImpl(identityProvider, new KogitoAuthConfig(enabled, List.of(rolesThatAllowImpersonation)));
    }
}
