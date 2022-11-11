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
package org.kie.kogito.jackson.utils;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonNodeVisitorTest {
    @Test
    void testSimpleObjectNodeVisitor() {
        ObjectNode source = ObjectMapperFactory.get().createObjectNode().put("name", "no javierito");
        assertThat(JsonNodeVisitor.transformTextNode(source, n -> JsonObjectUtils.fromValue("javierito"))).isEqualTo(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"));
    }

    @Test
    void testComplextObjectNodeVisitor() {
        ObjectNode source = ObjectMapperFactory.get().createObjectNode().set("embedded", ObjectMapperFactory.get().createObjectNode().put("name", "no javierito"));
        assertThat(JsonNodeVisitor.transformTextNode(source, n -> JsonObjectUtils.fromValue("javierito")))
                .isEqualTo(ObjectMapperFactory.get().createObjectNode().set("embedded", ObjectMapperFactory.get().createObjectNode().put("name", "javierito")));
    }

    @Test
    void testArrayNodeVisitor() {
        ObjectNode source =
                ObjectMapperFactory.get().createObjectNode().set("embedded", ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "no javierito")));
        assertThat(JsonNodeVisitor.transformTextNode(source, n -> JsonObjectUtils.fromValue("javierito"))).isEqualTo(
                ObjectMapperFactory.get().createObjectNode().set("embedded", ObjectMapperFactory.get().createArrayNode().add(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"))));
    }

    @Test
    void testStringNodeVisitor() {
        assertThat(JsonNodeVisitor.transformTextNode(JsonObjectUtils.fromValue("pepa"), p -> p).asText()).isEqualTo("pepa");
    }
}
