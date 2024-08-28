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
package org.kie.kogito.jackson.utils;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.assertj.core.api.Assertions.assertThat;

public class MergeUtilsTest {

    @Test
    void testSrcTargetArray() {
        assertThat(JsonObjectUtils
                .toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(4).add(5).add(6), ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), true)))
                        .isEqualTo(Arrays.asList(1, 2, 3, 4, 5, 6));
    }

    @Test
    void testTargetArraySrcInt() {
        assertThat(JsonObjectUtils.toJavaValue(MergeUtils.merge(new IntNode(4), ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3)))).isEqualTo(Arrays.asList(1, 2, 3, 4));
    }

    @Test
    void testTargetIntSrcArray() {
        assertThat(JsonObjectUtils.toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), new IntNode(4), false))).isEqualTo(Arrays.asList(4, 1, 2, 3));
    }

    @Test
    void testTargetObjectSrcInt() {
        assertThat(JsonObjectUtils.toJavaValue(MergeUtils.merge(new IntNode(3), ObjectMapperFactory.get().createObjectNode(), false))).isEqualTo(3);
    }

    @Test
    void testTargetObjectSrcArray() {
        assertThat(JsonObjectUtils.toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), ObjectMapperFactory.get().createObjectNode(), false)))
                .isEqualTo(Arrays.asList(1, 2, 3));
    }

    @Test
    void testTargetStringSrcObject() {
        assertThat(JsonObjectUtils.toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createObjectNode(), new TextNode("pepe")))).isEqualTo(Collections.singletonMap("_target", "pepe"));
    }

    @Test
    void testObjectMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        JsonNode merged =
                MergeUtils.merge(ObjectMapperFactory.get().createObjectNode().set("dummyCustomer", dummyCustomer), ObjectMapperFactory.get().createObjectNode().set("vipCustomer", vipCustomer));
        assertThat(merged.get("dummyCustomer")).isEqualTo(dummyCustomer);
        assertThat(merged.get("vipCustomer")).isEqualTo(vipCustomer);
    }

    @Test
    void testOverlapObjectMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Parla", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        JsonNode expectedMerged =
                getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N", "percebe 13", "casa de mis padres en Mostoles"));
        assertThat(MergeUtils.merge(dummyCustomer, vipCustomer, true)).isEqualTo(expectedMerged);
    }

    @Test
    void testObjectArrayMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas Virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        ArrayNode merged = (ArrayNode) MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(dummyCustomer), ObjectMapperFactory.get().createArrayNode().add(vipCustomer), true);
        assertThat(merged.size()).isEqualTo(2);
        assertThat(merged.get(0)).isEqualTo(vipCustomer);
        assertThat(merged.get(1)).isEqualTo(dummyCustomer);
    }

    @Test
    void testObjectArrayMergeNoDuplicate() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas Virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        ArrayNode merged =
                (ArrayNode) MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(dummyCustomer), ObjectMapperFactory.get().createArrayNode().add(vipCustomer).add(dummyCustomer), true);
        assertThat(merged.size()).isEqualTo(2);
        assertThat(merged.get(0)).isEqualTo(vipCustomer);
        assertThat(merged.get(1)).isEqualTo(dummyCustomer);
    }

    @Test
    void testNullMerge() {
        JsonNode srcNode = ObjectMapperFactory.get().createObjectNode().put("name", "javierito");
        assertThat(MergeUtils.merge(srcNode, null)).isEqualTo(srcNode);
    }

    private static class Person {
        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Test
    void testMergeWithPojo() {
        Person person = new Person("javierito");
        ObjectNode target = ObjectMapperFactory.get().createObjectNode().put("name", "fulanito");
        assertThat(MergeUtils.merge(JsonObjectUtils.fromValue(person), target)).isEqualTo(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"));

    }

    private static ObjectNode getCustomer(String name, int age, double salary, boolean hasJob, String city, Iterable<String> addresses) {
        ArrayNode addressesArray = ObjectMapperFactory.get().createArrayNode();
        for (String address : addresses) {
            addressesArray.add(ObjectMapperFactory.get().createObjectNode().put("address", address).put("city", city));
        }
        return ObjectMapperFactory.get().createObjectNode().put("name", name).put("age", age).put("salary", salary).put("hasJob", hasJob).set("addresses", addressesArray);
    }

}
