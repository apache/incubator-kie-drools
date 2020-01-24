/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.util;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class JsonUtilsTest {

    @Test
    public void convertFromStringToJSONNode() {
        assertFalse(JsonUtils.convertFromStringToJSONNode(null).isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("Not json").isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("\"Not json").isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("key : notJson\"").isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("[key : 100]").isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("{\"key\" : 100{").isPresent());
        assertFalse(JsonUtils.convertFromStringToJSONNode("{key : 100}").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("\"Json\"").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("\"key : Json\"").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("{ \"id\": 2, \"username\": \"user\", \"num\": 12, \"name\": \"Mr Yellow\"\n }").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("{ \"users\": [\n" +
                                                                 "\t\t{ \"id\": 3, \"username\": \"user45\", \"num\": 24, \"name\": \"Mr White\" },\n" +
                                                                 "\t\t{ \"id\": 4, \"username\": \"user65\", \"num\": 32, \"name\": \"Mr Red\" }\n" +
                                                                 "\t]}").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("[{\"name\": \"\\\"John\\\"\"}, " +
                                                                 "{\"name\": \"\\\"John\\\"\", \"names\" : [{\"value\": \"\\\"Anna\\\"\"}, {\"value\": \"\\\"Mario\\\"\"}]}]").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("[1,2,3]").isPresent());
        assertTrue(JsonUtils.convertFromStringToJSONNode("{\"id\": 23, \"num\": 34, \"time\" : 56}").isPresent());
        assertTrue("Combine three data types in object",
                   JsonUtils.convertFromStringToJSONNode("{\"married\":true, \"num\":34, \"name\": \"john\"}").isPresent());
        assertTrue("Combine three data types in array",
                   JsonUtils.convertFromStringToJSONNode("[{\"married\":true,\"num\":34,\"name\":\"john\"}," +
                                                                 "{\"married\":false,\"num\":43,\"name\":\"jane\"}]").isPresent());
        assertTrue("Whitespaces",
                   JsonUtils.convertFromStringToJSONNode("{\"is married\":\"yes, is\"}").isPresent());
    }
}
