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

import java.util.Collections;
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

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(retrieved).isNotNull();
        ObjectNode jsonNodes = retrieved.jsonNodes;
        assertThat(jsonNodes).isNotNull();
        ObjectNode definitions = (ObjectNode) jsonNodes.get(DEFINITIONS);
        assertThat(definitions).isNotNull();
        ObjectNode outputSet = (ObjectNode) definitions.get(OUTPUT_SET);
        assertThat(outputSet).isNotNull();
        commonValidateOutputSet(outputSet);
    }

    @Test
    void addMiningFields() {
        PMMLOASResultImpl retrieved = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        final List<MiningField> miningFields = getRandomMiningFields();
        retrieved.addMiningFields(miningFields);
        ObjectNode jsonNodes = retrieved.jsonNodes;
        assertThat(jsonNodes).isNotNull();
        ObjectNode definitions = (ObjectNode) jsonNodes.get(DEFINITIONS);
        assertThat(definitions).isNotNull();
        ObjectNode inputSet = (ObjectNode) definitions.get(INPUT_SET);
        assertThat(inputSet).isNotNull();
    }

    @Test
    void addOutputFields() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode outputSetPropertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        assertThat(outputSetPropertiesNode.get(RESULT_VARIABLES)).isNull();
        List<OutputField> toAdd = getRandomOutputFields();
        pmmlOAResult.addOutputFields(toAdd);
        assertThat(outputSetPropertiesNode.get(RESULT_VARIABLES)).isNotNull();
        ObjectNode resultVariablesNode = (ObjectNode) outputSetPropertiesNode.get(RESULT_VARIABLES);
        assertThat(resultVariablesNode.get(PROPERTIES)).isNotNull();
        ObjectNode resultVariablesPropertiesNode = (ObjectNode) resultVariablesNode.get(PROPERTIES);
        assertThat(resultVariablesPropertiesNode).hasSameSizeAs(toAdd);

        List<JsonNode> nodeList = StreamSupport
                .stream(resultVariablesPropertiesNode.spliterator(), false)
                .collect(Collectors.toList());
        assertThat(nodeList).isNotEmpty().allMatch(resultNode -> resultNode instanceof ObjectNode);
        assertThat(toAdd).allSatisfy(outputField -> assertThat(resultVariablesPropertiesNode.get(outputField.getName())).isNotNull());

    }

    @Test
    void addToResultSet() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        definitionsNode.removeAll();
        assertThat(definitionsNode.get(RESULT_SET)).isNull();
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        pmmlOAResult.addToResultSet(fieldName, dataType, Collections.emptyList());
        assertThat(definitionsNode.get(RESULT_SET)).isNotNull();
        ObjectNode resultSetNode = (ObjectNode) definitionsNode.get(RESULT_SET);
        ObjectNode resultSetPropertiesNode = (ObjectNode) resultSetNode.get(PROPERTIES);
        assertThat(resultSetPropertiesNode).isNotNull();
        assertThat(resultSetPropertiesNode.get(fieldName)).isNotNull();
    }

    @Test
    void addToResultVariables() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode propertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        assertThat(propertiesNode.get(RESULT_VARIABLES)).isNull();
        String fieldName = "fieldName";
        DATA_TYPE dataType = DATA_TYPE.DOUBLE;
        pmmlOAResult.addToResultVariables(fieldName, dataType, Collections.emptyList());
        assertThat(propertiesNode.get(RESULT_VARIABLES)).isNotNull();
        ObjectNode resultVariablesNode = (ObjectNode) propertiesNode.get(RESULT_VARIABLES);
        ObjectNode resultVariablesPropertiesNode = (ObjectNode) resultVariablesNode.get(PROPERTIES);
        assertThat(resultVariablesPropertiesNode).isNotNull();
        assertThat(resultVariablesPropertiesNode.get(fieldName)).isNotNull();
    }

    @Test
    void conditionallyCreateResultSetNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        definitionsNode.removeAll();
        assertThat(definitionsNode.get(RESULT_SET)).isNull();
        ObjectNode created = pmmlOAResult.conditionallyCreateResultSetNode();
        assertThat(created).isNotNull();
        assertThat(definitionsNode.get(RESULT_SET)).isEqualTo(created);
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateResultSetNode();
        assertThat(notCreated).isEqualTo(created);
    }

    @Test
    void conditionallyCreateResultVariablesNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        ObjectNode outputSetNode = (ObjectNode) definitionsNode.get(OUTPUT_SET);
        ObjectNode propertiesNode = (ObjectNode) outputSetNode.get(PROPERTIES);
        propertiesNode.removeAll();
        assertThat(propertiesNode.get(RESULT_VARIABLES)).isNull();
        ObjectNode created = pmmlOAResult.conditionallyCreateResultVariablesNode();
        assertThat(created).isNotNull();
        assertThat(propertiesNode.get(RESULT_VARIABLES)).isEqualTo(created);
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateResultVariablesNode();
        assertThat(notCreated).isEqualTo(created);
    }

    @Test
    void conditionallyCreateSetNode() {
        PMMLOASResultImpl pmmlOAResult = (PMMLOASResultImpl) new PMMLOASResultImpl.Builder().build();
        ObjectNode jsonNodes = pmmlOAResult.jsonNodes;
        ObjectNode definitionsNode = (ObjectNode) jsonNodes.get(DEFINITIONS);
        String nodeToCreate = "nodeToCreate";
        assertThat(definitionsNode.get(nodeToCreate)).isNull();
        ObjectNode created = pmmlOAResult.conditionallyCreateSetNode(nodeToCreate);
        assertThat(created).isNotNull();
        assertThat(definitionsNode.get(nodeToCreate)).isEqualTo(created);
        ObjectNode notCreated = pmmlOAResult.conditionallyCreateSetNode(nodeToCreate);
        assertThat(notCreated).isEqualTo(created);
    }

    private void commonValidateOutputSet(ObjectNode toValidate) {
        JsonNode typeNode = toValidate.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(OBJECT);
        JsonNode propertiesNode = toValidate.get(PROPERTIES);
        assertThat(propertiesNode).isNotNull();
        assertThat(propertiesNode).isInstanceOf(ObjectNode.class);
        JsonNode correlationIdNode = propertiesNode.get(CORRELATION_ID);
        assertThat(correlationIdNode).isNotNull();
        assertThat(correlationIdNode).isInstanceOf(ObjectNode.class);
        typeNode = correlationIdNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(STRING);
        JsonNode segmentationIdNode = propertiesNode.get(SEGMENTATION_ID);
        assertThat(segmentationIdNode).isNotNull();
        assertThat(segmentationIdNode).isInstanceOf(ObjectNode.class);
        typeNode = segmentationIdNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(STRING);
        JsonNode segmentIdNode = propertiesNode.get(SEGMENT_ID);
        assertThat(segmentIdNode).isNotNull();
        assertThat(segmentIdNode).isInstanceOf(ObjectNode.class);
        typeNode = segmentIdNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(STRING);
        JsonNode segmentIndexNode = propertiesNode.get(SEGMENT_INDEX);
        assertThat(segmentIndexNode).isNotNull();
        assertThat(segmentIndexNode).isInstanceOf(ObjectNode.class);
        typeNode = segmentIndexNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo("integer");
        JsonNode resultCodeNode = propertiesNode.get(RESULT_CODE);
        assertThat(resultCodeNode).isNotNull();
        assertThat(resultCodeNode).isInstanceOf(ObjectNode.class);
        typeNode = resultCodeNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(STRING);
        JsonNode enumNode = resultCodeNode.get(ENUM);
        assertThat(enumNode).isNotNull();
        assertThat(enumNode).isInstanceOf(ArrayNode.class);
        assertThat(((ArrayNode) enumNode)).hasSameSizeAs(ResultCode.values());
        List<JsonNode> enumElements = StreamSupport
                .stream(enumNode.spliterator(), false)
                .collect(Collectors.toList());
        assertThat(enumElements).allMatch(node -> node instanceof TextNode);
        assertThat(ResultCode.values()).extracting(resultCode -> resultCode.getName()).allSatisfy(name -> assertThat(enumElements).extracting(enumElement -> enumElement.asText()).contains(name));

        JsonNode resultObjectNameNode = propertiesNode.get(RESULT_OBJECT_NAME);
        assertThat(resultObjectNameNode).isNotNull();
        assertThat(resultObjectNameNode).isInstanceOf(ObjectNode.class);
        typeNode = resultObjectNameNode.get(TYPE);
        assertThat(typeNode).isNotNull();
        assertThat(typeNode).isInstanceOf(TextNode.class);
        assertThat(((TextNode) typeNode).asText()).isEqualTo(STRING);
    }
}
