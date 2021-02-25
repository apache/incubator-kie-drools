/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.taskassigning.index.service.client.graphql;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractArgumentContainerTest<T extends ArgumentContainer> {

    protected T argumentContainer;
    protected Map<String, Argument> configuredArguments;

    @BeforeEach
    void setUp() {
        argumentContainer = createArgumentContainer();
        configuredArguments = new HashMap<>();
        setUpArguments(argumentContainer);
    }

    protected abstract T createArgumentContainer();

    protected abstract void setUpArguments(T argumentContainer);

    protected abstract String expectedType();

    protected void addArgument(String name, Argument value) {
        argumentContainer.add(name, value);
        configuredArguments.put(name, value);
    }

    @Test
    void asJson() {
        JsonNode result = argumentContainer.asJson();
        assertThat(result.size()).isEqualTo(configuredArguments.size());
        configuredArguments.forEach((key, value) -> assertThat(result.get(key))
                .isNotNull()
                .isEqualTo(value.asJson()));
    }

    @Test
    void isEmpty() {
        assertThat(argumentContainer.isEmpty()).isFalse();
    }

    @Test
    void getArguments() {
        assertThat(argumentContainer.getArguments()).hasSize(configuredArguments.size());
        argumentContainer.getArguments().forEach(argumentEntry -> assertThat(configuredArguments.get(argumentEntry.getName()))
                .isNotNull()
                .isEqualTo(argumentEntry.getValue()));
    }

    @Test
    void getTypeId() {
        assertThat(argumentContainer.getTypeId()).isEqualTo(expectedType());
    }
}
