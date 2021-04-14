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

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceConnectorRegistryTest {

    private static final String CONNECTOR1 = "CONNECTOR1";
    private static final String CONNECTOR2 = "CONNECTOR2";
    private static final String CONNECTOR3 = "CONNECTOR3";

    @Mock
    private Instance<UserServiceConnector> connectorsInstance;

    @Test
    void duplicatedConnectorError() {
        List<UserServiceConnector> connectorList = Arrays.asList(mockConnector(CONNECTOR1),
                mockConnector(CONNECTOR2),
                mockConnector(CONNECTOR1));
        doReturn(connectorList.stream()).when(connectorsInstance).stream();
        Assertions.assertThatThrownBy(() -> new UserServiceConnectorRegistry(connectorsInstance))
                .hasMessageStartingWith("Two different connectors for the same name: " + CONNECTOR1);
    }

    @Test
    void get() {
        List<UserServiceConnector> connectorList = Arrays.asList(mockConnector(CONNECTOR1),
                mockConnector(CONNECTOR2),
                mockConnector(CONNECTOR3));
        doReturn(connectorList.stream()).when(connectorsInstance).stream();
        UserServiceConnectorRegistry registry = new UserServiceConnectorRegistry(connectorsInstance);
        assertThat(registry.get(CONNECTOR1)).isSameAs(connectorList.get(0));
        assertThat(registry.get(CONNECTOR2)).isSameAs(connectorList.get(1));
        assertThat(registry.get(CONNECTOR3)).isSameAs(connectorList.get(2));
    }

    private static UserServiceConnector mockConnector(String name) {
        UserServiceConnector result = mock(UserServiceConnector.class);
        doReturn(name).when(result).getName();
        return result;
    }
}
