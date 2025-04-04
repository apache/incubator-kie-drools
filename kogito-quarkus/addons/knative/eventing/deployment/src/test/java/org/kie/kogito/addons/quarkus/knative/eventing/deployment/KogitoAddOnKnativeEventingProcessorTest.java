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
package org.kie.kogito.addons.quarkus.knative.eventing.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.junit.jupiter.api.Test;
import org.kie.kogito.addons.quarkus.knative.eventing.KSinkInjectionHealthCheck;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.mockito.ArgumentCaptor;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.SystemPropertyBuildItem;
import io.quarkus.smallrye.health.deployment.spi.HealthBuildItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSourceFactory.INCLUDE_PROCESS_EVENTS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class KogitoAddOnKnativeEventingProcessorTest {

    @Test
    void checkKogitoFileIsGeneratedWithDefaultConfig() {
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        ces.add(new CloudEventMeta("myConsumedEvent", "/local/test", EventKind.CONSUMED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertTrue(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertTrue(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedOnlyProduced() {
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertTrue(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertFalse(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedWithKogitoSource() {
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        eventingProcessor.config.generateKogitoSource = true;
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertFalse(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertTrue(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertFalse(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedOnlyConsumed() {
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myConsumedEvent", "/local/test", EventKind.CONSUMED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertFalse(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertTrue(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkNotProducedIfNoCEs() {
        final Set<CloudEventMeta> ces = new HashSet<>();
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(Optional.of(resourcesMetadataBuildItem), producer);

        assertNull(producer.getItem());
    }

    @Test
    void checkProcessEventsWithEventPublisher() {
        BuildProducer<SystemPropertyBuildItem> buildProducer = mock(BuildProducer.class);
        IndexView indexView = mock(IndexView.class);
        CombinedIndexBuildItem combinedIndex = new CombinedIndexBuildItem(indexView, null);
        ArgumentCaptor<SystemPropertyBuildItem> systemPropertyCaptor = ArgumentCaptor.forClass(SystemPropertyBuildItem.class);
        doReturn(ClassInfo.create(DotName.createSimple("MyEventPublisherClass"), null, (short) 1, new DotName[] {}, Collections.emptyMap(), false)).when(indexView)
                .getClassByName(KogitoAddOnKnativeEventingProcessor.PROCESS_EVENTS_PUBLISHER_CLASS);

        KogitoAddOnKnativeEventingProcessor eventingProcessor = new KogitoAddOnKnativeEventingProcessor();
        eventingProcessor.checkProcessEvents(buildProducer, combinedIndex);
        verify(buildProducer).produce(systemPropertyCaptor.capture());
        assertThat(systemPropertyCaptor.getValue().getKey()).isEqualTo(INCLUDE_PROCESS_EVENTS);
        assertThat(systemPropertyCaptor.getValue().getValue()).isEqualTo("true");
    }

    @Test
    void checkProcessEventsWithoutEventPublisher() {
        BuildProducer<SystemPropertyBuildItem> buildProducer = mock(BuildProducer.class);
        IndexView indexView = mock(IndexView.class);
        CombinedIndexBuildItem combinedIndex = new CombinedIndexBuildItem(indexView, null);

        KogitoAddOnKnativeEventingProcessor eventingProcessor = new KogitoAddOnKnativeEventingProcessor();
        eventingProcessor.checkProcessEvents(buildProducer, combinedIndex);
        verify(buildProducer, never()).produce(any(SystemPropertyBuildItem.class));
    }

    @Test
    void registerKSinkInjectionHealthCheckWithProducedEvents() {
        Set<? extends CloudEventMeta> cloudEvents = Collections.singleton(new CloudEventMeta("my_type", "my_source", EventKind.PRODUCED));
        registerKSinkInjectionHealthCheck(cloudEvents, true);
    }

    @Test
    void registerKSinkInjectionHealthCheckWithoutEvents() {
        registerKSinkInjectionHealthCheck(Collections.emptySet(), false);
    }

    @Test
    void registerKSinkInjectionHealthCheckWithOnlyConsumedEvents() {
        Set<? extends CloudEventMeta> cloudEvents = Collections.singleton(new CloudEventMeta("my_type", "my_source", EventKind.CONSUMED));
        registerKSinkInjectionHealthCheck(cloudEvents, false);
    }

    private static void registerKSinkInjectionHealthCheck(Set<? extends CloudEventMeta> cloudEvents, boolean expectedIsEnabled) {
        KogitoKnativeResourcesMetadataBuildItem metadata = new KogitoKnativeResourcesMetadataBuildItem(cloudEvents, null);
        KogitoAddOnKnativeEventingProcessor eventingProcessor = new KogitoAddOnKnativeEventingProcessor();
        HealthBuildItem healthBuildItem = eventingProcessor.registerKSinkInjectionHealthCheck(Optional.of(metadata));
        assertThat(healthBuildItem.getHealthCheckClass()).isEqualTo(KSinkInjectionHealthCheck.class.getName());
        assertThat(healthBuildItem.isEnabled()).isEqualTo(expectedIsEnabled);
    }

    private KogitoAddOnKnativeEventingProcessor buildTestProcessorWithDefaultConfig() {
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = new KogitoAddOnKnativeEventingProcessor();
        final SinkConfiguration sinkConfiguration = new SinkConfiguration();
        sinkConfiguration.apiVersion = SinkConfiguration.DEFAULT_SINK_API_VERSION;
        sinkConfiguration.kind = SinkConfiguration.DEFAULT_SINK_KIND;
        sinkConfiguration.name = SinkConfiguration.DEFAULT_SINK_NAME;
        sinkConfiguration.namespace = Optional.empty();

        eventingProcessor.config = new EventingConfiguration();
        eventingProcessor.config.autoGenerateBroker = true;
        eventingProcessor.config.generateKogitoSource = false;
        eventingProcessor.config.broker = "default";
        eventingProcessor.config.sink = sinkConfiguration;

        return eventingProcessor;
    }

    private static final class MockGeneratedFSProducer implements BuildProducer<GeneratedResourceBuildItem> {

        private GeneratedResourceBuildItem item;

        @Override
        public void produce(GeneratedResourceBuildItem item) {
            this.item = item;
        }

        public GeneratedResourceBuildItem getItem() {
            return item;
        }
    }
}
