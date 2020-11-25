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

package org.kie.kogito.taskassigning.process.service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicAuthenticationCredentialsTest {

    private static final String USER = "USER";
    private static final String PASSWORD = "PASSWORD";

    private BasicAuthenticationCredentials credentials;

    @BeforeEach
    void setUp() {
        credentials = BasicAuthenticationCredentials.newBuilder()
                .user(USER)
                .password(PASSWORD)
                .build();
    }

    @Test
    void getUser() {
        assertThat(credentials.getUser()).isEqualTo(USER);
    }

    @Test
    void getPassword() {
        assertThat(credentials.getPassword()).isEqualTo(PASSWORD);
    }
}
