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

import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;

public final class KnativeResourcesUtil {

    private static final String SINK_BINDING_SUFFIX = "sb";
    private static final String TRIGGER_SUFFIX = "trigger";

    private KnativeResourcesUtil() {
    }

    public static String generateTriggerName(final String ceType, final String appName) {
        final String triggerName = String.format("%s-%s-%s", ceType, TRIGGER_SUFFIX, appName);
        return KubernetesResourceUtil.sanitizeName(triggerName);
    }

    public static String generateSinkBindingName(final String appName) {
        final String sinkBindingName = String.format("%s-%s", SINK_BINDING_SUFFIX, appName);
        return KubernetesResourceUtil.sanitizeName(sinkBindingName);
    }
}
