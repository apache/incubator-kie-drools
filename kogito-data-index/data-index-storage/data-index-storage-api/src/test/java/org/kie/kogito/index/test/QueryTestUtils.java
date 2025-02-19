/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.index.test;

import java.util.List;
import java.util.function.BiConsumer;

import org.assertj.core.groups.Tuple;
import org.kie.kogito.index.model.ProcessDefinitionKey;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTestUtils {

    public static <V> BiConsumer<List<V>, String[]> assertWithIdInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id").containsExactly(ids);
    }

    public static <V> BiConsumer<List<V>, String[]> assertWithId() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id").containsExactlyInAnyOrder(ids);
    }

    public static BiConsumer<List<String>, String[]> assertWithStringInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).containsExactly(ids);
    }

    public static BiConsumer<List<String>, String[]> assertWithString() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).containsExactlyInAnyOrder(ids);
    }

    public static BiConsumer<List<ObjectNode>, String[]> assertWithObjectNodeInOrder() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting(n -> n.get("id").asText()).containsExactly(ids);
    }

    public static BiConsumer<List<ObjectNode>, String[]> assertWithObjectNode() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting(n -> n.get("id").asText()).containsExactlyInAnyOrder(ids);
    }

    public static <V> BiConsumer<List<V>, String[]> assertNotId() {
        return (instances, ids) -> assertThat(instances).extracting("id").doesNotContainAnyElementsOf(List.of(ids));
    }

    public static <V> BiConsumer<List<V>, ProcessDefinitionKey[]> assertWithKey() {
        return (instances, ids) -> assertThat(instances).hasSize(ids == null ? 0 : ids.length).extracting("id", "version").map(Tuple::toArray)
                .map(objs -> new ProcessDefinitionKey((String) objs[0], (String) objs[1])).containsExactly(ids);
    }

    public static <V> BiConsumer<List<V>, ProcessDefinitionKey[]> assertNoKey() {
        return (instances, ids) -> assertThat(instances).isEmpty();
    }
}
