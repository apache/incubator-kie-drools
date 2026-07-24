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
package org.kie.kogito.index.json;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilsTest {

    @Test
    void testMergeWithDot() {
        ObjectNode expected = ObjectMapperFactory.get().createObjectNode().set("key", ObjectMapperFactory.get().createObjectNode().put("user1", "manolo"));
        assertThat(JsonUtils.mergeVariable("key.user1", "manolo", null)).isEqualTo(expected);
    }

    @Test
    void testSimpleMerge() {
        ObjectNode expected = ObjectMapperFactory.get().createObjectNode().put("user1", "manolo");
        assertThat(JsonUtils.mergeVariable("user1", "manolo", null)).isEqualTo(expected);
    }

    @Test
    void testComplexMergeWithDot() {
        ObjectNode expected = ObjectMapperFactory.get().createObjectNode().set("key", ObjectMapperFactory.get().createObjectNode().put("user1", "manolo").put("user2", "pepe"));
        assertThat(JsonUtils.mergeVariable("key.user1", "manolo", ObjectMapperFactory.get().createObjectNode().set("key", ObjectMapperFactory.get().createObjectNode().put("user2", "pepe"))))
                .isEqualTo(expected);
    }
}
