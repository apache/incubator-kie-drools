/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.user.service.UserServiceConnectorQualifier;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceConnectorQualifierImplTest {

    private static final String CONNECTOR_NAME = "CONNECTOR_NAME";

    private UserServiceConnectorQualifierImpl qualifier;

    @BeforeEach
    void setUp() {
        qualifier = new UserServiceConnectorQualifierImpl(CONNECTOR_NAME);
    }

    @Test
    void getValue() {
        assertThat(qualifier.value()).isEqualTo(CONNECTOR_NAME);
    }

    @Test
    void annotationType() {
        assertThat(qualifier.annotationType()).isEqualTo(UserServiceConnectorQualifier.class);
    }

    @Test
    void equals() {
        assertThat(qualifier)
                .isEqualTo(new UserServiceConnectorQualifierImpl(CONNECTOR_NAME))
                .isNotEqualTo(new UserServiceConnectorQualifierImpl("Another name"));
    }

    @Test
    void hashCodeTest() {
        assertThat(qualifier).hasSameHashCodeAs(new UserServiceConnectorQualifierImpl(CONNECTOR_NAME));
    }
}
