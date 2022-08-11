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
package org.kie.kogito.jackson.utils;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MergeUtilsTest {

    @Test
    void testSrcTargetArray() {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6),
                JsonObjectUtils
                        .toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(4).add(5).add(6), ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), true)));
    }

    @Test
    void testTargetArraySrcInt() {
        assertEquals(Arrays.asList(1, 2, 3, 4), JsonObjectUtils.toJavaValue(MergeUtils.merge(new IntNode(4), ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3))));
    }

    @Test
    void testTargetIntSrcArray() {
        assertEquals(Arrays.asList(4, 1, 2, 3), JsonObjectUtils.toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), new IntNode(4), false)));
    }

    @Test
    void testTargetObjectSrcInt() {
        assertEquals(Collections.singletonMap("response", 3), JsonObjectUtils.toJavaValue(MergeUtils.merge(new IntNode(3), ObjectMapperFactory.get().createObjectNode(), false)));
    }

    @Test
    void testTargetObjectSrcArray() {
        assertEquals(Collections.singletonMap("response", Arrays.asList(1, 2, 3)),
                JsonObjectUtils.toJavaValue(MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(1).add(2).add(3), ObjectMapperFactory.get().createObjectNode(), false)));
    }

    @Test
    void testObjectMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        JsonNode merged =
                MergeUtils.merge(ObjectMapperFactory.get().createObjectNode().set("dummyCustomer", dummyCustomer), ObjectMapperFactory.get().createObjectNode().set("vipCustomer", vipCustomer));
        assertEquals(dummyCustomer, merged.get("dummyCustomer"));
        assertEquals(vipCustomer, merged.get("vipCustomer"));
    }

    @Test
    void testOverlapObjectMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Parla", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        JsonNode expectedMerged =
                getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N", "percebe 13", "casa de mis padres en Mostoles"));
        assertEquals(expectedMerged, MergeUtils.merge(dummyCustomer, vipCustomer, true));
    }

    @Test
    void testObjectArrayMerge() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas Virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        ArrayNode merged = (ArrayNode) MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(dummyCustomer), ObjectMapperFactory.get().createArrayNode().add(vipCustomer), true);
        assertEquals(2, merged.size());
        assertEquals(vipCustomer, merged.get(0));
        assertEquals(dummyCustomer, merged.get(1));
    }

    @Test
    void testObjectArrayMergeNoDuplicate() {
        ObjectNode dummyCustomer = getCustomer("Fulanito", 23, 999.9, false, "Parla", Arrays.asList("percebe 13", "casa de mis padres en Mostoles"));
        ObjectNode vipCustomer = getCustomer("Messi", 69, 1221312.2, true, "Islas Virgenes", Arrays.asList("Isla paradisiaca anonima", "Palacio presidencial S/N"));
        ArrayNode merged =
                (ArrayNode) MergeUtils.merge(ObjectMapperFactory.get().createArrayNode().add(dummyCustomer), ObjectMapperFactory.get().createArrayNode().add(vipCustomer).add(dummyCustomer), true);
        assertEquals(2, merged.size());
        assertEquals(vipCustomer, merged.get(0));
        assertEquals(dummyCustomer, merged.get(1));
    }

    @Test
    void testNullMerge() {
        JsonNode srcNode = ObjectMapperFactory.get().createObjectNode().put("name", "javierito");
        assertEquals(srcNode, MergeUtils.merge(srcNode, null));
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
        assertEquals(ObjectMapperFactory.get().createObjectNode().put("name", "javierito"), MergeUtils.merge(JsonObjectUtils.fromValue(person), target));

    }

    private static ObjectNode getCustomer(String name, int age, double salary, boolean hasJob, String city, Iterable<String> addresses) {
        ArrayNode addressesArray = ObjectMapperFactory.get().createArrayNode();
        for (String address : addresses) {
            addressesArray.add(ObjectMapperFactory.get().createObjectNode().put("address", address).put("city", city));
        }
        return ObjectMapperFactory.get().createObjectNode().put("name", name).put("age", age).put("salary", salary).put("hasJob", hasJob).set("addresses", addressesArray);
    }

}
