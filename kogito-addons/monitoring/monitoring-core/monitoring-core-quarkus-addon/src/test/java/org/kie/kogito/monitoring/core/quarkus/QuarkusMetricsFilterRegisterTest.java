/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.monitoring.core.quarkus;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.FeatureContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class QuarkusMetricsFilterRegisterTest {

    @Test
    public void configure() {
        commonConfigure(true, 1);
        commonConfigure(false, 0);
    }

    private void commonConfigure(boolean httpInterceptorUseDefault, int numberOfTimes) {
        FeatureContext contextMock = mock(FeatureContext.class);
        QuarkusMetricsFilterRegister filterRegister = new QuarkusMetricsFilterRegister();

        filterRegister.setHttpInterceptorUseDefault(httpInterceptorUseDefault);
        filterRegister.configure(null, contextMock);

        final ArgumentCaptor<Object> registerCaptor = ArgumentCaptor.forClass(Object.class);

        verify(contextMock, times(numberOfTimes)).register(registerCaptor.capture());

        if(httpInterceptorUseDefault) {
            List<Object> values = registerCaptor.getAllValues();
            assertThat(values.isEmpty()).isFalse();
            assertThat(values.size()).isEqualTo(1);
            assertThat(values.get(0)).isInstanceOf(QuarkusMetricsInterceptor.class);
        } else {
            assertThat(registerCaptor.getAllValues().isEmpty()).isTrue();
        }
    }

}