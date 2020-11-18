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
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.victools.jsonschema.generator.SchemaVersion;
import org.junit.jupiter.api.Test;
import org.kie.kogito.UserTask;
import org.kie.kogito.UserTaskParam;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaGeneratorTest {

    private enum Color {
        GREEN,
        WHITE
    }

    @UserTask(taskName = "test", processName = "org.jbpm.test")
    private static class PersonInputParams {

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private String name;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private Address address;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private Color color;
    }

    @UserTask(taskName = "test", processName = "org.jbpm.test")
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

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private Color color;
    }
    
    @UserTask(taskName = "name with spaces", processName = "InputOutput")
    private static class WhitespacesTask {

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private int age;

        @UserTaskParam(UserTaskParam.ParamType.INPUT)
        private String name;
    }

    private static class Address {

        @SuppressWarnings("unused")
        private String street;
        @SuppressWarnings("unused")
        private Date date;
    }

    private static class IgnoredClass {

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private int age;
    }

    @Test
    public void testSimpleSchemaGenerator() throws IOException {
        Collection<GeneratedFile> files =
                new JsonSchemaGenerator.SimpleBuilder(Thread.currentThread().getContextClassLoader())
                        .addSchemaName(PersonInputParams.class.getName(), "org.jbpm.test", "test")
                        .addSchemaName(PersonOutputParams.class.getName(), "org.jbpm.test", "test")
                        .build()
                        .generate();

        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("org#jbpm#test_test.json", file.relativePath());
        assertSchema(file, SchemaVersion.DRAFT_7);

        Collection<GeneratedFile> filesFromClasses =
                new JsonSchemaGenerator.ClassBuilder(
                        Stream.of(PersonInputParams.class, PersonOutputParams.class, IgnoredClass.class))
                        .build().generate();
        assertEquals(1, filesFromClasses.size());
        GeneratedFile fileFromClasses = filesFromClasses.iterator().next();

        assertEquals(fileFromClasses.relativePath(), file.relativePath(),
                     "must have the same path of a class-based generator");
        assertArrayEquals(fileFromClasses.contents(), file.contents(),
                     "must have the same contents of a class-based generator");
    }

    @Test
    public void testJsonSchemaGenerator() throws IOException {
        Collection<GeneratedFile> files =
                new JsonSchemaGenerator.ClassBuilder(
                        Stream.of(PersonInputParams.class, PersonOutputParams.class, IgnoredClass.class))
                        .build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("org#jbpm#test_test.json", file.relativePath());
        assertSchema(file, SchemaVersion.DRAFT_7);
    }

    @Test
    public void testJsonSchemaGeneratorNonExistingDraft() throws IOException {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonSchemaGenerator.ClassBuilder builder =
                    new JsonSchemaGenerator.ClassBuilder(
                            Stream.of(PersonInputParams.class, PersonOutputParams.class, IgnoredClass.class))
                            .withSchemaNameFunction(c -> "pepe")
                            .withSchemaVersion("NON_EXISTING_DRAFT");
            builder.build().generate();
        });
    }

    @Test
    public void testJsonSchemaGeneratorDraft2019() throws IOException {
        Collection<GeneratedFile> files =
                new JsonSchemaGenerator.ClassBuilder(
                        Stream.of(PersonInputParams.class, PersonOutputParams.class, IgnoredClass.class))
                        .withSchemaVersion("DRAFT_2019_09").build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("org#jbpm#test_test.json", file.relativePath());
        assertSchema(file, SchemaVersion.DRAFT_2019_09);
    }

    @Test
    public void testJsonSchemaGeneratorInputOutput() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(PersonInputOutputParams.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("InputOutput_test.json", file.relativePath());
        assertSchema(file, SchemaVersion.DRAFT_7);
    }
    
    @Test
    public void testJsonSchemaGeneratorWithSpace() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(WhitespacesTask.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals("InputOutput_name_with_spaces.json", file.relativePath());
    }

    private void assertSchema(GeneratedFile file, SchemaVersion schemaVersion) throws IOException {
        ObjectReader reader = new ObjectMapper().reader();
        JsonNode node = reader.readTree(file.contents());
        assertEquals(schemaVersion.getIdentifier(), node.get("$schema").asText());
        assertEquals("object", node.get("type").asText());
        JsonNode properties = node.get("properties");
        assertEquals(4, properties.size());
        assertEquals("integer", properties.get("age").get("type").asText());
        assertEquals("string", properties.get("name").get("type").asText());
        JsonNode color = properties.get("color");
        assertEquals("string", color.get("type").asText());
        assertTrue(color.get("enum") instanceof ArrayNode);
        ArrayNode colors = (ArrayNode) color.get("enum");
        Set<Color> colorValues = EnumSet.noneOf(Color.class);
        colors.forEach(x -> colorValues.add(Color.valueOf(x.asText())));
        assertArrayEquals(Color.values(), colorValues.toArray());
        JsonNode address = properties.get("address");
        assertEquals("object", address.get("type").asText());
        JsonNode addressProperties = address.get("properties");
        assertEquals("string", addressProperties.get("street").get("type").asText());
        JsonNode dateNode = addressProperties.get("date");
        assertEquals("string", dateNode.get("type").asText());
        assertEquals("date-time", dateNode.get("format").asText());
    }

    @Test
    public void testNothingToDo() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(IgnoredClass.class)).build().generate();
        assertTrue(files.isEmpty());
    }
}
