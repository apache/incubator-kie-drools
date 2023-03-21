/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.jobs.service.embedded.deployment;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KogitoAddonsQuarkusJobsServiceEmbeddedProcessorTest {

    private static final String JOBS_SERVICE_URL = "kogito.jobs-service.url";
    private static final String SERVICE_URL = "kogito.service.url";

    private KogitoAddonsQuarkusJobsServiceEmbeddedProcessor processor;

    @Mock
    private BuildProducer<SystemPropertyBuildItem> systemPropertyBuildItemBuildProducer;
    @Captor
    private ArgumentCaptor<SystemPropertyBuildItem> systemPropertyBuildItemCaptor;

    @BeforeEach
    void setUp() {
        processor = new KogitoAddonsQuarkusJobsServiceEmbeddedProcessor();
    }

    @Test
    void feature() {
        FeatureBuildItem feature = processor.feature();
        assertThat(feature.getName()).isEqualTo("kogito-addons-quarkus-jobs-service-embedded");
    }

    @Test
    void buildConfiguration() {
        processor.buildConfiguration(systemPropertyBuildItemBuildProducer);
        verify(systemPropertyBuildItemBuildProducer, times(3)).produce(systemPropertyBuildItemCaptor.capture());
        List<SystemPropertyBuildItem> items = systemPropertyBuildItemCaptor.getAllValues();
        assertThat(items)
                .anyMatch(item -> JOBS_SERVICE_URL.equals(item.getKey()) && ("${" + SERVICE_URL + "}").equals(item.getValue()))
                .anyMatch(item -> SERVICE_URL.equals(item.getKey()) && "http://${quarkus.http.host}:${quarkus.http.port}".equals(item.getValue()));
    }
}
