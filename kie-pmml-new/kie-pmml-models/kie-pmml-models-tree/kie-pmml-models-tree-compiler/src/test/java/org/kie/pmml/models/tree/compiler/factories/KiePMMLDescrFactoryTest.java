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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.tree.LeafNode;
import org.dmg.pmml.tree.Node;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.lang.descr.ExprConstraintDescr.Type.NAMED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.DrooledModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLDescrFactory.MODIFY_STATUS_HOLDER;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLDescrFactory.PMML4_RESULT;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLDescrFactory.PMML4_RESULT_IDENTIFIER;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLDescrFactory.UPDATE_PMML4_RESULT;

public class KiePMMLDescrFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactoryTest.class);
    private static final String PARENT_PATH = "parent/path";
    private static final String PACKAGE_NAME = "package";
    private static final String CURRENT_RULE = "currentRule";
    private PackageDescrBuilder builder;
    private RuleDescrBuilder ruleBuilder;
    private CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder;
    private PMML pmml;
    private TreeModel treeModel;

    @Before
    public void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        treeModel = ((TreeModel) pmml.getModels().get(0));
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        ruleBuilder = builder.newRule().name(CURRENT_RULE);
        lhsBuilder = ruleBuilder.lhs();
    }

    @Test
    public void getBaseDescr() {
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        PackageDescr retrieved = KiePMMLDescrFactory.getBaseDescr(pmml.getDataDictionary(), treeModel, "org.test.package", fieldTypeMap);
        assertNotNull(retrieved);
        assertEquals(3, retrieved.getImports().size());
        assertEquals(KiePMMLStatusHolder.class.getName(), retrieved.getImports().get(0).getTarget());
        assertEquals(9, retrieved.getRules().size());
        assertEquals(5, retrieved.getTypeDeclarations().size());
        for (DataField dataField : pmml.getDataDictionary().getDataFields()) {
            String expectedGeneratedType = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
            assertNotNull(retrieved.getTypeDeclarations().stream().filter(typeDeclarationDescr -> expectedGeneratedType.equals(typeDeclarationDescr.getTypeName())).findFirst().orElse(null));
            assertTrue(fieldTypeMap.containsKey(dataField.getName().getValue()));
            assertEquals(expectedGeneratedType, fieldTypeMap.get(dataField.getName().getValue()).getGeneratedType());
        }
    }

    @Test
    public void declareRules() {
        PackageDescr packageDescr = builder.getDescr();
        assertEquals(1, packageDescr.getRules().size());
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = pmml.getDataDictionary()
                .getDataFields().stream()
                .collect(Collectors.toMap(dataField -> dataField.getName().getValue(),
                                          dataField -> new KiePMMLOriginalTypeGeneratedType(dataField.getDataType().value(), getSanitizedClassName(dataField.getName().getValue().toUpperCase()))));
        KiePMMLDescrFactory.declareRules(builder, treeModel.getNode(), PARENT_PATH, fieldTypeMap);
        assertEquals(10, packageDescr.getRules().size());
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            String ruleName = ruleDescr.getName();
            if (ruleName.equals("currentRule")) {
                continue;
            }
            String nodePath = ruleName.replace(PARENT_PATH + "_", "");
            String[] nodeSteps = nodePath.split("_");
            if (nodeSteps.length == 1) { // the root node
                assertEquals(nodeSteps[0], treeModel.getNode().getScore());
            } else {
                Node currentNode = treeModel.getNode();
                for (int i = 1; i < nodeSteps.length; i++) {
                    String nodeStep = nodeSteps[i];
                    currentNode = currentNode.getNodes().stream().filter(node -> nodeStep.equals(node.getScore())).findFirst().orElse(null);
                    assertNotNull(currentNode);
                }
            }
        }
    }

    @Test
    public void declareFinalLeafWhen() {
        String currentRule = "CURRENT_RULE";
        final RuleDescrBuilder ruleBuilder = builder.newRule().name(currentRule);
        final CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder = ruleBuilder.lhs();
        Node finalLeafNode = treeModel.getNode().getNodes().get(0).getNodes().get(1);
        assertTrue(finalLeafNode instanceof LeafNode || finalLeafNode.getNodes() == null || finalLeafNode.getNodes().isEmpty());
        KiePMMLDescrFactory.declareFinalLeafWhen(ruleBuilder, lhsBuilder, finalLeafNode);
        List<BaseDescr> lhsDescrs = lhsBuilder.getDescr().getDescrs();
        assertEquals(1, lhsDescrs.size());
        assertTrue(lhsBuilder.getDescr().getDescrs().get(0) instanceof PatternDescr);
        PatternDescr lhsPatternDescr = ((PatternDescr) lhsBuilder.getDescr().getDescrs().get(0));
        assertEquals(PMML4_RESULT, lhsPatternDescr.getObjectType());
        assertEquals(PMML4_RESULT_IDENTIFIER, lhsPatternDescr.getIdentifier());
        RuleDescr ruleDescr = ruleBuilder.getDescr();
        assertEquals(currentRule, ruleDescr.getName());
        String expectedModifyStatusHolder = String.format(MODIFY_STATUS_HOLDER, StatusCode.DONE.name());
        String expectedUpdatePmml4Result = String.format(UPDATE_PMML4_RESULT, StatusCode.OK.name(), finalLeafNode.getScore().toString());
        String consequence = ruleDescr.getConsequence().toString();
        assertNotNull(consequence);
        assertTrue(consequence.contains(expectedModifyStatusHolder));
        assertTrue(consequence.contains(expectedUpdatePmml4Result));
        assertTrue(consequence.replace(expectedModifyStatusHolder, "").isEmpty());
    }

    @Test
    public void declareBranchWhen() {
        String currentRule = "CURRENT_RULE";
        String parentPath = "parent_path";
        Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = pmml.getDataDictionary()
                .getDataFields().stream()
                .collect(Collectors.toMap(dataField -> dataField.getName().getValue(),
                                          dataField -> new KiePMMLOriginalTypeGeneratedType(dataField.getDataType().value(), getSanitizedClassName(dataField.getName().getValue().toUpperCase()))));
        final RuleDescrBuilder ruleBuilder = builder.newRule().name(currentRule);
        Node branchNode = treeModel.getNode();
        assertFalse(branchNode instanceof LeafNode || branchNode.getNodes() == null || branchNode.getNodes().isEmpty());
        KiePMMLDescrFactory.declareBranchWhen(builder, ruleBuilder, parentPath, branchNode, fieldTypeMap);
        System.out.println(builder);
    }

    @Test
    public void declarePredicate() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        Predicate predicate = getSimplePredicate("SimplePredicate", "VALUE", fieldTypeMap);
        KiePMMLDescrFactory.declarePredicate(lhsBuilder, predicate, fieldTypeMap);
        assertEquals(1, lhsBuilder.getDescr().getDescrs().size());
        final AndDescr descr = lhsBuilder.getDescr();
        commonVerifySimplePredicate(descr, (SimplePredicate) predicate);
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE", "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE) || operator.equals(CompoundPredicate.BooleanOperator.XOR)) {
                // TODO {gcardosi} Not implemented, yet
                continue;
            }
            predicate = new CompoundPredicate();
            ((CompoundPredicate) predicate).setBooleanOperator(operator);
            predicates.forEach(((CompoundPredicate) predicate)::addPredicates);
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declarePredicate(lhsBuilder, predicate, fieldTypeMap);
            assertEquals(1, descr.getDescrs().size());
            commonVerifySimplePredicates((ConditionalElementDescr) descr.getDescrs().get(0), predicates, operator, "SIMPLEPREDICATE");
        }
    }

    @Test
    public void declareSimplePredicate() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate predicate = getSimplePredicate("SimplePredicate", "VALUE", fieldTypeMap);
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = lhsBuilder.and();
        assertTrue(andBuilder.getDescr().getDescrs().isEmpty());
        KiePMMLDescrFactory.declareSimplePredicate(andBuilder, predicate, fieldTypeMap);
        final AndDescr descr = andBuilder.getDescr();
        commonVerifySimplePredicate(descr, predicate);
    }

    @Test
    public void declareCompoundPredicate() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE", "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        final AndDescr descr = lhsBuilder.getDescr();
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE) || operator.equals(CompoundPredicate.BooleanOperator.XOR)) {
                // TODO {gcardosi} Not implemented, yet
                continue;
            }
            CompoundPredicate predicate = new CompoundPredicate();
            predicate.setBooleanOperator(operator);
            predicates.forEach(predicate::addPredicates);
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declareCompoundPredicate(lhsBuilder, predicate, fieldTypeMap);
            commonVerifySimplePredicates((ConditionalElementDescr) descr.getDescrs().get(0), predicates, operator, "SIMPLEPREDICATE");
        }
    }

    @Test(expected = KiePMMLException.class)
    public void declareCompoundPredicateXORExceptionFewElements() throws Exception {
        CompoundPredicate predicate = new CompoundPredicate();
        predicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        predicate.addPredicates(getSimplePredicate("SimplePredicate", "VALUE", fieldTypeMap));
        try {
            KiePMMLDescrFactory.declareCompoundPredicate(lhsBuilder, predicate, fieldTypeMap);
        } catch (Exception e) {
            assertTrue(e instanceof KiePMMLException);
            assertEquals("At least two elements expected for XOR operations", e.getMessage());
            throw e;
        }
    }

    @Test(expected = KiePMMLException.class)
    public void declareCompoundPredicateXORExceptionTooMuchElements() throws Exception {
        CompoundPredicate predicate = new CompoundPredicate();
        predicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        IntStream.range(0, 3).forEach(i -> {
            predicate.addPredicates(getSimplePredicate("SimplePredicate" + i, "VALUE-" + i, fieldTypeMap));
        });
        try {
            KiePMMLDescrFactory.declareCompoundPredicate(lhsBuilder, predicate, fieldTypeMap);
        } catch (Exception e) {
            assertTrue(e instanceof KiePMMLException);
            assertEquals("More then two elements not managed, yet, for XOR operations", e.getMessage());
            throw e;
        }
    }

    @Test
    public void declareSimplePredicates() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SimplePredicate" + index, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = lhsBuilder.and();
        assertTrue(andBuilder.getDescr().getDescrs().isEmpty());
        final AndDescr descr = andBuilder.getDescr();
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE) || operator.equals(CompoundPredicate.BooleanOperator.XOR)) {
                // TODO {gcardosi} Not implemented, yet
                continue;
            }
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declareSimplePredicates(andBuilder, "SIMPLEPREDICATE", predicates, operator, fieldTypeMap);
            assertEquals(1, descr.getDescrs().size());
            commonVerifySimplePredicates(descr, predicates, operator, "SIMPLEPREDICATE");
        }
    }

    @Test
    public void declareTypes() {
        List<DataField> dataFields = Arrays.asList(getTypeDataField(), getDottedTypeDataField());
        DataDictionary dataDictionary = new DataDictionary(dataFields);
        assertTrue(builder.getDescr().getTypeDeclarations().isEmpty());
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDescrFactory.declareTypes(builder, dataDictionary, fieldTypeMap);
        assertEquals(2, builder.getDescr().getTypeDeclarations().size());
        IntStream.range(0, dataFields.size()).forEach(i -> commonVerifyTypeDeclarationDescr(dataFields.get(i), fieldTypeMap, builder.getDescr().getTypeDeclarations().get(i)));
    }

    @Test
    public void declareType() {
        DataField dataField = getTypeDataField();
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        KiePMMLDescrFactory.declareType(builder, dataField, fieldTypeMap);
        assertEquals(1, builder.getDescr().getTypeDeclarations().size());
        commonVerifyTypeDeclarationDescr(dataField, fieldTypeMap, builder.getDescr().getTypeDeclarations().get(0));
    }

    private void commonVerifySimplePredicate(AndDescr descr, SimplePredicate predicate) {
        final PatternDescr baseDescr = (PatternDescr) descr.getDescrs().get(0);
        assertEquals("SIMPLEPREDICATE", baseDescr.getObjectType());
        assertEquals(1, baseDescr.getDescrs().size());
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) baseDescr.getDescrs().get(0);
        assertEquals(NAMED, exprConstraintDescr.getType());
        String expected = String.format("value %s \"%s\"", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue());
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    private void commonVerifySimplePredicates(ConditionalElementDescr descr, List<SimplePredicate> predicates, CompoundPredicate.BooleanOperator operator, String expectedPredicate) {
        final PatternDescr patternDescr = (PatternDescr) descr.getDescrs().get(0);
//        assertEquals("SIMPLEPREDICATE", patternDescr.getObjectType());
        assertEquals(expectedPredicate, patternDescr.getObjectType());
        String operatorString;
        switch (operator) {
            case OR:
                operatorString = " || ";
                break;
            case AND:
            default:
                operatorString = " && ";
                break;
        }
        if (operator.equals(CompoundPredicate.BooleanOperator.XOR)) {
            commonVerifySimplePredicatesXOR(patternDescr, predicates);
        } else {
            commonVerifySimplePredicates(patternDescr, predicates, operatorString);
        }
    }

    private void commonVerifySimplePredicates(PatternDescr baseDescr, List<SimplePredicate> predicates, String operatorString) {
        int expectedDescrs = 1;
        assertEquals(expectedDescrs, baseDescr.getDescrs().size());
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) baseDescr.getDescrs().get(0);
        assertEquals(NAMED, exprConstraintDescr.getType());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate predicate = predicates.get(i);
            if (i > 0) {
                builder.append(operatorString);
            }
            builder.append(String.format("value %s \"%s\"", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue()));
        }
        String expected = builder.toString();
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    private void commonVerifySimplePredicatesXOR(PatternDescr baseDescr, List<SimplePredicate> predicates) {
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) baseDescr.getDescrs().get(0);
        assertEquals(NAMED, exprConstraintDescr.getType());
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < predicates.size(); i++) {
            if (i > 0) {
                builder.append(" & !");
            }
            SimplePredicate predicate = predicates.get(i);
            builder.append(String.format("(value %s \"%s\")", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue()));
        }
        builder.append(") | (!");
        for (int i = 0; i < predicates.size(); i++) {
            if (i > 0) {
                builder.append(" & ");
            }
            SimplePredicate predicate = predicates.get(i);
            builder.append(String.format("(value %s \"%s\")", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue()));
        }
        builder.append("))");
        String expected = builder.toString();
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    private void commonVerifyTypeDeclarationDescr(DataField dataField, Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final TypeDeclarationDescr typeDeclarationDescr) {
        String expectedGeneratedType = getSanitizedClassName(dataField.getName().getValue().toUpperCase());
        String expectedMappedOriginalType = DATA_TYPE.byName(dataField.getDataType().value()).getMappedClass().getSimpleName();
        assertEquals(expectedGeneratedType, typeDeclarationDescr.getTypeName());
        assertEquals(1, typeDeclarationDescr.getFields().size());
        assertTrue(typeDeclarationDescr.getFields().containsKey("value"));
        assertEquals(expectedMappedOriginalType, typeDeclarationDescr.getFields().get("value").getPattern().getObjectType());
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