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
package org.kie.kogito.pmml.openapi.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomMiningFields;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomOutputFields;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.CORRELATION_ID;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.DEFINITIONS;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.ENUM;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.INPUT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.OBJECT;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.OUTPUT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.PROPERTIES;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_CODE;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_OBJECT_NAME;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_VARIABLES;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.SEGMENTATION_ID;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.SEGMENT_ID;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.SEGMENT_INDEX;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.STRING;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.TYPE;

class PMMLOASResultImplTest {

    @Test
    void constructor() {
        PMMLOASResultImpl retrieved = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        assertNotNull(retrieved);
        ObjectNode jsonNodes = retrieved.jsonNodes;
        assertNotNull(jsonNodes);
        ObjectNode definitions = (ObjectNode) jsonNodes.get(DEFINITIONS);
        assertNotNull(definitions);
        ObjectNode outputSet = (ObjectNode) definitions.get(OUTPUT_SET);
        assertNotNull(outputSet);
        commonValidateOutputSet(outputSet);
    }

    @Test
    void addMiningFields() {
        PMMLOASResultImpl retrieved = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        final List<MiningField> miningFields = getRandomMiningFields();
        retrieved.addMiningFields(miningFields);
        ObjectNode jsonNodes = retrieved.jsonNodes;
        assertNotNull(jsonNodes);
        ObjectNode definitions = (ObjectNode) jsonNodes.get(DEFINITIONS);
        assertNotNull(definitions);
        ObjectNode inputSet = (ObjectNode) definitions.get(INPUT_SET);
        assertNotNull(inputSet);
    }

    @Test
    void addOutputFields() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode outputSetPropertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        assertNull(outputSetPropertiesNode.get(RESULT_VARIABLES));
        List<OutputField> toAdd = getRandomOutputFields();
        pmmlOAResult.addOutputFields(toAdd);
        assertNotNull(outputSetPropertiesNode.get(RESULT_VARIABLES));
        ObjectNode resultVariablesNode = (ObjectNode) outputSetPropertiesNode.get(RESULT_VARIABLES);
        assertNotNull(resultVariablesNode.get(PROPERTIES));
        ObjectNode resultVariablesPropertiesNode = (ObjectNode) resultVariablesNode.get(PROPERTIES);
        assertEquals(toAdd.size(), resultVariablesPropertiesNode.size());
        List<JsonNode> nodeList = StreamSupport
                .stream(resultVariablesPropertiesNode.spliterator(), false)
                .collect(Collectors.toList());
        nodeList.forEach(resultNode -> assertTrue(resultNode instanceof ObjectNode));
        toAdd.forEach(outputField -> {
            assertNotNull(resultVariablesPropertiesNode.get(outputField.getName()));
        });

    }

    @Test
    void addToResultSet() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        definitionsNode.removeAll();
        assertNull(definitionsNode.get(RESULT_SET));
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        pmmlOAResult.addToResultSet(fieldName, dataType, Collections.emptyList());
        assertNotNull(definitionsNode.get(RESULT_SET));
        ObjectNode resultSetNode = (ObjectNode) definitionsNode.get(RESULT_SET);
        ObjectNode resultSetPropertiesNode = (ObjectNode) resultSetNode.get(PROPERTIES);
        assertNotNull(resultSetPropertiesNode);
        assertNotNull(resultSetPropertiesNode.get(fieldName));
    }

    @Test
    void addToResultVariables() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode propertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        assertNull(propertiesNode.get(RESULT_VARIABLES));
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        pmmlOAResult.addToResultVariables(fieldName, dataType, Collections.emptyList());
        assertNotNull(propertiesNode.get(RESULT_VARIABLES));
        ObjectNode resultVariablesNode = (ObjectNode) propertiesNode.get(RESULT_VARIABLES);
        ObjectNode resultVariablesPropertiesNode = (ObjectNode) resultVariablesNode.get(PROPERTIES);
        assertNotNull(resultVariablesPropertiesNode);
        assertNotNull(resultVariablesPropertiesNode.get(fieldName));
    }

    @Test
    void conditionallyCreateResultSetNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        definitionsNode.removeAll();
        assertNull(definitionsNode.get(RESULT_SET));
        ObjectNode created = pmmlOAResult.conditionallyCreateResultSetNode();
        assertNotNull(created);
        assertEquals(created, definitionsNode.get(RESULT_SET));
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateResultSetNode();
        assertEquals(created, notCreated);
    }

    @Test
    void conditionallyCreateResultVariablesNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode propertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        propertiesNode.removeAll();
        assertNull(propertiesNode.get(RESULT_VARIABLES));
        ObjectNode created = pmmlOAResult.conditionallyCreateResultVariablesNode();
        assertNotNull(created);
        assertEquals(created, propertiesNode.get(RESULT_VARIABLES));
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateResultVariablesNode();
        assertEquals(created, notCreated);
    }

    @Test
    void conditionallyCreateSetNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        String nodeToCreate = "nodeToCreate";
        assertNull(definitionsNode.get(nodeToCreate));
        ObjectNode created = pmmlOAResult.conditionallyCreateSetNode(nodeToCreate);
        assertNotNull(created);
        assertEquals(created, definitionsNode.get(nodeToCreate));
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateSetNode(nodeToCreate);
        assertEquals(created, notCreated);
    }

    private void commonValidateOutputSet(ObjectNode toValidate) {
        JsonNode typeNode = toValidate.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(OBJECT, ((TextNode) typeNode).asText());
        JsonNode propertiesNode = toValidate.get(PROPERTIES);
        assertNotNull(propertiesNode);
        assertTrue(propertiesNode instanceof ObjectNode);
        JsonNode correlationIdNode = propertiesNode.get(CORRELATION_ID);
        assertNotNull(correlationIdNode);
        assertTrue(correlationIdNode instanceof ObjectNode);
        typeNode = correlationIdNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(STRING, ((TextNode) typeNode).asText());
        JsonNode segmentationIdNode = propertiesNode.get(SEGMENTATION_ID);
        assertNotNull(segmentationIdNode);
        assertTrue(segmentationIdNode instanceof ObjectNode);
        typeNode = segmentationIdNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(STRING, ((TextNode) typeNode).asText());
        JsonNode segmentIdNode = propertiesNode.get(SEGMENT_ID);
        assertNotNull(segmentIdNode);
        assertTrue(segmentIdNode instanceof ObjectNode);
        typeNode = segmentIdNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(STRING, ((TextNode) typeNode).asText());
        JsonNode segmentIndexNode = propertiesNode.get(SEGMENT_INDEX);
        assertNotNull(segmentIndexNode);
        assertTrue(segmentIndexNode instanceof ObjectNode);
        typeNode = segmentIndexNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals("integer", ((TextNode) typeNode).asText());
        JsonNode resultCodeNode = propertiesNode.get(RESULT_CODE);
        assertNotNull(resultCodeNode);
        assertTrue(resultCodeNode instanceof ObjectNode);
        typeNode = resultCodeNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(STRING, ((TextNode) typeNode).asText());
        JsonNode enumNode = resultCodeNode.get(ENUM);
        assertNotNull(enumNode);
        assertTrue(enumNode instanceof ArrayNode);
        assertEquals(ResultCode.values().length, ((ArrayNode) enumNode).size());
        final Iterator<JsonNode> enumElements = enumNode.elements();
        Arrays.stream(ResultCode.values()).forEach(resultCode -> {
            boolean find = false;
            while (enumElements.hasNext()) {
                JsonNode node = enumElements.next();
                assertTrue(node instanceof TextNode);
                if (resultCode.getName().equals(((TextNode) node).asText())) {
                    find = true;
                    break;
                }
            }
            assertTrue(find);
        });
        JsonNode resultObjectNameNode = propertiesNode.get(RESULT_OBJECT_NAME);
        assertNotNull(resultObjectNameNode);
        assertTrue(resultObjectNameNode instanceof ObjectNode);
        typeNode = resultObjectNameNode.get(TYPE);
        assertNotNull(typeNode);
        assertTrue(typeNode instanceof TextNode);
        assertEquals(STRING, ((TextNode) typeNode).asText());
    }
}
