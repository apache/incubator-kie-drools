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
package org.kie.kogito.addons.quarkus.k8s.discovery;

import java.util.Locale;

public enum GVK {

    DEPLOYMENT("apps/v1/deployment"),
    DEPLOYMENT_CONFIG("apps.openshift.io/v1/deploymentconfig"),
    STATEFUL_SET("apps/v1/statefulset"),
    SERVICE("v1/service"),
    ROUTE("route.openshift.io/v1/route"),
    INGRESS("networking.k8s.io/v1/ingress"),
    POD("v1/pod"),
    KNATIVE_SERVICE("serving.knative.dev/v1/service");

    private final String value;

    GVK(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static GVK from(String version, String kind) {
        return from(version + "/" + kind);
    }

    public static GVK from(String group, String version, String kind) {
        return from(group + "/" + version + "/" + kind);
    }

    private static String sanitize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private static GVK from(String value) {
        String sanitizedValue = sanitize(value);

        for (GVK gvk : GVK.values()) {
            if (gvk.value.equals(sanitizedValue)) {
                return gvk;
            }
        }

        throw new IllegalArgumentException("Given GVK is not valid or supported: " + value);
    }
}
