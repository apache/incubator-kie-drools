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

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceConnectorProducerTest {

    private static final String CONNECTOR_NAME = "CONNECTOR_NAME";

    @Mock
    private TaskAssigningConfig config;

    @Mock
    private Instance<UserServiceConnector> userServiceConnectorInstance;

    @Mock
    private Instance<UserServiceConnector> userServiceConnectorProvider;

    @Mock
    private UserServiceConnector userServiceConnector;

    @Captor
    private ArgumentCaptor<UserServiceConnectorQualifierImpl> qualifierCaptor;

    private UserServiceConnectorProducer producer;

    @BeforeEach
    void setUp() {
        producer = new UserServiceConnectorProducer();
        producer.config = config;
        producer.userServiceConnectorInstance = userServiceConnectorInstance;
        doReturn(CONNECTOR_NAME).when(config).getUserServiceConnector();
    }

    @Test
    void initSuccessful() {
        prepareInitSuccessful();
        producer.init();
        verify(userServiceConnectorInstance).select(qualifierCaptor.capture());
        assertThat(qualifierCaptor).isNotNull();
        assertThat(qualifierCaptor.getValue().value()).isEqualTo(CONNECTOR_NAME);
    }

    private void prepareInitSuccessful() {
        doReturn(userServiceConnectorProvider).when(userServiceConnectorInstance).select(any(UserServiceConnectorQualifierImpl.class));
        doReturn(userServiceConnector).when(userServiceConnectorProvider).get();
    }

    @Test
    void initWithFailure() {
        doReturn(userServiceConnectorProvider).when(userServiceConnectorInstance).select(any(UserServiceConnectorQualifierImpl.class));
        doReturn(null).when(userServiceConnectorProvider).get();
        Assertions.assertThatThrownBy(() -> producer.init())
                .hasMessageStartingWith("No user service connector was found for the configured value %s = %s",
                        TaskAssigningConfigProperties.USER_SERVICE_CONNECTOR,
                        CONNECTOR_NAME);
    }

    @Test
    void userServiceConnector() {
        prepareInitSuccessful();
        producer.init();
        assertThat(producer.userServiceConnector()).isSameAs(userServiceConnector);
    }
}