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
package org.kie.kogito.addons.quarkus.knative.eventing.deployment;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.builditem.GeneratedFileSystemResourceBuildItem;
import io.quarkus.deployment.pkg.builditem.OutputTargetBuildItem;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KogitoAddOnKnativeEventingProcessorTest {

    @Test
    void checkKogitoFileIsGeneratedWithDefaultConfig() {
        final OutputTargetBuildItem outputTargetBuildItem = new OutputTargetBuildItem(Paths.get("/"), "", false, null, Optional.empty());
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        ces.add(new CloudEventMeta("myConsumedEvent", "/local/test", EventKind.CONSUMED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(outputTargetBuildItem, Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertTrue(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertTrue(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedOnlyProduced() {
        final OutputTargetBuildItem outputTargetBuildItem = new OutputTargetBuildItem(Paths.get("/"), "", false, null, Optional.empty());
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(outputTargetBuildItem, Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertTrue(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertFalse(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedWithKogitoSource() {
        final OutputTargetBuildItem outputTargetBuildItem = new OutputTargetBuildItem(Paths.get("/"), "", false, null, Optional.empty());
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myProducedEvent", "/local/test", EventKind.PRODUCED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        eventingProcessor.config.generateKogitoSource = true;
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(outputTargetBuildItem, Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertFalse(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertTrue(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertFalse(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkKogitoFileIsGeneratedOnlyConsumed() {
        final OutputTargetBuildItem outputTargetBuildItem = new OutputTargetBuildItem(Paths.get("/"), "", false, null, Optional.empty());
        final Set<CloudEventMeta> ces = new HashSet<>();
        ces.add(new CloudEventMeta("myConsumedEvent", "/local/test", EventKind.CONSUMED));
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(outputTargetBuildItem, Optional.of(resourcesMetadataBuildItem), producer);

        assertNotNull(producer.getItem().getData());
        assertTrue(producer.getItem().getData().length > 0);
        assertFalse(new String(producer.getItem().getData()).contains("SinkBinding"));
        assertFalse(new String(producer.getItem().getData()).contains("KogitoSource"));
        assertTrue(new String(producer.getItem().getData()).contains("Trigger"));
        assertTrue(new String(producer.getItem().getData()).contains("Broker"));
    }

    @Test
    void checkNotProducedIfNoCEs() {
        final OutputTargetBuildItem outputTargetBuildItem = new OutputTargetBuildItem(Paths.get("/"), "", false, null, Optional.empty());
        final Set<CloudEventMeta> ces = new HashSet<>();
        final KogitoServiceDeploymentTarget deploymentTarget = new KogitoServiceDeploymentTarget("apps", "v1", "Deployment", "kogito-service");
        final KogitoKnativeResourcesMetadataBuildItem resourcesMetadataBuildItem =
                new KogitoKnativeResourcesMetadataBuildItem(ces, deploymentTarget);
        final KogitoAddOnKnativeEventingProcessor eventingProcessor = buildTestProcessorWithDefaultConfig();
        final MockGeneratedFSProducer producer = new MockGeneratedFSProducer();

        eventingProcessor.generate(outputTargetBuildItem, Optional.of(resourcesMetadataBuildItem), producer);

        assertNull(producer.getItem());
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

    private static final class MockGeneratedFSProducer implements BuildProducer<GeneratedFileSystemResourceBuildItem> {

        private GeneratedFileSystemResourceBuildItem item;

        @Override
        public void produce(GeneratedFileSystemResourceBuildItem item) {
            this.item = item;
        }

        public GeneratedFileSystemResourceBuildItem getItem() {
            return item;
        }
    }
}
