/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.commons.factories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalBranchDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.dsl.CEDescrBuilder;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrLhsFactory.INPUT_FIELD;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrLhsFactory.INPUT_FIELD_CONDITIONAL;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRulesFactory.STATUS_HOLDER;

public class KiePMMLDescrLhsFactoryTest {

    private static final String CURRENT_RULE = "currentRule";
    private CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder;

    @BeforeEach
    public void setUp() throws Exception {
        PackageDescrBuilder builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        RuleDescrBuilder ruleBuilder = builder.newRule().name(CURRENT_RULE);
        lhsBuilder = ruleBuilder.lhs();
    }

    @Test
    void declareLhs() {
        String name = "NAME";
        String statusToSet = "STATUS_TO_SET";
        KiePMMLDroolsRule rule = KiePMMLDroolsRule.builder(name, statusToSet, Collections.emptyList()).build();
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareLhs(rule);
        assertThat(lhsBuilder.getDescr()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).hasSize(1);
        assertThat(lhsBuilder.getDescr().getDescrs().get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) lhsBuilder.getDescr().getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(KiePMMLStatusHolder.class.getSimpleName());
        assertThat(patternDescr.getIdentifier()).isEqualTo(STATUS_HOLDER);
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).isEmpty();
    }

    @Test
    void declareConstraintAnd() {
        String temperatureField = "TEMPERATURE";
        String humidityField = "HUMIDITY";
        List<KiePMMLFieldOperatorValue> kiePMMLOperatorValues =
                Arrays.asList(new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.AND,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                        new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null));
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsAndOr(kiePMMLOperatorValues, lhsBuilder.and());
        assertThat(lhsBuilder.getDescr()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).hasSize(1);
        assertThat(lhsBuilder.getDescr().getDescrs().get(0)).isInstanceOf(AndDescr.class);
        AndDescr baseAndDescr = (AndDescr) lhsBuilder.getDescr().getDescrs().get(0);
        final List<BaseDescr> descrs = baseAndDescr.getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(2);
        // First KiePMMLFieldOperatorValue
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value < 35";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        // Second KiePMMLFieldOperatorValue
        assertThat(descrs.get(1)).isInstanceOf(PatternDescr.class);
        patternDescr = (PatternDescr) descrs.get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo(humidityField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value > 85";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void declareNestedConstraintAnd() {
        String temperatureField = "TEMPERATURE";
        String humidityField = "HUMIDITY";
        final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues = Arrays
                .asList(new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 56)), null),
                        new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 91)), null));
        List<KiePMMLFieldOperatorValue> kiePMMLOperatorValues =
                Collections.singletonList(new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.AND,
                        Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), nestedKiePMMLFieldOperatorValues));
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsAndOr(kiePMMLOperatorValues, lhsBuilder.and());
        assertThat(lhsBuilder.getDescr()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).hasSize(1);
        assertThat(lhsBuilder.getDescr().getDescrs().get(0)).isInstanceOf(AndDescr.class);
        AndDescr baseAndDescr = (AndDescr) lhsBuilder.getDescr().getDescrs().get(0);
        final List<BaseDescr> descrs = baseAndDescr.getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(2);
        // First KiePMMLFieldOperatorValue
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value < 35";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        // Nested KiePMMLFieldOperatorValues
        assertThat(descrs.get(1)).isInstanceOf(AndDescr.class);
        AndDescr nestedAndDescr = (AndDescr) descrs.get(1);
        assertThat(nestedAndDescr.getDescrs()).hasSize(2);
        final List<BaseDescr> nestedDescrs = nestedAndDescr.getDescrs();
        // First nested KiePMMLFieldOperatorValue
        assertThat(nestedDescrs.get(0)).isInstanceOf(PatternDescr.class);
        patternDescr = (PatternDescr) nestedDescrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(humidityField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value < 56";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        // Second nested KiePMMLFieldOperatorValue
        assertThat(nestedDescrs.get(1)).isInstanceOf(PatternDescr.class);
        patternDescr = (PatternDescr) nestedDescrs.get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo(humidityField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value > 91";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void declareConstraintsOr() {
        String temperatureField = "TEMPERATURE";
        String humidityField = "HUMIDITY";
        List<KiePMMLFieldOperatorValue> kiePMMLOperatorValues =
                Arrays.asList(new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.AND,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                        new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR, Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null));
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsAndOr(kiePMMLOperatorValues, lhsBuilder.or());
        assertThat(lhsBuilder.getDescr()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).hasSize(1);
        assertThat(lhsBuilder.getDescr().getDescrs().get(0)).isInstanceOf(OrDescr.class);
        OrDescr baseOrDescr = (OrDescr) lhsBuilder.getDescr().getDescrs().get(0);
        final List<BaseDescr> descrs = baseOrDescr.getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(2);
        // First KiePMMLFieldOperatorValue
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value < 35";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        // Second KiePMMLFieldOperatorValue
        assertThat(descrs.get(1)).isInstanceOf(PatternDescr.class);
        patternDescr = (PatternDescr) descrs.get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo(humidityField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value > 85";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void declareConstraintsXorWrongInput() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            String temperatureField = "TEMPERATURE";
            String humidityField = "HUMIDITY";
            final List<KiePMMLFieldOperatorValue> xorConstraints = Arrays
                    .asList(new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.OR,
                                    Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                            new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.OR,
                                    Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null),
                            new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR,
                                    Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 56)), null),
                            new KiePMMLFieldOperatorValue(humidityField, BOOLEAN_OPERATOR.OR,
                                    Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 91)), null));
            KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsXor(xorConstraints);
        });
    }

    @Test
    void declareConstraintsXor() {
        String temperatureField = "TEMPERATURE";
        final List<KiePMMLFieldOperatorValue> xorConstraints = Arrays
                .asList(new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.OR,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.LESS_THAN, 35)), null),
                        new KiePMMLFieldOperatorValue(temperatureField, BOOLEAN_OPERATOR.OR,
                                Collections.singletonList(new KiePMMLOperatorValue(OPERATOR.GREATER_THAN, 85)), null));
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintsXor(xorConstraints);
        assertThat(lhsBuilder.getDescr()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).isNotNull();
        assertThat(lhsBuilder.getDescr().getDescrs()).hasSize(1);
        assertThat(lhsBuilder.getDescr().getDescrs().get(0)).isInstanceOf(AndDescr.class);
        AndDescr rootAndDescr = (AndDescr) lhsBuilder.getDescr().getDescrs().get(0);
        assertThat(rootAndDescr.getDescrs()).hasSize(2);
        assertThat(rootAndDescr.getDescrs().get(0)).isInstanceOf(NotDescr.class);
        assertThat(rootAndDescr.getDescrs().get(1)).isInstanceOf(ExistsDescr.class);
        // "Not" construct
        NotDescr notDescr = (NotDescr) rootAndDescr.getDescrs().get(0);
        assertThat(notDescr.getDescrs()).hasSize(1);
        assertThat(notDescr.getDescrs().get(0)).isInstanceOf(AndDescr.class);
        AndDescr notAndDescr = (AndDescr) notDescr.getDescrs().get(0);
        assertThat(notAndDescr.getDescrs().get(0)).isInstanceOf(PatternDescr.class);
        assertThat(notAndDescr.getDescrs().get(1)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) notAndDescr.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value < 35";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        patternDescr = (PatternDescr) notAndDescr.getDescrs().get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value > 85";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        // "Exists" construct
        ExistsDescr existsDescr = (ExistsDescr) rootAndDescr.getDescrs().get(1);
        assertThat(existsDescr.getDescrs()).hasSize(1);
        assertThat(existsDescr.getDescrs().get(0)).isInstanceOf(OrDescr.class);
        OrDescr existsOrDescr = (OrDescr) existsDescr.getDescrs().get(0);
        assertThat(existsOrDescr.getDescrs()).hasSize(2);
        assertThat(existsOrDescr.getDescrs().get(0)).isInstanceOf(PatternDescr.class);
        assertThat(existsOrDescr.getDescrs().get(1)).isInstanceOf(OrDescr.class);
        patternDescr = (PatternDescr) existsOrDescr.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value < 35";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
        OrDescr nestedOrDescr = (OrDescr) existsOrDescr.getDescrs().get(1);
        assertThat(nestedOrDescr.getDescrs()).hasSize(1);
        assertThat(nestedOrDescr.getDescrs().get(0)).isInstanceOf(PatternDescr.class);
        patternDescr = (PatternDescr) nestedOrDescr.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(temperatureField);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        expected = "value > 85";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void commonDeclarePatternWithConstraint() {
        String patternType = "TEMPERATURE";
        String constraintsString = "value < 35";
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, NotDescr>, ExistsDescr> existsBuilder = lhsBuilder.not().exists();
        KiePMMLDescrLhsFactory.factory(lhsBuilder).commonDeclarePatternWithConstraint(existsBuilder, patternType,
                constraintsString);
        assertThat(existsBuilder.getDescr()).isNotNull();
        final List<BaseDescr> descrs = existsBuilder.getDescr().getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(1);
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(patternType);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(constraintsString);
    }

    @Test
    void declareConstraintIn() {
        List<Object> values = Arrays.asList("-5", "0.5", "1", "10");
        String patternType = "INPUT1";
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintIn(patternType, values);
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(1);
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(patternType);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value in (-5, 0.5, 1, 10)";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void declareConstraintNotIn() {
        List<Object> values = Arrays.asList("3", "8.5");
        String patternType = "INPUT2";
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareConstraintNotIn(patternType, values);
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(1);
        assertThat(descrs.get(0)).isInstanceOf(NotDescr.class);
        NotDescr notDescr = (NotDescr) descrs.get(0);
        assertThat(notDescr.getDescrs()).hasSize(1);
        assertThat(notDescr.getDescrs().get(0)).isInstanceOf(PatternDescr.class);
        PatternDescr patternDescr = (PatternDescr) notDescr.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(patternType);
        assertThat(patternDescr.getIdentifier()).isNull();
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        AndDescr andDescr = (AndDescr) patternDescr.getConstraint();
        assertThat(andDescr.getDescrs()).hasSize(1);
        assertThat(andDescr.getDescrs().get(0)).isInstanceOf(ExprConstraintDescr.class);
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) andDescr.getDescrs().get(0);
        assertThat(exprConstraintDescr.isNegated()).isFalse();
        assertThat(exprConstraintDescr.getType()).isEqualTo(ExprConstraintDescr.Type.NAMED);
        String expected = "value in (3, 8.5)";
        assertThat(exprConstraintDescr.getExpression()).isEqualTo(expected);
    }

    @Test
    void declareIfBreak() {
        String ifBreakField = "TEMPERATURE";
        String ifBreakOperator = "<";
        Object ifBreakValue = 24;
        KiePMMLDescrLhsFactory.factory(lhsBuilder).declareIfBreak(ifBreakField, ifBreakOperator, ifBreakValue);
        assertThat(lhsBuilder.getDescr()).isNotNull();
        final List<BaseDescr> descrs = lhsBuilder.getDescr().getDescrs();
        assertThat(descrs).isNotNull();
        assertThat(descrs).hasSize(2);
        assertThat(descrs.get(0)).isInstanceOf(PatternDescr.class);
        assertThat(descrs.get(1)).isInstanceOf(ConditionalBranchDescr.class);
        PatternDescr patternDescr = (PatternDescr) descrs.get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo(ifBreakField);
        assertThat(patternDescr.getIdentifier()).isEqualTo(INPUT_FIELD);
        assertThat(patternDescr.getConstraint()).isInstanceOf(AndDescr.class);
        ConditionalBranchDescr conditionalBranchDescr = (ConditionalBranchDescr) descrs.get(1);
        String expectedCondition = String.format(INPUT_FIELD_CONDITIONAL, ifBreakOperator, ifBreakValue);
        assertThat(conditionalBranchDescr.getCondition().getContent()).isEqualTo(expectedCondition);
        assertThat(conditionalBranchDescr.getConsequence().isBreaking()).isTrue();
        assertThat(conditionalBranchDescr.getConsequence().getText()).isEqualTo(BREAK_LABEL);
    }

    @Test
    void getInNotInConstraint() {
        List<Object> values = Arrays.asList("-5", "0.5", "1", "10");
        String retrieved = KiePMMLDescrLhsFactory.factory(lhsBuilder).getInNotInConstraint(values);
        String expected = "value in (-5, 0.5, 1, 10)";
        assertThat(retrieved).isEqualTo(expected);
    }
}