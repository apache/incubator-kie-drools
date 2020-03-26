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

package org.kie.pmml.models.tree.compiler.factories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.tree.ClassifierNode;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledAST;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledType;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.DrooledModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_PATTERN;

public class KiePMMLTreeModelASTFactoryTest {

    private static final String SOURCE_GOLFING = "TreeSample.pmml";
    private static final String SOURCE_IRIS = "irisTree.pmml";
    private PMML golfingPmml;
    private TreeModel golfingModel;
    private PMML irisPmml;
    private TreeModel irisModel;

    @Before
    public void setUp() throws Exception {
        golfingPmml = TestUtils.loadFromFile(SOURCE_GOLFING);
        assertNotNull(golfingPmml);
        assertEquals(1, golfingPmml.getModels().size());
        assertTrue(golfingPmml.getModels().get(0) instanceof TreeModel);
        golfingModel = ((TreeModel) golfingPmml.getModels().get(0));
        irisPmml = TestUtils.loadFromFile(SOURCE_IRIS);
        assertNotNull(irisPmml);
        assertEquals(1, irisPmml.getModels().size());
        assertTrue(irisPmml.getModels().get(0) instanceof TreeModel);
        irisModel = ((TreeModel) irisPmml.getModels().get(0));
    }

    @Test
    public void getKiePMMLDrooledGolfingAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDrooledAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDrooledAST(golfingPmml.getDataDictionary(), golfingModel, fieldTypeMap);
        assertNotNull(retrieved);
        assertFalse(retrieved.getTypes().isEmpty());
        assertFalse(retrieved.getRules().isEmpty());
    }

    @Test
    public void getKiePMMLDrooledIrisAST() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDrooledAST retrieved = KiePMMLTreeModelASTFactory.getKiePMMLDrooledAST(irisPmml.getDataDictionary(), irisModel, fieldTypeMap);
        assertNotNull(retrieved);
        assertFalse(retrieved.getTypes().isEmpty());
        assertFalse(retrieved.getRules().isEmpty());
    }

    @Test
    public void declareRulesFromRootGolfingNode() {
        Node rootNode = golfingModel.getNode();
        assertEquals("will play", rootNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLTreeModelASTFactory.declareTypes(golfingPmml.getDataDictionary(), fieldTypeMap);
        KiePMMLTreeModelASTFactory.declareRulesFromRootNode(rootNode,
                                                            "_will play",
                                                            fieldTypeMap);
        assertFalse(fieldTypeMap.isEmpty());
    }

    @Test
    public void declareRulesFromRootIrisNode() {
        Node rootNode = irisModel.getNode();
        assertEquals("setosa", rootNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLTreeModelASTFactory.declareTypes(irisPmml.getDataDictionary(), fieldTypeMap);
        KiePMMLTreeModelASTFactory.declareRulesFromRootNode(rootNode,
                                                            "_setosa",
                                                            fieldTypeMap);
        assertFalse(fieldTypeMap.isEmpty());
    }

    @Test
    public void declareIntermediateRuleFromGolfingNode() {
        Node finalNode = golfingModel.getNode()
                .getNodes().get(0);
        assertEquals("will play", finalNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLTreeModelASTFactory.declareTypes(golfingPmml.getDataDictionary(), fieldTypeMap);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareIntermediateRuleFromNode(finalNode,
                                                                   "_will play",
                                                                   fieldTypeMap,
                                                                   rules);
        assertFalse(rules.isEmpty());
    }

    @Test
    public void declareIntermediateRuleFromIrisNode() {
        Node finalNode = irisModel.getNode()
                .getNodes().get(1);
        assertEquals("versicolor", finalNode.getScore());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLTreeModelASTFactory.declareTypes(irisPmml.getDataDictionary(), fieldTypeMap);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareIntermediateRuleFromNode(finalNode,
                                                                   "_setosa",
                                                                   fieldTypeMap,
                                                                   rules);
        assertFalse(rules.isEmpty());
    }

    @Test
    public void declareRuleFromCompoundPredicateAndOrXorFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE)) {
                continue;
            }
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(operator);
            predicates.forEach(compoundPredicate::addPredicates);
            final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
            KiePMMLTreeModelASTFactory.declareRuleFromCompoundPredicateAndOrXor(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, true);
            assertEquals(1, rules.size());
            final KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            assertEquals(currentRule, retrieved.getName());
            assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
            Map<String, List<KiePMMLOperatorValue>> constraints = null;
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    constraints = retrieved.getAndConstraints();
                    break;
                case OR:
                    constraints = retrieved.getOrConstraints();
                    break;
                case XOR:
                    constraints = retrieved.getXorConstraints();
                    break;
                default:
                    continue;
            }
            assertNotNull(constraints);
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateAndOrXorNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE)) {
                continue;
            }
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(operator);
            predicates.forEach(compoundPredicate::addPredicates);
            final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
            KiePMMLTreeModelASTFactory.declareRuleFromCompoundPredicateAndOrXor(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, false);
            assertEquals(1, rules.size());
            final KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            assertEquals(currentRule, retrieved.getName());
            assertEquals(currentRule, retrieved.getStatusToSet());
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            Map<String, List<KiePMMLOperatorValue>> constraints = null;
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    constraints = retrieved.getAndConstraints();
                    break;
                case OR:
                    constraints = retrieved.getOrConstraints();
                    break;
                case XOR:
                    constraints = retrieved.getXorConstraints();
                    break;
                default:
                    continue;
            }
            assertNotNull(constraints);
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateSurrogateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareRuleFromCompoundPredicateSurrogate(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, true);
        assertEquals(predicates.size(), rules.size());
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate simplePredicate = predicates.get(i);
            KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
            assertEquals(expectedRule, retrieved.getName());
            if (i < predicates.size() - 1) {
                assertEquals(currentRule, retrieved.getStatusToSet());
            } else {
                assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
            }
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateSurrogateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareRuleFromCompoundPredicateSurrogate(compoundPredicate, parentPath, currentRule, result, fieldTypeMap, rules, false);
        assertEquals(predicates.size(), rules.size());
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate simplePredicate = predicates.get(i);
            KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
            assertEquals(expectedRule, retrieved.getName());
            if (i < predicates.size() - 1) {
                assertEquals(currentRule, retrieved.getStatusToSet());
            } else {
                assertEquals(parentPath, retrieved.getStatusToSet());
            }
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
        }
    }

    @Test
    public void declareRuleFromSimplePredicateSurrogate() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        String statusToSet = StatusCode.DONE.getName();
        KiePMMLTreeModelASTFactory.declareRuleFromSimplePredicateSurrogate(simplePredicate, parentPath, currentRule, statusToSet, result, fieldTypeMap, rules);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(statusToSet, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(declaredType, retrieved.getIfBreakField());
        assertEquals(simplePredicate.getOperator().value(), retrieved.getIfBreakOperator());
        assertEquals(simplePredicate.getValue(), retrieved.getIfBreakValue());
        assertNull(retrieved.getAndConstraints());
        assertEquals(result, retrieved.getResult());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromSimplePredicateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareRuleFromSimplePredicate(simplePredicate, parentPath, currentRule, result, fieldTypeMap, rules, true);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
        assertEquals(result, retrieved.getResult());
        final Map<String, List<KiePMMLOperatorValue>> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        assertTrue(andConstraints.containsKey(declaredType));
        List<KiePMMLOperatorValue> operatorValues = andConstraints.get(declaredType);
        assertNotNull(operatorValues);
        assertEquals(1, operatorValues.size());
        KiePMMLOperatorValue operatorValue = operatorValues.get(0);
        assertEquals(simplePredicate.getOperator().value(), operatorValue.getOperator());
        assertEquals(simplePredicate.getValue(), operatorValue.getValue());
    }

    @Test
    public void declareIntermediateRuleFromSimplePredicateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelASTFactory.declareRuleFromSimplePredicate(simplePredicate, parentPath, currentRule, result, fieldTypeMap, rules, false);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(currentRule, retrieved.getStatusToSet());
        final Map<String, List<KiePMMLOperatorValue>> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        assertTrue(andConstraints.containsKey(declaredType));
        List<KiePMMLOperatorValue> operatorValues = andConstraints.get(declaredType);
        assertNotNull(operatorValues);
        assertEquals(1, operatorValues.size());
        KiePMMLOperatorValue operatorValue = operatorValues.get(0);
        assertEquals(simplePredicate.getOperator().value(), operatorValue.getOperator());
        assertEquals(simplePredicate.getValue(), operatorValue.getValue());
    }

    @Test
    public void declareTypes() {
        List<DataField> dataFields = Arrays.asList(getTypeDataField(), getDottedTypeDataField(), getTypeDataField(), getDottedTypeDataField());
        DataDictionary dataDictionary = new DataDictionary(dataFields);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        Queue<KiePMMLDrooledType> retrieved = KiePMMLTreeModelASTFactory.declareTypes(dataDictionary, fieldTypeMap);
        assertNotNull(retrieved);
        assertEquals(dataFields.size(), retrieved.size());
        IntStream.range(0, dataFields.size()).forEach(i -> commonVerifyTypeDeclarationDescr(dataFields.get(i), fieldTypeMap, retrieved.poll()));
    }

    @Test
    public void declareType() {
        DataField dataField = getTypeDataField();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDrooledType retrieved = KiePMMLTreeModelASTFactory.declareType(dataField, fieldTypeMap);
        assertNotNull(retrieved);
        commonVerifyTypeDeclarationDescr(dataField, fieldTypeMap, retrieved);
    }

    @Test
    public void isFinalLeaf() {
        Node node = new LeafNode();
        assertTrue(KiePMMLTreeModelASTFactory.isFinalLeaf(node));
        node = new ClassifierNode();
        assertTrue(KiePMMLTreeModelASTFactory.isFinalLeaf(node));
        node.addNodes(new LeafNode());
        assertFalse(KiePMMLTreeModelASTFactory.isFinalLeaf(node));
    }

    private void commonVerifyTypeDeclarationDescr(DataField dataField, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final KiePMMLDrooledType kiePMMLDrooledType) {
        String expectedGeneratedType = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
        String expectedMappedOriginalType = DATA_TYPE.byName(dataField.getDataType().value()).getMappedClass().getSimpleName();
        assertEquals(expectedGeneratedType, kiePMMLDrooledType.getName());
        assertEquals(expectedMappedOriginalType, kiePMMLDrooledType.getType());
        assertTrue(fieldTypeMap.containsKey(dataField.getName().getValue()));
        KiePMMLOriginalTypeGeneratedType kiePMMLOriginalTypeGeneratedType = fieldTypeMap.get(dataField.getName().getValue());
        assertEquals(dataField.getDataType().value(), kiePMMLOriginalTypeGeneratedType.getOriginalType());
        assertEquals(expectedGeneratedType, kiePMMLOriginalTypeGeneratedType.getGeneratedType());
    }

    private SimplePredicate getSimplePredicate(String predicateName, String value, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        FieldName fieldName = FieldName.create(predicateName);
        fieldTypeMap.put(fieldName.getValue(), new KiePMMLOriginalTypeGeneratedType(DataType.STRING.value(), getSanitizedClassName(fieldName.getValue().toUpperCase())));
        SimplePredicate toReturn = new SimplePredicate();
        toReturn.setField(fieldName);
        toReturn.setOperator(SimplePredicate.Operator.LESS_THAN);
        toReturn.setValue(value);
        return toReturn;
    }

    private DataField getTypeDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setDataType(DataType.DATE);
        toReturn.setName(FieldName.create("dataField"));
        return toReturn;
    }

    private DataField getDottedTypeDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setDataType(DataType.BOOLEAN);
        toReturn.setName(FieldName.create("dotted.field"));
        return toReturn;
    }
}