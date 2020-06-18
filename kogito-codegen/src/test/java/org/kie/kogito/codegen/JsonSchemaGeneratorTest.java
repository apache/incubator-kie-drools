/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen;


import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.kie.kogito.UserTask;
import org.kie.kogito.UserTaskParam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaGeneratorTest {

    @UserTask(taskName = "test", processName = "test")
    private static class PersonInputParams {

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private String name;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private Address address;
    }

    @UserTask(taskName = "test", processName = "test")
    private static class PersonOutputParams {

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private int age;

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private String name;

        @SuppressWarnings("unused")
        private String ignored;
    }

    @UserTask(taskName = "test", processName = "InputOutput")
    private static class PersonInputOutputParams {

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private int age;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private String name;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private Address address;
    }

    private static class Address {
        @SuppressWarnings("unused")
        private String street;
    }

    private static class IgnoredClass {

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private int age;
    }

    @Test
    public void testJsonSchemaGenerator() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.Builder(Stream.of(PersonInputParams.class, PersonOutputParams.class, IgnoredClass.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("test_test.json", file.relativePath());
        assertSchema(file);
    }

    @Test
    public void testJsonSchemaGeneratorInputOutput() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.Builder(Stream.of(PersonInputOutputParams.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("InputOutput_test.json", file.relativePath());
        assertSchema(file);
    }

    private void assertSchema(GeneratedFile file) throws IOException {
        ObjectReader reader = new ObjectMapper().reader();
        JsonNode node = reader.readTree(file.contents());
        assertEquals("https://json-schema.org/draft/2019-09/schema", node.get("$schema").asText());
        assertEquals("object", node.get("type").asText());
        JsonNode properties = node.get("properties");
        assertEquals(3, properties.size());
        assertEquals("integer", properties.get("age").get("type").asText());
        assertEquals("string", properties.get("name").get("type").asText());
        JsonNode address = properties.get("address");
        assertEquals("object", address.get("type").asText());
        assertEquals("string", address.get("properties").get("street").get("type").asText());
    }


    @Test
    public void testNothingToDo() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.Builder(Stream.of(IgnoredClass.class)).build().generate();
        assertTrue(files.isEmpty());
    }
}
