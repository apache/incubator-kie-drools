/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.util.Locale;
import java.util.Optional;

enum GVK {

    DEPLOYMENT("deployments.v1.apps"),
    DEPLOYMENT_CONFIG("deploymentconfigs.v1.apps.openshift.io"),
    STATEFUL_SET("statefulsets.v1.apps"),
    SERVICE("services.v1"),
    ROUTE("routes.v1.route.openshift.io"),
    INGRESS("ingresses.v1.networking.k8s.io"),
    POD("pods.v1"),
    KNATIVE_SERVICE("services.v1.serving.knative.dev");

    private final String value;

    GVK(String value) {
        this.value = value;
    }

    String getValue() {
        return this.value;
    }

    static GVK from(String version, String kind) {
        return from(kind + "." + version);
    }

    static GVK from(String group, String version, String kind) {
        return from(kind + "." + version + "." + group);
    }

    private static String sanitize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    static GVK from(String value) {
        return fromInternal(value).orElseThrow(() -> new IllegalArgumentException("Given GVK is not valid or supported: " + value));
    }

    private static Optional<GVK> fromInternal(String value) {
        String sanitizedValue = sanitize(value);

        for (GVK gvk : GVK.values()) {
            if (gvk.value.equals(sanitizedValue)) {
                return Optional.of(gvk);
            }
        }

        return Optional.empty();
    }

    static boolean isValid(String value) {
        return fromInternal(value).isPresent();
    }
}
