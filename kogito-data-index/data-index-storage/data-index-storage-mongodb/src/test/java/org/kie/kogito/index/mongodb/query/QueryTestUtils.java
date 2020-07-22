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

package org.kie.kogito.index.mongodb.query;

import java.util.List;
import java.util.function.BiConsumer;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

class QueryTestUtils {

    static <V> BiConsumer<List<V>, String[]> assertWithIdInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id").containsExactly(ids);
    }

    static <V> BiConsumer<List<V>, String[]> assertWithId() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id").containsExactlyInAnyOrder(ids);
    }

    static BiConsumer<List<String>, String[]> assertWithStringInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).containsExactly(ids);
    }

    static BiConsumer<List<String>, String[]> assertWithString() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).containsExactlyInAnyOrder(ids);
    }

    static BiConsumer<List<ObjectNode>, String[]> assertWithObjectNodeInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting(n -> n.get("id").asText()).containsExactly(ids);
    }

    static BiConsumer<List<ObjectNode>, String[]> assertWithObjectNode() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting(n -> n.get("id").asText()).containsExactlyInAnyOrder(ids);
    }
}
