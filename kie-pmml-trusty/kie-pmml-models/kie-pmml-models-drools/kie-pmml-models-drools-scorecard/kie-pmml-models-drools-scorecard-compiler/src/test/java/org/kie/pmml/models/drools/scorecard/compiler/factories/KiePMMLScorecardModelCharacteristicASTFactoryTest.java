/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.dmg.pmml.scorecard.Scorecard;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.ast.factories.KiePMMLDataDictionaryASTFactory;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldType;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.PATH_PATTERN;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.commons.utils.KiePMMLDroolsModelUtils.getCorrectlyFormattedResult;

public class KiePMMLScorecardModelCharacteristicASTFactoryTest {

    private static final String SOURCE_SAMPLE = "ScorecardSample.pmml";
    private final String fieldName = "age";
    private PMML samplePmml;
    private Scorecard scorecardModel;
    private DataDictionary dataDictionary;

    @Before
    public void setUp() throws Exception {
        samplePmml = TestUtils.loadFromFile(SOURCE_SAMPLE);
        assertNotNull(samplePmml);
        assertEquals(1, samplePmml.getModels().size());
        assertTrue(samplePmml.getModels().get(0) instanceof Scorecard);
        scorecardModel = ((Scorecard) samplePmml.getModels().get(0));
        dataDictionary = samplePmml.getDataDictionary();
    }

    @Test
    public void declareRulesFromCharacteristics() {
        Characteristics characteristics = scorecardModel.getCharacteristics();
        String parentPath = "_will";
        List<KiePMMLDroolsRule> retrieved = getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRulesFromCharacteristics(characteristics, parentPath, null);
        final List<Characteristic> characteristicList = characteristics.getCharacteristics();
        List<Attribute> attributes = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        for (int i = 0; i < characteristicList.size(); i++) {
            Characteristic characteristic = characteristicList.get(i);
            attributes.addAll(characteristic.getAttributes());
            for (int j = 0; j < characteristic.getAttributes().size(); j++) {
                Attribute attribute = characteristic.getAttributes().get(j);
                KiePMMLDroolsRule rule = retrieved.get(counter.incrementAndGet());
                int expectedOperatorValuesSize = 1;
                Integer expectedAndConstraints = null;
                Integer expectedInConstraints = null;
                BOOLEAN_OPERATOR expectedOperator = BOOLEAN_OPERATOR.AND;
                if (attribute.getPredicate() instanceof SimplePredicate) {
                    expectedAndConstraints = 1;
                }
                if (attribute.getPredicate() instanceof CompoundPredicate) {
                    expectedOperatorValuesSize = ((CompoundPredicate) attribute.getPredicate()).getPredicates().size();
                    expectedAndConstraints = 1;
                }
                if (attribute.getPredicate() instanceof SimpleSetPredicate) {
                    expectedInConstraints = 1;
                }
                boolean isLastCharacteristic = (i == characteristicList.size() - 1);
                String statusToSet = isLastCharacteristic ? DONE : String.format(PATH_PATTERN, parentPath, characteristicList.get(i + 1).getName());
                commonValidateRule(rule,
                                   attribute,
                                   statusToSet,
                                   parentPath + "_" + characteristic.getName(),
                                   j,
                                   isLastCharacteristic,
                                   expectedAndConstraints,
                                   expectedInConstraints,
                                   expectedOperator,
                                   null,
                                   expectedOperatorValuesSize);
            }
        }
        assertEquals(attributes.size() + 1, retrieved.size());
    }

    @Test
    public void declareRuleFromCharacteristicNotLastCharacteristic() {
        Characteristic characteristic = getCharacteristic();
        final String parentPath = "parent_path";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final boolean isLastCharacteristic = false;
        String[] expectedConstraints = {"value <= 5.0", "value >= 5.0 && value < 12.0"};
        int[] expectedOperatorValuesSizes = {1, 2};
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRuleFromCharacteristic(characteristic,
                                               parentPath,
                                               rules,
                                               statusToSet,
                                               isLastCharacteristic);
        assertEquals(characteristic.getAttributes().size(), rules.size());
        for (int i = 0; i < rules.size(); i++) {
            commonValidateRule(rules.get(i),
                               characteristic.getAttributes().get(i),
                               statusToSet,
                               parentPath + "_AgeScore",
                               i,
                               isLastCharacteristic,
                               1,
                               null,
                               BOOLEAN_OPERATOR.AND,
                               expectedConstraints[i],
                               expectedOperatorValuesSizes[i]
            );
        }
    }

    @Test
    public void declareRuleFromAttributeWithSimplePredicateNotLastCharacteristic() {
        Attribute attribute = getSimplePredicateAttribute();
        final String parentPath = "parent_path";
        final int attributeIndex = 2;
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final String characteristicReasonCode = "REASON_CODE";
        final double characteristicBaselineScore = 12;
        final boolean isLastCharacteristic = false;
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRuleFromAttribute(attribute, parentPath, attributeIndex, rules, statusToSet, characteristicReasonCode, characteristicBaselineScore, isLastCharacteristic);
        assertEquals(1, rules.size());
        commonValidateRule(rules.get(0),
                           attribute,
                           statusToSet,
                           parentPath,
                           attributeIndex,
                           isLastCharacteristic,
                           1,
                           null,
                           BOOLEAN_OPERATOR.AND,
                           "value <= 5.0",
                           1);
    }

    @Test
    public void declareRuleFromAttributeWithSimplePredicateUseReasonCodesTrue() {
        Attribute attribute = getSimplePredicateAttribute();
        final String parentPath = "parent_path";
        final int attributeIndex = 2;
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final String characteristicReasonCode = "REASON_CODE";
        final double characteristicBaselineScore = 12;
        final boolean isLastCharacteristic = false;
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(null, REASONCODE_ALGORITHM.POINTS_ABOVE)
                .declareRuleFromAttribute(attribute, parentPath, attributeIndex, rules, statusToSet, characteristicReasonCode, characteristicBaselineScore, isLastCharacteristic);
        assertEquals(1, rules.size());
        KiePMMLDroolsRule toValidate = rules.get(0);
        commonValidateRule(toValidate,
                           attribute,
                           statusToSet,
                           parentPath,
                           attributeIndex,
                           isLastCharacteristic,
                           1,
                           null,
                           BOOLEAN_OPERATOR.AND,
                           "value <= 5.0",
                           1);
        KiePMMLReasonCodeAndValue retrieved = toValidate.getReasonCodeAndValue();
        assertNotNull(retrieved);
        assertEquals(characteristicReasonCode, retrieved.getReasonCode());
        double expected = attribute.getPartialScore().doubleValue() - characteristicBaselineScore;
        assertEquals(expected, retrieved.getValue(), 0);
    }

    @Test
    public void declareRuleFromAttributeWithSimplePredicateLastCharacteristic() {
        Attribute attribute = getSimplePredicateAttribute();
        final String parentPath = "parent_path";
        final int attributeIndex = 2;
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final String characteristicReasonCode = "REASON_CODE";
        final double characteristicBaselineScore = 12;
        final boolean isLastCharacteristic = true;
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRuleFromAttribute(attribute, parentPath, attributeIndex, rules, statusToSet, characteristicReasonCode, characteristicBaselineScore, isLastCharacteristic);
        assertEquals(1, rules.size());
        commonValidateRule(rules.get(0),
                           attribute,
                           statusToSet,
                           parentPath,
                           attributeIndex,
                           isLastCharacteristic,
                           1,
                           null,
                           BOOLEAN_OPERATOR.AND,
                           "value <= 5.0",
                           1);
    }

    @Test
    public void declareRuleFromAttributeWithCompoundPredicate() {
        Attribute attribute = getCompoundPredicateAttribute();
        final String parentPath = "parent_path";
        final int attributeIndex = 2;
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final String characteristicReasonCode = "REASON_CODE";
        final double characteristicBaselineScore = 12;
        final boolean isLastCharacteristic = false;
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRuleFromAttribute(attribute, parentPath, attributeIndex, rules, statusToSet, characteristicReasonCode, characteristicBaselineScore, isLastCharacteristic);
        assertEquals(1, rules.size());
        commonValidateRule(rules.get(0),
                           attribute,
                           statusToSet,
                           parentPath,
                           attributeIndex,
                           isLastCharacteristic,
                           1,
                           null,
                           BOOLEAN_OPERATOR.AND,
                           "value >= 5.0 && value < 12.0",
                           2);
    }

    @Test
    public void declareRuleFromAttributeWithSimpleSetPredicate() {
        Attribute attribute = getSimpleSetPredicateAttribute();
        final String parentPath = "parent_path";
        final int attributeIndex = 2;
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        final String statusToSet = "status_to_set";
        final String characteristicReasonCode = "REASON_CODE";
        final double characteristicBaselineScore = 12;
        final boolean isLastCharacteristic = false;
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .declareRuleFromAttribute(attribute, parentPath, attributeIndex, rules, statusToSet, characteristicReasonCode, characteristicBaselineScore, isLastCharacteristic);
        assertEquals(1, rules.size());
        commonValidateRule(rules.get(0),
                           attribute,
                           statusToSet,
                           parentPath,
                           attributeIndex,
                           isLastCharacteristic,
                           null,
                           1,
                           null,
                           null,
                           null);
    }

    private void commonValidateRule(KiePMMLDroolsRule toValidate,
                                    Attribute attribute,
                                    String statusToSet,
                                    String parentPath,
                                    int attributeIndex,
                                    boolean isLastCharacteristic,
                                    Integer expectedAndConstraints,
                                    Integer expectedInConstraints,
                                    BOOLEAN_OPERATOR expectedOperator,
                                    String expectedConstraints,
                                    Integer expectedOperatorValuesSize) {
        assertEquals(String.format(PATH_PATTERN, parentPath, attributeIndex), toValidate.getName());
        assertEquals(statusToSet, toValidate.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), toValidate.getStatusConstraint());
        assertEquals(attribute.getPartialScore().doubleValue(), toValidate.getToAccumulate(), 0.0);
        if (isLastCharacteristic) {
            assertTrue(toValidate.isAccumulationResult());
            assertEquals(ResultCode.OK, toValidate.getResultCode());
        } else {
            assertFalse(toValidate.isAccumulationResult());
            assertNull(toValidate.getResultCode());
        }
        assertNull(toValidate.getResult());
        if (expectedAndConstraints != null) {
            assertEquals(expectedAndConstraints.intValue(), toValidate.getAndConstraints().size());
            commonValidateAndConstraint(toValidate.getAndConstraints().get(0), attribute, expectedOperator, expectedConstraints, expectedOperatorValuesSize);
        }
        if (expectedInConstraints != null) {
            assertEquals(expectedInConstraints.intValue(), toValidate.getInConstraints().size());
            commonValidateInConstraint(toValidate.getInConstraints(), attribute);
        }
    }

    private void commonValidateAndConstraint(KiePMMLFieldOperatorValue toValidate,
                                             Attribute attribute,
                                             BOOLEAN_OPERATOR expectedOperator,
                                             String expectedConstraints,
                                             int expectedOperatorValuesSize) {
        assertEquals(expectedOperator, toValidate.getOperator());
        if (expectedConstraints != null) {
            assertEquals(expectedConstraints, toValidate.getConstraintsAsString());
        }
        final List<KiePMMLOperatorValue> operatorValues = toValidate.getKiePMMLOperatorValues();
        assertEquals(expectedOperatorValuesSize, operatorValues.size());
        if (attribute.getPredicate() instanceof SimplePredicate) {
            commonValidateKiePMMLOperatorValue(operatorValues.get(0),
                                               (SimplePredicate) attribute.getPredicate());
        } else if (attribute.getPredicate() instanceof CompoundPredicate) {
            commonValidateKiePMMLOperatorValues(operatorValues, (CompoundPredicate) attribute.getPredicate());
        }
    }

    private void commonValidateInConstraint(Map<String, List<Object>> inConstraint,
                                            Attribute attribute) {
        if (attribute.getPredicate() instanceof SimpleSetPredicate) {
            commonValidateObjectValues(inConstraint.values().iterator().next(), (SimpleSetPredicate) attribute.getPredicate());
        }
    }

    private void commonValidateKiePMMLOperatorValue(KiePMMLOperatorValue toValidate, SimplePredicate simplePredicate) {
        OPERATOR expectedOperator = OPERATOR.byName(simplePredicate.getOperator().value());
        assertEquals(expectedOperator, toValidate.getOperator());
        Object expectedValue = getExpectedValue(simplePredicate);
        assertEquals(expectedValue, toValidate.getValue());
    }

    private Object getExpectedValue(SimplePredicate simplePredicate) throws RuntimeException {
        DATA_TYPE dataType = dataDictionary.getDataFields().stream().filter(dataField -> dataField.getName().equals(simplePredicate.getField()))
                .map(dataField -> DATA_TYPE.byName(dataField.getDataType().value()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to find DataField for " + simplePredicate.getField().getValue()));
        return getCorrectlyFormattedResult(simplePredicate.getValue(), dataType);
    }

    private void commonValidateObjectValues(List<Object> toValidate, SimpleSetPredicate simpleSetPredicate) {
        assertEquals(toValidate.size(), simpleSetPredicate.getArray().getN().intValue());
        String[] retrieved = ((String) simpleSetPredicate.getArray().getValue()).split(" ");
        for (int i = 0; i < toValidate.size(); i++) {
            assertEquals(toValidate.get(i), "\"" + retrieved[i] + "\"");
        }
    }

    private void commonValidateKiePMMLOperatorValues(List<KiePMMLOperatorValue> toValidate, CompoundPredicate compoundPredicate) {
        assertEquals(toValidate.size(), compoundPredicate.getPredicates().size());
        for (int i = 0; i < toValidate.size(); i++) {
            commonValidateKiePMMLOperatorValue(toValidate.get(i),
                                               (SimplePredicate) compoundPredicate.getPredicates().get(i));
        }
    }

    private Characteristic getCharacteristic() {
        Characteristic toReturn = new Characteristic();
        toReturn.setName("AgeScore");
        toReturn.addAttributes(getSimplePredicateAttribute(), getCompoundPredicateAttribute());
        return toReturn;
    }

    private Attribute getSimplePredicateAttribute() {
        final double partialScore = 10.0;
        Attribute toReturn = new Attribute();
        toReturn.setPartialScore(partialScore);
        toReturn.setPredicate(getSimplePredicate(fieldName,
                                                 DataType.DOUBLE,
                                                 5.0,
                                                 SimplePredicate.Operator.LESS_OR_EQUAL,
                                                 new HashMap<>()));
        return toReturn;
    }

    private Attribute getCompoundPredicateAttribute() {
        final double partialScore = 30.0;
        Attribute toReturn = new Attribute();
        toReturn.setPartialScore(partialScore);
        toReturn.setPredicate(getCompoundPredicate());
        return toReturn;
    }

    private Attribute getSimpleSetPredicateAttribute() {
        final double partialScore = -10.0;
        Attribute toReturn = new Attribute();
        toReturn.setPartialScore(partialScore);
        toReturn.setPredicate(getSimpleSetPredicate("occupation",
                                                    Array.Type.STRING,
                                                    Arrays.asList("SKYDIVER", "ASTRONAUT"),
                                                    SimpleSetPredicate.BooleanOperator.IS_IN,
                                                    new HashMap<>()));
        return toReturn;
    }

    private CompoundPredicate getCompoundPredicate() {
        CompoundPredicate toReturn = new CompoundPredicate();
        toReturn.setBooleanOperator(CompoundPredicate.BooleanOperator.AND);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        toReturn.addPredicates(getSimplePredicate(fieldName,
                                                  DataType.DOUBLE,
                                                  5.0,
                                                  SimplePredicate.Operator.GREATER_OR_EQUAL,
                                                  fieldTypeMap),
                               getSimplePredicate(fieldName,
                                                  DataType.DOUBLE,
                                                  12.0,
                                                  SimplePredicate.Operator.LESS_THAN,
                                                  fieldTypeMap));
        return toReturn;
    }

    @Test
    public void getKiePMMLReasonCodeAndValueUseReasonCodesFalse() {
        assertNull(getKiePMMLScorecardModelCharacteristicASTFactory().getKiePMMLReasonCodeAndValue(new Attribute(),
                                                                                                   "", 0));
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLReasonCodeAndValueUseReasonCodesTrueNoBaselineScore() {
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(null, null)
                .getKiePMMLReasonCodeAndValue(new Attribute(),
                                              "", null);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLReasonCodeAndValueUseReasonCodesTrueNoReasonCodeAlgorithm() {
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(null, null)
                .getKiePMMLReasonCodeAndValue(new Attribute(),
                                              "", 12);
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePMMLReasonCodeAndValueUseReasonCodesTrueNoReasonCode() {
        getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(null, REASONCODE_ALGORITHM.POINTS_ABOVE)
                .getKiePMMLReasonCodeAndValue(new Attribute(),
                                              "", 12);
    }

    @Test
    public void getKiePMMLReasonCodeAndValueUseReasonCodesTrue() {
        String characteristicReasonCode = "CHARACTERISTIC_REASON_CODE";
        String attributeReasonCode = "ATTRIBUTE_REASON_CODE";
        double baselineScore = 13;
        double characteristicBaselineScore = 24.45;
        double attributePartialScore = 13.17;
        Attribute attribute = new Attribute();
        attribute.setPartialScore(attributePartialScore);
        KiePMMLReasonCodeAndValue retrieved = getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(baselineScore, REASONCODE_ALGORITHM.POINTS_ABOVE)
                .getKiePMMLReasonCodeAndValue(attribute,
                                              characteristicReasonCode, null);
        assertNotNull(retrieved);
        assertEquals(characteristicReasonCode, retrieved.getReasonCode());
        double expected = attributePartialScore - baselineScore;
        assertEquals(expected, retrieved.getValue(), 0);
        retrieved = getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(baselineScore, REASONCODE_ALGORITHM.POINTS_ABOVE)
                .getKiePMMLReasonCodeAndValue(attribute,
                                              characteristicReasonCode, characteristicBaselineScore);
        assertNotNull(retrieved);
        assertEquals(characteristicReasonCode, retrieved.getReasonCode());
        expected = attributePartialScore - characteristicBaselineScore;
        assertEquals(expected, retrieved.getValue(), 0);
        attribute.setReasonCode(attributeReasonCode);
        retrieved = getKiePMMLScorecardModelCharacteristicASTFactory()
                .withReasonCodes(baselineScore, REASONCODE_ALGORITHM.POINTS_ABOVE)
                .getKiePMMLReasonCodeAndValue(attribute,
                                              characteristicReasonCode, characteristicBaselineScore);
        assertNotNull(retrieved);
        assertEquals(attributeReasonCode, retrieved.getReasonCode());
        assertEquals(expected, retrieved.getValue(), 0);
    }

    private KiePMMLScorecardModelCharacteristicASTFactory getKiePMMLScorecardModelCharacteristicASTFactory() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        DATA_TYPE targetType = getTargetFieldType(samplePmml.getDataDictionary(), scorecardModel);
        KiePMMLDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(samplePmml.getDataDictionary());
        assertFalse(fieldTypeMap.isEmpty());
        return KiePMMLScorecardModelCharacteristicASTFactory.factory(fieldTypeMap, Collections.emptyList(), targetType);
    }

    private SimplePredicate getSimplePredicate(final String predicateName,
                                               final DataType dataType,
                                               final Object value,
                                               final SimplePredicate.Operator operator,
                                               final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        fieldTypeMap.put(predicateName,
                         new KiePMMLOriginalTypeGeneratedType(dataType.value(),
                                                              getSanitizedClassName(predicateName.toUpperCase())));
        return PMMLModelTestUtils.getSimplePredicate(predicateName,
                                                     value,
                                                     operator);
    }

    private SimpleSetPredicate getSimpleSetPredicate(final String predicateName,
                                                     final Array.Type arrayType,
                                                     final List<String> values,
                                                     final SimpleSetPredicate.BooleanOperator booleanOperator,
                                                     final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        fieldTypeMap.put(predicateName,
                         new KiePMMLOriginalTypeGeneratedType(arrayType.value(),
                                                              getSanitizedClassName(predicateName.toUpperCase())));
        return PMMLModelTestUtils.getSimpleSetPredicate(predicateName,
                                                        arrayType,
                                                        values,
                                                        booleanOperator);
    }
}