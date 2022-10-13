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
package org.kie.kogito.pmml.openapi.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.pmml.openapi.PMMLOASUtils;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.pmml.CommonTestUtility.getFromArrayNode;
import static org.kie.kogito.pmml.CommonTestUtility.getFromJsonNodeList;
import static org.kie.kogito.pmml.CommonTestUtility.getKiePMMLModelInternal;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomMiningFields;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomOutputField;
import static org.kie.kogito.pmml.CommonTestUtility.getRandomOutputFields;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.DEFINITIONS;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.INPUT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.OBJECT;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.OUTPUT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.PROPERTIES;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.REQUIRED;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_SET;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.RESULT_VARIABLES;
import static org.kie.kogito.pmml.openapi.api.PMMLOASResult.TYPE;

class PMMLOASResultFactoryTest {

    @Test
    void getPMMLOASResultNoMiningFieldsNoOutputFields() {
        final List<MiningField> miningFields = Collections.emptyList();
        final List<OutputField> outputFields = Collections.emptyList();
        final KiePMMLModel kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
        final PMMLOASResult retrieved = PMMLOASResultFactory.getPMMLOASResult(kiePMMLModel);
        assertThat(retrieved).isNotNull();

        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertThat(jsonNodes).isNotNull().isNotEmpty();
        assertThat(jsonNodes.get(DEFINITIONS)).isNotNull();

        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertThat(definitionsNode).isNotEmpty();
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        assertThat(definitionsNode.get(RESULT_SET)).isNull();
        assertThat(definitionsNode.get(OUTPUT_SET)).isNotNull();
        assertThat(definitionsNode.get(OUTPUT_SET).get(RESULT_VARIABLES)).isNull();
    }

    @Test
    void getPMMLOASResultMiningFieldsNoOutputFields() {
        final List<MiningField> miningFields = getRandomMiningFields();
        final List<MiningField> predictedFields = miningFields.stream().filter(PMMLOASUtils::isPredicted).collect(Collectors.toList());
        final List<OutputField> outputFields = Collections.emptyList();
        final KiePMMLModel kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
        final PMMLOASResult retrieved = PMMLOASResultFactory.getPMMLOASResult(kiePMMLModel);
        assertThat(retrieved).isNotNull();

        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertThat(jsonNodes).isNotNull().isNotEmpty();
        assertThat(jsonNodes.get(DEFINITIONS)).isNotNull();

        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertThat(definitionsNode.isEmpty()).isFalse();
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        commonValidateResultSet(definitionsNode.get(RESULT_SET), predictedFields);
        assertThat(definitionsNode.get(OUTPUT_SET)).isNotNull();
        assertThat(definitionsNode.get(OUTPUT_SET).get(PROPERTIES)).isNotNull();
        assertThat(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES)).isNotNull();
    }

    @Test
    void getPMMLOASResultMiningFieldsOutputFields() {
        final List<MiningField> miningFields = getRandomMiningFields();
        final List<MiningField> predictedFields = miningFields.stream().filter(PMMLOASUtils::isPredicted).collect(Collectors.toList());
        final List<OutputField> outputFields = getRandomOutputFields();
        outputFields.add(getRandomOutputField(miningFields.get(miningFields.size() - 1).getName()));
        final KiePMMLModel kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
        final PMMLOASResult retrieved = PMMLOASResultFactory.getPMMLOASResult(kiePMMLModel);
        assertThat(retrieved).isNotNull();

        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertThat(jsonNodes).isNotNull().isNotEmpty();
        assertThat(jsonNodes.get(DEFINITIONS)).isNotNull();

        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertThat(definitionsNode).isNotEmpty();
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        commonValidateResultSet(definitionsNode.get(RESULT_SET), predictedFields);
        assertThat(definitionsNode.get(OUTPUT_SET)).isNotNull();
        assertThat(definitionsNode.get(OUTPUT_SET).get(PROPERTIES)).isNotNull();
        assertThat(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES)).isNotNull();
        commonValidateOutputSet(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES), outputFields);
    }

    private void commonValidateInputSet(final JsonNode toValidate, final List<MiningField> miningFields) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.get(TYPE).asText()).isEqualTo(OBJECT);
        assertThat(toValidate.get(REQUIRED)).isNotNull();
        assertThat(toValidate.get(PROPERTIES)).isNotNull();
        final ArrayNode requiredNode = (ArrayNode) toValidate.get(REQUIRED);
        List<MiningField> requiredMiningFields = miningFields.stream().filter(PMMLOASUtils::isRequired).collect(Collectors.toList());
        assertThat(requiredNode).hasSameSizeAs(requiredMiningFields);

        final List<JsonNode> requiredJsonNodes = getFromArrayNode(requiredNode);
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        List<MiningField> active = miningFields.stream().filter(miningField -> !PMMLOASUtils.isPredicted(miningField)).collect(Collectors.toList());
        assertThat(propertiesNode).hasSameSizeAs(active);

        assertThat(requiredMiningFields).allMatch(miningField -> getFromJsonNodeList(requiredJsonNodes, miningField.getName()) != null);

        assertThat(active).allSatisfy(miningField -> {
            JsonNode property = propertiesNode.get(miningField.getName());
            assertThat(property).isNotNull();
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertThat(typeFieldNode.get(TYPE)).isNotNull();
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(miningField.getDataType());
            assertThat(typeNode.asText()).isEqualTo(mappedType);
        });
    }

    private void commonValidateResultSet(final JsonNode toValidate, final List<MiningField> miningFields) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.get(TYPE).asText()).isEqualTo(OBJECT);
        assertThat(toValidate.get(PROPERTIES)).isNotNull();
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        assertThat(miningFields).isNotEmpty().allSatisfy(miningField -> {
            JsonNode property = propertiesNode.get(miningField.getName());
            assertThat(property).isNotNull();
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertThat(typeFieldNode.get(TYPE)).isNotNull();
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(miningField.getDataType());
            assertThat(typeNode.asText()).isEqualTo(mappedType);
        });
    }

    private void commonValidateOutputSet(final JsonNode toValidate, final List<OutputField> outputFields) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.get(TYPE).asText()).isEqualTo(OBJECT);
        assertThat(toValidate.get(PROPERTIES)).isNotNull();
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        assertThat(outputFields).isNotEmpty().allSatisfy(outputField -> {
            JsonNode property = propertiesNode.get(outputField.getName());
            assertThat(property).isNotNull();
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertThat(typeFieldNode.get(TYPE)).isNotNull();
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(outputField.getDataType());
            assertThat(typeNode.asText()).isEqualTo(mappedType);
        });
    }
}
