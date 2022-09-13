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
package org.kie.kogito.addons.quarkus.k8s.parser;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.kie.kogito.addons.quarkus.k8s.KubeConstants;

public class GVK {

    private final Optional<String> group;
    private final String version;
    private final String kind;
    private final String apiVersion;

    private Set<String> supportedGKVs = Set.of(
            KubeConstants.KIND_DEPLOYMENT,
            KubeConstants.KIND_DEPLOYMENT_CONFIG,
            KubeConstants.KIND_STATEFUL_SET,
            KubeConstants.KIND_SERVICE,
            KubeConstants.KIND_ROUTE,
            KubeConstants.KIND_INGRESS,
            KubeConstants.KIND_POD,
            KubeConstants.KIND_KNATIVE_SERVICE);

    public GVK(String group, String version, String kind) {
        this.group = Optional.of(group.toLowerCase(Locale.ROOT));
        this.version = version.toLowerCase(Locale.ROOT);
        this.kind = kind.toLowerCase(Locale.ROOT);
        this.apiVersion = setApiVersion();
        validateGivenGVK();
    }

    public GVK(String version, String kind) {
        this.version = version.toLowerCase(Locale.ROOT);
        this.kind = kind.toLowerCase(Locale.ROOT);
        this.group = Optional.empty();
        this.apiVersion = setApiVersion();
        validateGivenGVK();
    }

    private String setApiVersion() {
        return this.group.isPresent() ? this.group.get() + "/" + version : version;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getVersion() {
        return version;
    }

    public String getGVK() {
        return getApiVersion() + "/" + kind;
    }

    public String getKind() {
        return kind;
    }

    private void validateGivenGVK() {
        // while https://issues.redhat.com/browse/KOGITO-7373 is not implemented, does not allow empty or invalid gvk
        if (!supportedGKVs.contains(this.getGVK())) {
            throw new IllegalArgumentException("Given GVK is not valid or supported: " + this.getGVK());
        }
    }

    @Override
    public String toString() {
        return "GVK{" +
                "group='" + group.orElse("") + '\'' +
                ", version='" + version + '\'' +
                ", kind='" + kind + '\'' +
                '}';
    }
}
