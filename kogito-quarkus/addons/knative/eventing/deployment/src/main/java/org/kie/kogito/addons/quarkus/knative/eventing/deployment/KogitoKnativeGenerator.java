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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import io.fabric8.kubernetes.api.model.HasMetadata;

public class KogitoKnativeGenerator {

    private static final String YAML_SEPARATOR = "---\n";
    private static final ObjectMapper YAML_MAPPER;

    private static final Logger LOGGER = LoggerFactory.getLogger(KogitoKnativeGenerator.class);

    static {
        YAML_MAPPER = new ObjectMapper((new YAMLFactory()).enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        YAML_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
        YAML_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        YAML_MAPPER.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, false);
    }

    private final List<HasMetadata> resources;

    public KogitoKnativeGenerator() {
        this.resources = new ArrayList<>();
    }

    public KogitoKnativeGenerator addResources(final List<? extends HasMetadata> resources) {
        this.resources.addAll(resources);
        return this;
    }

    public KogitoKnativeGenerator addOptionalResource(final Optional<? extends HasMetadata> resource) {
        resource.ifPresent(this.resources::add);
        return this;
    }

    /**
     * Transforms all resources in an array of bytes in YAML format separated by `---`.
     */
    public byte[] getResourcesBytes() {
        return this.resources.stream()
                .flatMap(r -> {
                    try {
                        LOGGER.info("About to generate Kogito Knative resource {} named {}", r.getKind(), r.getMetadata().getName());
                        return Stream.of(YAML_MAPPER.writeValueAsString(r));
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Impossible to generate resource {} named {}", r.getKind(), r.getMetadata().getName(), e);
                    }
                    return Stream.empty();
                })
                .map(YAML_SEPARATOR::concat)
                .reduce("", String::concat).getBytes(StandardCharsets.UTF_8);
    }
}
