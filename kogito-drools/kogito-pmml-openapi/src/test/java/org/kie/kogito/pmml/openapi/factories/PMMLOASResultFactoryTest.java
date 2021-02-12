package org.kie.kogito.pmml.openapi.factories;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.pmml.openapi.PMMLOASUtils;
import org.kie.kogito.pmml.openapi.api.PMMLOASResult;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getFromArrayNode;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getFromJsonNodeList;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getKiePMMLModelInternal;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getRandomMiningFields;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getRandomOutputField;
import static org.kie.kogito.pmml.openapi.CommonTestUtility.getRandomOutputFields;
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
        assertNotNull(retrieved);
        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertNotNull(jsonNodes);
        assertFalse(jsonNodes.isEmpty());
        assertNotNull(jsonNodes.get(DEFINITIONS));
        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertFalse(definitionsNode.isEmpty());
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        assertNull(definitionsNode.get(RESULT_SET));
        assertNotNull(definitionsNode.get(OUTPUT_SET));
        assertNull(definitionsNode.get(OUTPUT_SET).get(RESULT_VARIABLES));
    }

    @Test
    void getPMMLOASResultMiningFieldsNoOutputFields() {
        final List<MiningField> miningFields = getRandomMiningFields();
        final List<MiningField> predictedFields = miningFields.stream().filter(PMMLOASUtils::isPredicted).collect(Collectors.toList());
        final List<OutputField> outputFields = Collections.emptyList();
        final KiePMMLModel kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
        final PMMLOASResult retrieved = PMMLOASResultFactory.getPMMLOASResult(kiePMMLModel);
        assertNotNull(retrieved);
        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertNotNull(jsonNodes);
        assertFalse(jsonNodes.isEmpty());
        assertNotNull(jsonNodes.get(DEFINITIONS));
        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertFalse(definitionsNode.isEmpty());
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        commonValidateResultSet(definitionsNode.get(RESULT_SET), predictedFields);
        assertNotNull(definitionsNode.get(OUTPUT_SET));
        assertNotNull(definitionsNode.get(OUTPUT_SET).get(PROPERTIES));
        assertNotNull(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES));
    }

    @Test
    void getPMMLOASResultMiningFieldsOutputFields() {
        final List<MiningField> miningFields = getRandomMiningFields();
        final List<MiningField> predictedFields = miningFields.stream().filter(PMMLOASUtils::isPredicted).collect(Collectors.toList());
        final List<OutputField> outputFields = getRandomOutputFields();
        outputFields.add(getRandomOutputField(miningFields.get(miningFields.size() - 1).getName()));
        final KiePMMLModel kiePMMLModel = getKiePMMLModelInternal(miningFields, outputFields);
        final PMMLOASResult retrieved = PMMLOASResultFactory.getPMMLOASResult(kiePMMLModel);
        assertNotNull(retrieved);
        final ObjectNode jsonNodes = retrieved.jsonSchemaNode();
        assertNotNull(jsonNodes);
        assertFalse(jsonNodes.isEmpty());
        assertNotNull(jsonNodes.get(DEFINITIONS));
        final JsonNode definitionsNode = jsonNodes.get(DEFINITIONS);
        assertFalse(definitionsNode.isEmpty());
        commonValidateInputSet(definitionsNode.get(INPUT_SET), miningFields);
        commonValidateResultSet(definitionsNode.get(RESULT_SET), predictedFields);
        assertNotNull(definitionsNode.get(OUTPUT_SET));
        assertNotNull(definitionsNode.get(OUTPUT_SET).get(PROPERTIES));
        assertNotNull(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES));
        commonValidateOutputSet(definitionsNode.get(OUTPUT_SET).get(PROPERTIES).get(RESULT_VARIABLES), outputFields);
    }

    private void commonValidateInputSet(final JsonNode toValidate, final List<MiningField> miningFields) {
        assertNotNull(toValidate);
        assertEquals(OBJECT, toValidate.get(TYPE).asText());
        assertNotNull(toValidate.get(REQUIRED));
        assertNotNull(toValidate.get(PROPERTIES));
        final ArrayNode requiredNode = (ArrayNode) toValidate.get(REQUIRED);
        List<MiningField> requiredMiningFields = miningFields.stream().filter(PMMLOASUtils::isRequired).collect(Collectors.toList());
        assertEquals(requiredMiningFields.size(), requiredNode.size());
        final List<JsonNode> requiredJsonNodes = getFromArrayNode(requiredNode);
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        List<MiningField> active = miningFields.stream().filter(miningField -> !PMMLOASUtils.isPredicted(miningField)).collect(Collectors.toList());
        assertEquals(active.size(), propertiesNode.size());
        for (MiningField miningField : requiredMiningFields) {
            JsonNode required = getFromJsonNodeList(requiredJsonNodes, miningField.getName());
            assertNotNull(required);
        }
        for (MiningField miningField : active) {
            JsonNode property = propertiesNode.get(miningField.getName());
            assertNotNull(property);
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertNotNull(typeFieldNode.get(TYPE));
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(miningField.getDataType());
            assertEquals(mappedType, typeNode.asText());
        }
    }

    private void commonValidateResultSet(final JsonNode toValidate, final List<MiningField> miningFields) {
        assertNotNull(toValidate);
        assertEquals(OBJECT, toValidate.get(TYPE).asText());
        assertNotNull(toValidate.get(PROPERTIES));
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        for (MiningField miningField : miningFields) {
            JsonNode property = propertiesNode.get(miningField.getName());
            assertNotNull(property);
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertNotNull(typeFieldNode.get(TYPE));
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(miningField.getDataType());
            assertEquals(mappedType, typeNode.asText());
        }
    }

    private void commonValidateOutputSet(final JsonNode toValidate, final List<OutputField> outputFields) {
        assertNotNull(toValidate);
        assertEquals(OBJECT, toValidate.get(TYPE).asText());
        assertNotNull(toValidate.get(PROPERTIES));
        final ObjectNode propertiesNode = (ObjectNode) toValidate.get(PROPERTIES);
        for (OutputField outputField : outputFields) {
            JsonNode property = propertiesNode.get(outputField.getName());
            assertNotNull(property);
            final ObjectNode typeFieldNode = (ObjectNode) property;
            assertNotNull(typeFieldNode.get(TYPE));
            final TextNode typeNode = (TextNode) typeFieldNode.get(TYPE);
            String mappedType = PMMLOASUtils.getMappedType(outputField.getDataType());
            assertEquals(mappedType, typeNode.asText());
        }
    }
}