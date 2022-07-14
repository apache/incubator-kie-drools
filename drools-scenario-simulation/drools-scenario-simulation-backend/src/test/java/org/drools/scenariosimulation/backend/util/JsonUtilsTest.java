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

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilsTest {

    @Test
    public void convertFromStringToJSONNode() {
        assertThat(JsonUtils.convertFromStringToJSONNode(null).isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("Not json").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("\"Not json").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("key : notJson\"").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("[key : 100]").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("{\"key\" : 100{").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("{key : 100}").isPresent()).isFalse();
        assertThat(JsonUtils.convertFromStringToJSONNode("\"Json\"").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("\"key : Json\"").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("{ \"id\": 2, \"username\": \"user\", \"num\": 12, \"name\": \"Mr Yellow\"\n }").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("{ \"users\": [\n" +
                "\t\t{ \"id\": 3, \"username\": \"user45\", \"num\": 24, \"name\": \"Mr White\" },\n" +
                "\t\t{ \"id\": 4, \"username\": \"user65\", \"num\": 32, \"name\": \"Mr Red\" }\n" +
                "\t]}").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("[{\"name\": \"\\\"John\\\"\"}, " +
                "{\"name\": \"\\\"John\\\"\", \"names\" : [{\"value\": \"\\\"Anna\\\"\"}, {\"value\": \"\\\"Mario\\\"\"}]}]").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("[1,2,3]").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("{\"id\": 23, \"num\": 34, \"time\" : 56}").isPresent()).isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("{\"married\":true, \"num\":34, \"name\": \"john\"}").isPresent()).as("Combine three data types in object").isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("[{\"married\":true,\"num\":34,\"name\":\"john\"}," +
                "{\"married\":false,\"num\":43,\"name\":\"jane\"}]").isPresent()).as("Combine three data types in array").isTrue();
        assertThat(JsonUtils.convertFromStringToJSONNode("{\"is married\":\"yes, is\"}").isPresent()).as("Whitespaces").isTrue();
    }
}
