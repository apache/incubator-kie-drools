/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.process.service.client.AuthenticationCredentials;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractAuthenticationFilterProviderTest<T extends AuthenticationCredentials> {

    private AuthenticationFilterProvider<T> provider;

    @BeforeEach
    void setUp() {
        provider = createProvider();
    }

    abstract AuthenticationFilterProvider<T> createProvider();

    abstract Class<T> getType();

    abstract T getCredentials();

    @Test
    void getCredentialsType() {
        assertThat(provider.getCredentialsType()).isEqualTo(getType());
    }

    @Test
    void createInstance() {
        assertThat(provider.createInstance(getCredentials())).isNotNull();
    }
}
