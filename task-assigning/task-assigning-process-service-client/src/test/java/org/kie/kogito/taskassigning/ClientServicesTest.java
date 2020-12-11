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

package org.kie.kogito.taskassigning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.index.service.client.DataIndexServiceClientFactory;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ClientServicesTest {

    @Mock
    private ProcessServiceClientFactory processServiceClientFactory;

    @Mock
    private DataIndexServiceClientFactory dataIndexServiceClientFactory;

    @Test
    void processServiceClientFactory() {
        ClientServices clientServices = new ClientServices(processServiceClientFactory, dataIndexServiceClientFactory);
        assertThat(clientServices.processServiceClientFactory()).isEqualTo(processServiceClientFactory);
        assertThat(clientServices.dataIndexClientFactory()).isEqualTo(dataIndexServiceClientFactory);
    }
}
