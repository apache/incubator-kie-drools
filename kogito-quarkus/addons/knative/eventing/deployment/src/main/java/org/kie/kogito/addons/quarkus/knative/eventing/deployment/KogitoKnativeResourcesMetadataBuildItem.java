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

import java.util.Collections;
import java.util.Set;

import org.kie.kogito.event.cloudevents.CloudEventMeta;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build Item to hold information about the project to generate Knative resources
 */
public final class KogitoKnativeResourcesMetadataBuildItem extends SimpleBuildItem {

    private final Set<? extends CloudEventMeta> cloudEvents;
    private final KogitoServiceDeploymentTarget deployment;

    public KogitoKnativeResourcesMetadataBuildItem(final Set<? extends CloudEventMeta> cloudEvents, final KogitoServiceDeploymentTarget deployment) {
        this.cloudEvents = cloudEvents;
        this.deployment = deployment;
    }

    public Set<CloudEventMeta> getCloudEvents() {
        return Collections.unmodifiableSet(this.cloudEvents);
    }

    public KogitoServiceDeploymentTarget getDeployment() {
        return deployment;
    }
}
