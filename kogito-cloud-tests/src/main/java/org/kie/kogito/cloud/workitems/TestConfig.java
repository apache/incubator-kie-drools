/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.cloud.workitems;

import java.util.Optional;

import cz.xtf.core.config.XTFConfig;

public class TestConfig {

    private TestConfig() {}

    private static final String IMAGE_KAAS_QUARKUS_BUILDER_S2I = "image.kaas.quarkus.builder.s2i";
    private static final String IMAGE_KAAS_QUARKUS_RUNTIME = "image.kaas.quarkus.runtime";
    private static final String IMAGE_KAAS_SPRINGBOOT_BUILDER_S2I = "image.kaas.springboot.builder.s2i";
    private static final String IMAGE_KAAS_SPRINGBOOT_RUNTIME = "image.kaas.springboot.runtime";

    private static final String MAVEN_MIRROR_URL = "maven.mirror.url";

    public static String getKaasS2iQuarkusBuilderImage() {
        return getMandatoryProperty(IMAGE_KAAS_QUARKUS_BUILDER_S2I);
    }

    public static String getKaasQuarkusRuntimeImage() {
        return getMandatoryProperty(IMAGE_KAAS_QUARKUS_RUNTIME);
    }

    public static String getKaasS2iSpringBootBuilderImage() {
        return getMandatoryProperty(IMAGE_KAAS_SPRINGBOOT_BUILDER_S2I);
    }

    public static String getKaasSpringBootRuntimeImage() {
        return getMandatoryProperty(IMAGE_KAAS_SPRINGBOOT_RUNTIME);
    }

    public static Optional<String> getMavenMirrorUrl() {
        return getOptionalProperty(MAVEN_MIRROR_URL);
    }

    private static String getMandatoryProperty(String propertyName) {
        String propertyValue = XTFConfig.get(propertyName);
        if (propertyValue == null || propertyValue.isEmpty()) {
            throw new RuntimeException("Required property with name " + propertyName + " is not set.");
        }
        return propertyValue;
    }

    private static Optional<String> getOptionalProperty(String propertyName) {
        String propertyValue = XTFConfig.get(propertyName);
        if (propertyValue == null || propertyValue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(propertyValue);
    }
}
