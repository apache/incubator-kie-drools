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
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;

public class IdentityProviderFactoryImplTest {

    private static final String KOGITO_IDENTITY_USER = "john";
    private static final Collection<String> KOGITO_IDENTITY_ROLES = List.of("IT", "task-operator");
    private static final Collection<String> KOGITO_IDENTITY_IMPERSONATOR_ROLES = List.of("root", "task-admin");
    private static final String TEST_USER = "katty";
    private static final Collection<String> TEST_ROLES = List.of("HR", "task-operator");

    @Test
    public void testResolveIdentityWithAuthDisabled() {
        KogitoAuthConfig config = new KogitoAuthConfig(false, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", TEST_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(TEST_ROLES));
    }

    @Test
    public void testResolveIdentityWithAuthEnabled() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_ROLES));
    }

    @Test
    public void testResolveImpersonatedIdentityWithAuthEnabled() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", TEST_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(TEST_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithNullUser() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(null, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_IMPERSONATOR_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithBlankUser() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity("  ", TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_IMPERSONATOR_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithSameUser() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(KOGITO_IDENTITY_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_IMPERSONATOR_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithoutImpersonationRole() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithNullRoles() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, null))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", TEST_USER)
                .matches(identityProvider -> identityProvider.getRoles().isEmpty());
    }

    @Test
    public void testGetOrImpersonateIdentityWithEmptyRoles() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_IMPERSONATOR_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, List.of()))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", TEST_USER)
                .matches(identityProvider -> identityProvider.getRoles().isEmpty());
    }

    @Test
    public void testGetOrImpersonateIdentityWithPartialImpersonationRole() {
        Collection<String> partialRoles = List.of("IT", "task-admin"); // task-admin is an impersonation role
        KogitoAuthConfig config = new KogitoAuthConfig(true, KOGITO_IDENTITY_IMPERSONATOR_ROLES);
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, partialRoles), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", TEST_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(TEST_ROLES));
    }

    @Test
    public void testGetOrImpersonateIdentityWithEmptyImpersonationRolesConfig() {
        KogitoAuthConfig config = new KogitoAuthConfig(true, List.of());
        IdentityProviderFactoryImpl identityProviderFactory = new IdentityProviderFactoryImpl(
                IdentityProviders.of(KOGITO_IDENTITY_USER, KOGITO_IDENTITY_ROLES), config);

        Assertions.assertThat(identityProviderFactory.getOrImpersonateIdentity(TEST_USER, TEST_ROLES))
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", KOGITO_IDENTITY_USER)
                .matches(identityProvider -> identityProvider.getRoles().containsAll(KOGITO_IDENTITY_ROLES));
    }

}
