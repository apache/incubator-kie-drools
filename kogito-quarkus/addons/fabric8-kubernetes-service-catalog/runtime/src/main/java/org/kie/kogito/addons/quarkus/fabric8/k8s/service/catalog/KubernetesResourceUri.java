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
package org.kie.kogito.addons.quarkus.fabric8.k8s.service.catalog;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class KubernetesResourceUri {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesResourceUri.class.getName());

    private final GVK gvk;

    private final String namespace;

    private final String resourceName;

    private final String customPortName;

    private final Map<String, String> customLabel;

    private KubernetesResourceUri(Builder builder) {
        if (builder.resourceName == null || builder.resourceName.isBlank()) {
            throw new IllegalArgumentException("resource name can't be empty");
        }

        this.gvk = builder.gvk;
        this.namespace = builder.namespace;
        this.resourceName = builder.resourceName;
        this.customPortName = builder.customPortName;
        this.customLabel = builder.customLabel != null ? Collections.unmodifiableMap(builder.customLabel) : Map.of();
    }

    GVK getGvk() {
        return gvk;
    }

    String getCustomPortName() {
        return customPortName;
    }

    Map<String, String> getCustomLabel() {
        return customLabel;
    }

    String getResourceName() {
        return resourceName;
    }

    String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return "KubernetesResourceUri{" +
                "gvk=" + gvk +
                ", namespace='" + namespace + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", customPortName='" + customPortName + '\'' +
                ", customLabel=" + customLabel +
                "} " + super.toString();
    }

    static KubernetesResourceUri parse(String rawUri) {
        KubernetesResourceUri kubernetesResourceUri = Builder.parse(rawUri).build();
        logger.debug("KubernetesResourceUri successfully parsed: {}", kubernetesResourceUri);
        return kubernetesResourceUri;
    }

    Builder copyBuilder() {
        return new Builder()
                .withCustomLabel(customLabel)
                .withGvk(gvk)
                .withNamespace(namespace)
                .withCustomPortName(customPortName)
                .withResourceName(resourceName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KubernetesResourceUri that = (KubernetesResourceUri) o;
        return gvk == that.gvk
                && Objects.equals(namespace, that.namespace)
                && Objects.equals(resourceName, that.resourceName)
                && Objects.equals(customPortName, that.customPortName)
                && Objects.equals(customLabel, that.customLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gvk, namespace, resourceName, customPortName, customLabel);
    }

    static class Builder {

        private GVK gvk;

        private String namespace;

        private String resourceName;

        private String customPortName;

        private Map<String, String> customLabel;

        private Builder() {
        }

        static Builder parse(String rawUri) {
            Builder builder = new Builder();

            String[] values = rawUri.split("/");

            switch (values.length) {
                case 2:
                    builder.withGvk(GVK.from(values[0]));
                    builder.withResourceName(values[1]);
                    break;
                case 3:
                    builder.withGvk(GVK.from(values[0]));
                    builder.withNamespace(values[1]);
                    builder.withResourceName(values[2]);
                    break;
                default:
                    logger.error("rawUri {} is not valid", rawUri);
            }

            if (rawUri.contains("?")) {
                setAttributes(rawUri, builder);
            }

            return builder;
        }

        static void setAttributes(String attrs, Builder builder) {
            // if there is more than one & at the query parameters, ignore the rest of them
            for (String str : attrs.split("\\?")[1].split("&")) {
                int indexOf = str.indexOf("=");
                String param = str.substring(0, indexOf);
                String value = str.substring(indexOf + 1);
                switch (param) {
                    case KubeConstants.CUSTOM_PORT_NAME_PROPERTY:
                        builder.withCustomPortName(value);
                        break;

                    case KubeConstants.CUSTOM_RESOURCE_LABEL_PROPERTY:
                        builder.withCustomLabel(Arrays.stream(value.split(";"))
                                .map(str1 -> str1.split("="))
                                .collect(Collectors.toMap(lblName -> lblName[0], lblValue -> lblValue[1])));
                        break;
                    default:
                        logger.warn("The given parameters {} are not supported", param);
                }
            }
        }

        Builder withGvk(GVK gvk) {
            this.gvk = gvk;
            return this;
        }

        Builder withNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        Builder withResourceName(String resourceName) {
            this.resourceName = resourceName;
            return this;
        }

        Builder withCustomPortName(String customPortName) {
            this.customPortName = customPortName;
            return this;
        }

        Builder withCustomLabel(Map<String, String> customLabel) {
            this.customLabel = customLabel;
            return this;
        }

        KubernetesResourceUri build() {
            return new KubernetesResourceUri(this);
        }
    }
}
