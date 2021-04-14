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

package org.kie.kogito.taskassigning.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TaskAssigningServiceHelperTest {

    private static final String CONNECTOR_NAME = "CONNECTOR_NAME";

    @Mock
    private UserServiceConnectorRegistry connectorRegistry;

    @Mock
    private TaskAssigningConfig config;

    @Mock
    private UserServiceConnector connector;

    private TaskAssigningServiceHelper serviceHelper;

    @BeforeEach
    void setUp() {
        serviceHelper = new TaskAssigningServiceHelper();
        serviceHelper.userServiceConnectorRegistry = connectorRegistry;
        serviceHelper.config = config;
        doReturn(CONNECTOR_NAME).when(config).getUserServiceConnector();
    }

    @Test
    void validateAndGetUserServiceConnectorSuccessful() {
        doReturn(connector).when(connectorRegistry).get(CONNECTOR_NAME);
        assertThat(serviceHelper.validateAndGetUserServiceConnector()).isSameAs(connector);
    }

    @Test
    void validateAndGetUserServiceConnectorUnsuccessful() {
        Assertions.assertThatThrownBy(() -> serviceHelper.validateAndGetUserServiceConnector())
                .hasMessageStartingWith("No user service connector was found");
    }
}
