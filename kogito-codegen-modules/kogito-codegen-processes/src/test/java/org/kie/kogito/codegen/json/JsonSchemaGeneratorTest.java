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
package org.kie.kogito.codegen.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jbpm.util.JsonSchemaUtil;
import org.junit.jupiter.api.Test;
import org.kie.kogito.ProcessInput;
import org.kie.kogito.UserTask;
import org.kie.kogito.UserTaskParam;
import org.kie.kogito.codegen.VariableInfo;
import org.kie.kogito.codegen.api.GeneratedFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.victools.jsonschema.generator.SchemaVersion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaGeneratorTest {

    private static final String ALL_OF = "allOf";
    private static final String REF = "$ref";
    private static final String INPUT = "input";
    private static final String OUTPUT = "output";

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

        @UserTaskParam(UserTaskParam.ParamType.OUTPUT)
        private Address address;

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

    @ProcessInput(processName = "processName")
    private static class ProcessInputModel {

        @VariableInfo
        private Color color;

        @VariableInfo
        private Person person;
    }

    @ProcessInput(processName = "emptyProcessName")
    private static class EmptyProcessInputModel {

        private Color color;

        private Person person;
    }

    private static class Person {

        @SuppressWarnings("unused")
        private String name;
        @SuppressWarnings("unused")
        private int age;
        @SuppressWarnings("unused")
        private Address address;
        @SuppressWarnings("unused")
        private Person parent;
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
    public void testJsonSchemaGenerator() throws IOException {
        Collection<GeneratedFile> files =
                new JsonSchemaGenerator.ClassBuilder(
                        Stream.of(EmptyProcessInputModel.class, PersonInputParams.class, ProcessInputModel.class, PersonOutputParams.class, IgnoredClass.class))
                                .build().generate();
        assertEquals(3, files.size());
        Iterator<GeneratedFile> iterator = files.iterator();
        assertEmptyProcessSchema("emptyProcessName.json", iterator.next(), SchemaVersion.DRAFT_2019_09);
        assertProcessSchema("processName.json", iterator.next(), SchemaVersion.DRAFT_2019_09);
        assertTaskSchema("org#jbpm#test_test.json", iterator.next(), SchemaVersion.DRAFT_2019_09, Arrays.asList("name", "address", "color"), Arrays.asList("name", "address", "age"));
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
    public void testJsonSchemaGeneratorDraft7() throws IOException {
        Collection<GeneratedFile> files =
                new JsonSchemaGenerator.ClassBuilder(
                        Stream.of(EmptyProcessInputModel.class, PersonInputParams.class, ProcessInputModel.class, PersonOutputParams.class, IgnoredClass.class))
                                .withSchemaVersion("DRAFT_7").build().generate();
        assertEquals(3, files.size());
        Iterator<GeneratedFile> iterator = files.iterator();
        assertEmptyProcessSchema("emptyProcessName.json", iterator.next(), SchemaVersion.DRAFT_7);
        assertProcessSchema("processName.json", iterator.next(), SchemaVersion.DRAFT_7);
        assertTaskSchema("org#jbpm#test_test.json", iterator.next(), SchemaVersion.DRAFT_7, Arrays.asList("name", "address", "color"), Arrays.asList("name", "address", "age"));
    }

    @Test
    public void testJsonSchemaGeneratorInputOutput() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(PersonInputOutputParams.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();

        assertTaskSchema("InputOutput_test.json", file, SchemaVersion.DRAFT_2019_09, Arrays.asList("name", "address", "color"), List.of("age"));
    }

    @Test
    public void testJsonSchemaGeneratorWithSpace() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(WhitespacesTask.class)).build().generate();
        assertEquals(1, files.size());
        GeneratedFile file = files.iterator().next();
        assertEquals(JsonSchemaUtil.getJsonDir().resolve("InputOutput_name_with_spaces.json").toString(), file.relativePath());
    }

    @Test
    public void testNothingToDo() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(IgnoredClass.class)).build().generate();
        assertTrue(files.isEmpty());
    }

    @Test
    public void testJsonSchemaGenerationForProcess() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(ProcessInputModel.class)).build().generate();
        assertEquals(1, files.size());
        assertProcessSchema("processName.json", files.iterator().next(), SchemaVersion.DRAFT_2019_09);
    }

    @Test
    public void testJsonSchemaGenerationForEmptyProcessModel() throws IOException {
        Collection<GeneratedFile> files = new JsonSchemaGenerator.ClassBuilder(Stream.of(EmptyProcessInputModel.class)).build().generate();
        assertEquals(1, files.size());

        assertEmptyProcessSchema("emptyProcessName.json", files.iterator().next(), SchemaVersion.DRAFT_2019_09);
    }

    private void assertEmptyProcessSchema(String fileName, GeneratedFile file, SchemaVersion schemaVersion) throws IOException {
        assertEquals(JsonSchemaUtil.getJsonDir().resolve(fileName).toString(), file.relativePath());

        ObjectReader reader = new ObjectMapper().reader();
        JsonNode node = reader.readTree(file.contents());

        assertEquals(schemaVersion.getIdentifier(), node.get("$schema").asText());
        assertEquals("object", node.get("type").asText());
        assertNull(node.get("properties"));
    }

    private void assertProcessSchema(String fileName, GeneratedFile file, SchemaVersion schemaVersion) throws IOException {
        assertEquals(JsonSchemaUtil.getJsonDir().resolve(fileName).toString(), file.relativePath());
        ObjectReader reader = new ObjectMapper().reader();
        JsonNode node = reader.readTree(file.contents());
        assertEquals(schemaVersion.getIdentifier(), node.get("$schema").asText());

        // assert definitions
        String definitionsPath = resolveDefinitionsProperty(schemaVersion);
        assertEquals(3, node.get(definitionsPath).size());
        assertPersonNode(node.get(definitionsPath).get("Person"), definitionsPath);
        assertAddressNode(node.get(definitionsPath).get("Address"));
        assertColorNode(node.get(definitionsPath).get("Color"));

        assertEquals("object", node.get("type").asText());
        JsonNode properties = node.get("properties");
        assertEquals(2, properties.size());
        JsonNode color = properties.get("color");
        assertEquals("#/" + definitionsPath + "/Color", color.get("$ref").asText());
        JsonNode address = properties.get("person");
        assertEquals("#/" + definitionsPath + "/Person", address.get("$ref").asText());
    }

    private void assertTaskSchema(String fileName, GeneratedFile file, SchemaVersion schemaVersion, List<String> inputs, List<String> outputs) throws IOException {
        assertEquals(JsonSchemaUtil.getJsonDir().resolve(fileName).toString(), file.relativePath());
        ObjectReader reader = new ObjectMapper().reader();
        JsonNode node = reader.readTree(file.contents());
        assertEquals(schemaVersion.getIdentifier(), node.get("$schema").asText());

        // assert definitions
        String definitionsPath = resolveDefinitionsProperty(schemaVersion);
        assertEquals(2, node.get(definitionsPath).size());
        assertAddressNode(node.get(definitionsPath).get("Address"));
        assertColorNode(node.get(definitionsPath).get("Color"));

        assertEquals("object", node.get("type").asText());
        JsonNode properties = node.get("properties");
        assertEquals(4, properties.size());
        assertBasicTaskField(properties, "age", "integer", inputs, outputs);
        assertBasicTaskField(properties, "name", "string", inputs, outputs);
        assertTaskFieldWithRef(properties, "color", "#/" + definitionsPath + "/Color", inputs, outputs);
        assertTaskFieldWithRef(properties, "address", "#/" + definitionsPath + "/Address", inputs, outputs);
    }

    private void assertBasicTaskField(JsonNode properties, String name, String type, List<String> inputs, List<String> outputs) {
        JsonNode property = properties.get(name);
        assertEquals(type, property.get("type").asText());
        if (inputs.contains(name)) {
            checkNodeHasInputField(property);
        }
        if (outputs.contains(name)) {
            checkNodeHasOutputField(property);
        }
    }

    private void checkNodeHasInputField(JsonNode node) {
        assertTrue(node.has(INPUT));
        assertTrue(node.get(INPUT).asBoolean());
    }

    private void checkNodeHasOutputField(JsonNode node) {
        assertTrue(node.has(OUTPUT));
        assertTrue(node.get(OUTPUT).asBoolean());
    }

    private void assertChildAssignmentNode(ArrayNode arrayNode, String assignment) {
        Iterable<JsonNode> iterable = () -> arrayNode.iterator();
        JsonNode childAssignment = StreamSupport.stream(iterable.spliterator(), false)
                .filter(node -> node.has(assignment))
                .findFirst()
                .orElse(null);

        assertNotNull(childAssignment);
        assertTrue(childAssignment.get(assignment).asBoolean());
    }

    private void assertTaskFieldWithRef(JsonNode properties, String name, String refPath, List<String> inputs, List<String> outputs) {
        JsonNode property = properties.get(name);
        if (property.has(ALL_OF)) {
            ArrayNode allOf = (ArrayNode) property.get(ALL_OF);
            assertEquals(refPath, allOf.get(0).get(REF).asText());
            if (inputs.contains(name)) {
                assertChildAssignmentNode(allOf, INPUT);
            }
            if (outputs.contains(name)) {
                assertChildAssignmentNode(allOf, OUTPUT);
            }
        } else {
            assertEquals(refPath, property.get("$ref").asText());
            if (inputs.contains(name)) {
                checkNodeHasInputField(property);
            }
            if (outputs.contains(name)) {
                checkNodeHasOutputField(property);
            }
        }

    }

    private void assertPersonNode(JsonNode personNode, String definitionsPath) {
        assertNotNull(personNode);

        assertEquals("object", personNode.get("type").asText());
        JsonNode personProperties = personNode.get("properties");
        assertEquals("string", personProperties.get("name").get("type").asText());
        assertEquals("integer", personProperties.get("age").get("type").asText());
        assertEquals("#/" + definitionsPath + "/Address", personProperties.get("address").get(REF).asText());
        assertEquals("#/" + definitionsPath + "/Person", personProperties.get("parent").get(REF).asText());
    }

    private void assertAddressNode(JsonNode addressNode) {
        assertNotNull(addressNode);

        assertEquals("object", addressNode.get("type").asText());
        JsonNode addressProperties = addressNode.get("properties");
        assertEquals("string", addressProperties.get("street").get("type").asText());
        JsonNode dateNode = addressProperties.get("date");
        assertEquals("string", dateNode.get("type").asText());
        assertEquals("date-time", dateNode.get("format").asText());
    }

    private void assertColorNode(JsonNode colorNode) {
        assertNotNull(colorNode);

        assertEquals("string", colorNode.get("type").asText());
        assertTrue(colorNode.get("enum") instanceof ArrayNode);
        ArrayNode colors = (ArrayNode) colorNode.get("enum");
        Set<Color> colorValues = EnumSet.noneOf(Color.class);
        colors.forEach(x -> colorValues.add(Color.valueOf(x.asText())));
        assertArrayEquals(Color.values(), colorValues.toArray());
    }

    private String resolveDefinitionsProperty(SchemaVersion schemaVersion) {
        return SchemaVersion.DRAFT_2019_09.equals(schemaVersion) ? "$defs" : "definitions";
    }
}
