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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.ruleflow.core.Metadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Node;
import org.kie.kogito.codegen.process.events.ProcessCloudEventMeta;
import org.kie.kogito.codegen.process.events.ProcessCloudEventMetaBuilder;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.kie.kogito.quarkus.extensions.spi.deployment.KogitoProcessContainerGeneratorBuildItem;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.kubernetes.spi.DeployStrategy;
import io.quarkus.kubernetes.spi.KubernetesDeploymentTargetBuildItem;
import io.quarkus.kubernetes.spi.KubernetesResourceMetadataBuildItem;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KogitoProcessKnativeEventingProcessorTest {

    private static TriggerMetaData triggerMetadata;
    private static final CloudEventMeta EXTENDED_CLOUD_EVENT1 = new CloudEventMeta("extendedEvent1", "source1", EventKind.PRODUCED);
    private static final CloudEventMeta EXTENDED_CLOUD_EVENT2 = new CloudEventMeta("extendedEvent2", "source2", EventKind.CONSUMED);

    @BeforeAll
    static void setupClass() {
        Node node = mock(Node.class);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(Metadata.TRIGGER_REF, "myType");
        metadata.put(Metadata.MAPPING_VARIABLE, "myVar");
        metadata.put(Metadata.TRIGGER_TYPE, "ProduceMessage");
        metadata.put(Metadata.MESSAGE_TYPE, "myDataType");
        when(node.getMetaData()).thenReturn(metadata);
        triggerMetadata = TriggerMetaData.of(node);
    }

    @Test
    void checkNotBuiltMetadataIfNoCEs() {
        final KogitoProcessContainerGeneratorBuildItem containerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(Collections.emptySet());
        final KogitoProcessKnativeEventingProcessor processor = new KogitoProcessKnativeEventingProcessor();
        final MockKogitoKnativeMetadataProducer metadata = new MockKogitoKnativeMetadataProducer();

        processor.buildMetadata(singletonList(containerGeneratorBuildItem), Collections.emptyList(), null, null, metadata);
        // not produced
        assertNull(metadata.getItem());
    }

    @Test
    void checkBuiltMetadataWithCEsNullSelectedItem() {
        final KogitoProcessContainerGeneratorBuildItem containerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(Collections.emptySet());
        final ProcessCloudEventMetaBuilder mockedCEBuilder = mock(ProcessCloudEventMetaBuilder.class);
        final KogitoProcessKnativeEventingProcessor processor = mock(KogitoProcessKnativeEventingProcessor.class);
        final MockKogitoKnativeMetadataProducer metadata = new MockKogitoKnativeMetadataProducer();
        final KubernetesResourceMetadataBuildItem kubernetesResourceMetadataBuildItem = new KubernetesResourceMetadataBuildItem("kubernetes", "apps", "v1", "Deployment", "name");
        final List<KubernetesResourceMetadataBuildItem> kubernetesMetaBuildItems = singletonList(kubernetesResourceMetadataBuildItem);
        final List<KogitoCloudEventsBuildItem> extendedCloudEventsBuildItems = singletonList(new KogitoCloudEventsBuildItem(Set.of(EXTENDED_CLOUD_EVENT1, EXTENDED_CLOUD_EVENT2)));

        doCallRealMethod().when(processor).buildMetadata(singletonList(containerGeneratorBuildItem), extendedCloudEventsBuildItems, null, kubernetesMetaBuildItems, metadata);
        when(processor.selectDeploymentTarget(null, kubernetesMetaBuildItems)).thenCallRealMethod();
        when(processor.getCloudEventMetaBuilder()).thenReturn(mockedCEBuilder);
        when(mockedCEBuilder.build(containerGeneratorBuildItem.getProcessContainerGenerators()))
                .thenReturn(Collections.singleton(new ProcessCloudEventMeta("123", triggerMetadata)));

        processor.buildMetadata(singletonList(containerGeneratorBuildItem), extendedCloudEventsBuildItems, null, kubernetesMetaBuildItems, metadata);

        assertNotNull(metadata.getItem());
        Set<CloudEventMeta> cloudEvents = metadata.getItem().getCloudEvents();
        assertTrue(cloudEvents.contains(EXTENDED_CLOUD_EVENT1));
        assertTrue(cloudEvents.contains(EXTENDED_CLOUD_EVENT2));
    }

    @Test
    void checkBuiltMetadataWithCEsNotNullSelectedItem() {
        final KogitoProcessContainerGeneratorBuildItem containerGeneratorBuildItem = new KogitoProcessContainerGeneratorBuildItem(Collections.emptySet());
        final ProcessCloudEventMetaBuilder mockedCEBuilder = mock(ProcessCloudEventMetaBuilder.class);
        final KogitoProcessKnativeEventingProcessor processor = mock(KogitoProcessKnativeEventingProcessor.class);
        final MockKogitoKnativeMetadataProducer metadata = new MockKogitoKnativeMetadataProducer();
        final KubernetesResourceMetadataBuildItem kubernetesResourceMetadataBuildItem = new KubernetesResourceMetadataBuildItem("kubernetes", "apps", "v1", "Deployment", "name");
        final List<KubernetesResourceMetadataBuildItem> kubernetesMetaBuildItems = singletonList(kubernetesResourceMetadataBuildItem);
        final List<KubernetesDeploymentTargetBuildItem> deploymentTargets =
                singletonList(new KubernetesDeploymentTargetBuildItem("kubernetes", "Deployment", "apps", "v1", DeployStrategy.CreateOrUpdate));
        final List<KogitoCloudEventsBuildItem> extendedCloudEventsBuildItems = singletonList(new KogitoCloudEventsBuildItem(Set.of(EXTENDED_CLOUD_EVENT1, EXTENDED_CLOUD_EVENT2)));

        doCallRealMethod().when(processor).buildMetadata(singletonList(containerGeneratorBuildItem), extendedCloudEventsBuildItems, deploymentTargets, kubernetesMetaBuildItems, metadata);
        when(processor.selectDeploymentTarget(deploymentTargets, kubernetesMetaBuildItems)).thenCallRealMethod();
        when(processor.getCloudEventMetaBuilder()).thenReturn(mockedCEBuilder);
        when(mockedCEBuilder.build(containerGeneratorBuildItem.getProcessContainerGenerators()))
                .thenReturn(Collections.singleton(new ProcessCloudEventMeta("123", triggerMetadata)));

        processor.buildMetadata(singletonList(containerGeneratorBuildItem), extendedCloudEventsBuildItems, deploymentTargets, kubernetesMetaBuildItems, metadata);

        assertNotNull(metadata.getItem());
        Set<CloudEventMeta> cloudEvents = metadata.getItem().getCloudEvents();
        assertTrue(cloudEvents.contains(EXTENDED_CLOUD_EVENT1));
        assertTrue(cloudEvents.contains(EXTENDED_CLOUD_EVENT2));
    }

    private static final class MockKogitoKnativeMetadataProducer implements BuildProducer<KogitoKnativeResourcesMetadataBuildItem> {

        private KogitoKnativeResourcesMetadataBuildItem item;

        @Override
        public void produce(KogitoKnativeResourcesMetadataBuildItem item) {
            this.item = item;
        }

        public KogitoKnativeResourcesMetadataBuildItem getItem() {
            return item;
        }
    }
}
