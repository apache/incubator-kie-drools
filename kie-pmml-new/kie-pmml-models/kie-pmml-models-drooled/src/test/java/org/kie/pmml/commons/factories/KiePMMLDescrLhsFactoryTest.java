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

package org.kie.pmml.commons.factories;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.factories.KiePMMLDescrLhsFactory.INPUT_FIELD;
import static org.kie.pmml.commons.factories.KiePMMLDescrLhsFactory.INPUT_FIELD_CONDITIONAL;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.STATUS_HOLDER;

public class KiePMMLDescrLhsFactoryTest {

    private static final String PACKAGE_NAME = "package";
    private static final String CURRENT_RULE = "currentRule";
    private CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder;

    @Before
    public void setUp() throws Exception {
        PackageDescrBuilder builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        RuleDescrBuilder ruleBuilder = builder.newRule().name(CURRENT_RULE);
        lhsBuilder = ruleBuilder.lhs();
    }

    @Test
    public void declareLhs() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDrooledRule rule = KiePMMLDrooledRule.builder(name, statusToSet).build();
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareLhs(rule);
        assertNotNull(lhsBuilder.getDescr());
        assertNotNull(lhsBuilder.getDescr().getDescrs());
        assertEquals(1, lhsBuilder.getDescr().getDescrs().size());
        assertTrue(lhsBuilder.getDescr().getDescrs().get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) lhsBuilder.getDescr().getDescrs().get(0);
        assertEquals(KiePMMLStatusHolder.class.getSimpleName(), patternDescr.getObjectType());
        assertEquals(STATUS_HOLDER, patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        assertNull(exprConstraintDescr.getExpression());
    }

    @Test
    public void declareConstraintAndOr() {
        String patternType = "TEMPERATURE";
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        String operator = "&&";
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintAndOr(operator, patternType, kiePMMLOperatorValues);
        assertNotNull(lhsBuilder.getDescr());
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(1, descrs.size());
        assertTrue(descrs.get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertEquals(patternType, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value < 35 && value > 85";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test(expected = KiePMMLException.class)
    public void declareConstraintsXorWrongInput() {
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        String patternType = "TEMPERATURE";
        final Map<String, List<KiePMMLOperatorValue>> xorConstraints = Collections.singletonMap(patternType, kiePMMLOperatorValues);
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsXor(xorConstraints);
    }

    @Test
    public void declareConstraintsXor() {
        String temperatureField = "TEMPERATURE";
        String humidityField = "HUMIDITY";
        List<KiePMMLOperatorValue> temperatureValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        List<KiePMMLOperatorValue> humidityValues = Arrays.asList(new KiePMMLOperatorValue("<", 56), new KiePMMLOperatorValue(">", 91));
        final Map<String, List<KiePMMLOperatorValue>> xorConstraints = new LinkedHashMap<>();
        xorConstraints.put(temperatureField, temperatureValues);
        xorConstraints.put(humidityField, humidityValues);
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsXor(xorConstraints);
        assertNotNull(lhsBuilder.getDescr());
        assertNotNull(lhsBuilder.getDescr().getDescrs());
        assertEquals(1, lhsBuilder.getDescr().getDescrs().size());
        assertTrue(lhsBuilder.getDescr().getDescrs().get(0) instanceof AndDescr);
        AndDescr rootAndDescr = (AndDescr) lhsBuilder.getDescr().getDescrs().get(0);
        assertEquals(2, rootAndDescr.getDescrs().size());
        assertTrue(rootAndDescr.getDescrs().get(0) instanceof NotDescr);
        assertTrue(rootAndDescr.getDescrs().get(1) instanceof ExistsDescr);
        // "Not" construct
        NotDescr notDescr = (NotDescr)rootAndDescr.getDescrs().get(0);
        assertEquals(1, notDescr.getDescrs().size());
        assertTrue(notDescr.getDescrs().get(0) instanceof AndDescr);
        AndDescr notAndDescr = (AndDescr) notDescr.getDescrs().get(0);
        assertTrue(notAndDescr.getDescrs().get(0) instanceof PatternDescr);
        assertTrue(notAndDescr.getDescrs().get(1) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) notAndDescr.getDescrs().get(0);
        assertEquals(temperatureField, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value < 35 && value > 85";
        assertEquals(expected, exprConstraintDescr.getExpression());
        patternDescr = (PatternDescr) notAndDescr.getDescrs().get(1);
        assertEquals(humidityField, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        expected = "value < 56 && value > 91";
        assertEquals(expected, exprConstraintDescr.getExpression());
        // "Exists" construct
        ExistsDescr existsDescr = (ExistsDescr) rootAndDescr.getDescrs().get(1);
        assertEquals(1, existsDescr.getDescrs().size());
        assertTrue(existsDescr.getDescrs().get(0) instanceof OrDescr);
        OrDescr existsOrDescr = (OrDescr)existsDescr.getDescrs().get(0);
        assertEquals(2, existsOrDescr.getDescrs().size());
        assertTrue(existsOrDescr.getDescrs().get(0) instanceof PatternDescr);
        assertTrue(existsOrDescr.getDescrs().get(1) instanceof OrDescr);
        patternDescr = (PatternDescr) existsOrDescr.getDescrs().get(0);
        assertEquals(temperatureField, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        expected = "value < 35 || value > 85";
        assertEquals(expected, exprConstraintDescr.getExpression());
        OrDescr nestedOrDescr = (OrDescr) existsOrDescr.getDescrs().get(1);
        assertEquals(1, nestedOrDescr.getDescrs().size());
        assertTrue(nestedOrDescr.getDescrs().get(0) instanceof PatternDescr);
        patternDescr = (PatternDescr) nestedOrDescr.getDescrs().get(0);
        assertEquals(humidityField, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        expected = "value < 56 || value > 91";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test
    public void declareNotConstraint() {
        String patternType = "TEMPERATURE";
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, NotDescr>, AndDescr> andNotBuilder = lhsBuilder.and().not().and();
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareNotConstraint(andNotBuilder, patternType, kiePMMLOperatorValues);
        final List<BaseDescr> descrs = andNotBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(1, descrs.size());
        assertTrue(descrs.get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertEquals(patternType, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value < 35 && value > 85";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test
    public void declareExistsConstraint() {
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = Arrays.asList(new KiePMMLOperatorValue("<", 35), new KiePMMLOperatorValue(">", 85));
        String patternType = "TEMPERATURE";
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, NotDescr>, ExistsDescr> existsBuilder = lhsBuilder.not().exists();
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareExistsConstraint(existsBuilder, patternType, kiePMMLOperatorValues);
        assertNotNull(existsBuilder.getDescr());
        final List<BaseDescr> descrs = existsBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(1, descrs.size());
        assertTrue(descrs.get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertEquals(patternType, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value < 35 || value > 85";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test
    public void declareConstraintIn() {
        List<Object> values = Arrays.asList("-5", "0.5", "1", "10");
        String patternType = "INPUT1";
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintIn(patternType, values);
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(1, descrs.size());
        assertTrue(descrs.get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertEquals(patternType, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value in (-5, 0.5, 1, 10)";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test
    public void declareConstraintNotIn() {
        List<Object> values = Arrays.asList("3", "8.5");
        String patternType = "INPUT2";
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintNotIn(patternType, values);
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(1, descrs.size());
        assertTrue(descrs.get(0) instanceof NotDescr);
        NotDescr notDescr = (NotDescr) descrs.get(0);
        assertEquals(1, notDescr.getDescrs().size());
        assertTrue(notDescr.getDescrs().get(0) instanceof PatternDescr);
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get(0);
        assertEquals(patternType, patternDescr.getObjectType());
        assertNull(patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertEquals(1, andDescr.getDescrs().size());
        assertTrue(andDescr.getDescrs().get(0) instanceof ExprConstraintDescr);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertFalse(exprConstraintDescr.isNegated());
        assertEquals(ExprConstraintDescr.Type.NAMED, exprConstraintDescr.getType());
        String expected = "value in (3, 8.5)";
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    @Test
    public void declareIfBreak() {
        String ifBreakField = "TEMPERATURE";
        String ifBreakOperator = "<";
        Object ifBreakValue = 24;
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareIfBreak(ifBreakField, ifBreakOperator, ifBreakValue);
        assertNotNull(lhsBuilder.getDescr());
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertNotNull(descrs);
        assertEquals(2, descrs.size());
        assertTrue(descrs.get(0) instanceof PatternDescr);
        assertTrue(descrs.get(1) instanceof ConditionalBranchDescr);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertEquals(ifBreakField, patternDescr.getObjectType());
        assertEquals(INPUT_FIELD, patternDescr.getIdentifier());
        assertTrue(patternDescr.getConstraint() instanceof AndDescr);
        ConditionalBranchDescr conditionalBranchDescr = (ConditionalBranchDescr) descrs.get(1);
        String expectedCondition = String.format(INPUT_FIELD_CONDITIONAL, ifBreakOperator, ifBreakValue);
        assertEquals(expectedCondition, conditionalBranchDescr.getCondition().getContent());
        assertTrue(conditionalBranchDescr.getConsequence().isBreaking());
        assertEquals(BREAK_LABEL, conditionalBranchDescr.getConsequence().getText());
    }

    @Test
    public void getInNotInConstraint() {
        List<Object> values = Arrays.asList("-5", "0.5", "1", "10");
        String retrieved =  KiePMMLDescrLhsFactory.factory(lhsBuilder).getInNotInConstraint(values);
        String expected = "value in (-5, 0.5, 1, 10)";
        assertEquals(expected, retrieved);
    }
}