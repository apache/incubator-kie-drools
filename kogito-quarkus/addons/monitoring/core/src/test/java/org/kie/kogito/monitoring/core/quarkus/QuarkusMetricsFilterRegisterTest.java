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
package org.kie.kogito.monitoring.core.quarkus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.monitoring.core.common.mock.MockedConfigBean;
import org.mockito.ArgumentCaptor;

import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.core.FeatureContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuarkusMetricsFilterRegisterTest {

    @Test
    public void configure() {
        commonConfigure(true, 1);
        commonConfigure(false, 0);
    }

    private void commonConfigure(boolean httpInterceptorUseDefault, int numberOfTimes) {
        FeatureContext contextMock = mock(FeatureContext.class);
        QuarkusMetricsFilterRegister filterRegister = new QuarkusMetricsFilterRegister(new MockedConfigBean());

        @SuppressWarnings("unchecked")
        Instance<Boolean> instanceHttpInterceptorUseDefault = mock(Instance.class);
        when(instanceHttpInterceptorUseDefault.isResolvable()).thenReturn(true);
        when(instanceHttpInterceptorUseDefault.get()).thenReturn(httpInterceptorUseDefault);

        filterRegister.setHttpInterceptorUseDefault(instanceHttpInterceptorUseDefault);
        filterRegister.configure(null, contextMock);

        final ArgumentCaptor<Object> registerCaptor = ArgumentCaptor.forClass(Object.class);

        verify(contextMock, times(numberOfTimes)).register(registerCaptor.capture());

        if (httpInterceptorUseDefault) {
            List<Object> values = registerCaptor.getAllValues();
            assertThat(values).hasSize(1);
            assertThat(values.get(0)).isInstanceOf(QuarkusMetricsInterceptor.class);
        } else {
            assertThat(registerCaptor.getAllValues()).isEmpty();
        }
    }

}
