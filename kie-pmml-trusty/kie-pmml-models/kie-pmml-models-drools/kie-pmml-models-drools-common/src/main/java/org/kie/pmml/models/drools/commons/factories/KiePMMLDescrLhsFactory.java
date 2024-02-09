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

import java.util.List;
import java.util.stream.Collectors;

import org.drools.drl.ast.dsl.CEDescrBuilder;
import org.drools.drl.ast.dsl.ConditionalBranchDescrBuilder;
import org.drools.drl.ast.dsl.PatternDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;
import static org.kie.pmml.models.drools.commons.factories.KiePMMLDescrRulesFactory.STATUS_HOLDER;

/**
 * Class used to generate the <b>lhs</b> of a rule (descr) out of a <b>KiePMMLDroolsRule</b>
 */
public class KiePMMLDescrLhsFactory {

    static final String INPUT_FIELD = "$inputField";
    static final String INPUT_FIELD_CONDITIONAL = "$inputField.getValue() %s %s";

    static final String VALUE_PATTERN = "value %s %s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrLhsFactory.class.getName());

    final CEDescrBuilder<RuleDescrBuilder, AndDescr> builder;

    private KiePMMLDescrLhsFactory(final CEDescrBuilder<RuleDescrBuilder, AndDescr> builder) {
        this.builder = builder;
    }

    public static KiePMMLDescrLhsFactory factory(final CEDescrBuilder<RuleDescrBuilder, AndDescr> builder) {
        return new KiePMMLDescrLhsFactory(builder);
    }

    public void declareLhs(final KiePMMLDroolsRule rule) {
        logger.trace("declareLhs {}", rule);
        final PatternDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>> patternDescrBuilder = builder.pattern(KiePMMLStatusHolder.class.getSimpleName()).id(STATUS_HOLDER, false);
        if (rule.getStatusConstraint() != null) {
            patternDescrBuilder.constraint(rule.getStatusConstraint());
        }
        if (rule.getAndConstraints() != null) {
            declareConstraintsAndOr(rule.getAndConstraints(), builder.and());
        }
        if (rule.getOrConstraints() != null) {
            declareConstraintsAndOr(rule.getOrConstraints(), builder.or());
        }
        if (rule.getXorConstraints() != null) {
            declareConstraintsXor(rule.getXorConstraints());
        }
        if (rule.getNotConstraints() != null) {
            declareNotConstraints(rule.getNotConstraints());
        }
        if (rule.getInConstraints() != null) {
            rule.getInConstraints().forEach(this::declareConstraintIn);
        }
        if (rule.getNotInConstraints() != null) {
            rule.getNotInConstraints().forEach(this::declareConstraintNotIn);
        }
        if (rule.getIfBreakField() != null) {
            declareIfBreak(rule.getIfBreakField(), rule.getIfBreakOperator(), rule.getIfBreakValue());
        }
    }

    protected void declareConstraintsAndOr(final List<KiePMMLFieldOperatorValue> orConstraints, final CEDescrBuilder<?, ?> andOrBuilder) {
        for (KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue : orConstraints) {
            if (kiePMMLFieldOperatorValue.getName() != null) {
                commonDeclarePatternWithConstraint(andOrBuilder, kiePMMLFieldOperatorValue.getName(), kiePMMLFieldOperatorValue.getConstraintsAsString());
            }
            if (kiePMMLFieldOperatorValue.getNestedKiePMMLFieldOperatorValues() != null) {
                switch (kiePMMLFieldOperatorValue.getOperator()) {
                    case OR:
                        declareConstraintsAndOr(kiePMMLFieldOperatorValue.getNestedKiePMMLFieldOperatorValues(), andOrBuilder.or());
                        break;
                    case AND:
                        declareConstraintsAndOr(kiePMMLFieldOperatorValue.getNestedKiePMMLFieldOperatorValues(), andOrBuilder.and());
                        break;
                    default:
                        throw new KiePMMLException(String.format("Operator %s not managed inside declareConstraintsAndOr, yet", kiePMMLFieldOperatorValue.getOperator()));
                }
            }
        }
    }

    protected void declareConstraintsXor(final List<KiePMMLFieldOperatorValue> xorConstraints) {
        if (xorConstraints.size() != 2) {
            throw new KiePMMLException("Expecting two fields for XOR constraints, retrieved " + xorConstraints.size());
        }
        final String[] keys = new String[xorConstraints.size()];
        final String[] values = new String[xorConstraints.size()];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = xorConstraints.get(i).getName();
            values[i] = xorConstraints.get(i).getConstraintsAsString();
        }
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = builder.and();
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, NotDescr>, AndDescr> notBuilder = andBuilder.not().and();
        commonDeclarePatternWithConstraint(notBuilder, keys[0], values[0]);
        commonDeclarePatternWithConstraint(notBuilder, keys[1], values[1]);
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, ExistsDescr>, OrDescr> existsBuilder = andBuilder.exists().or();
        commonDeclarePatternWithConstraint(existsBuilder, keys[0], values[0]);
        commonDeclarePatternWithConstraint(existsBuilder.or(), keys[1], values[1]);
    }

    protected void declareNotConstraints(final List<KiePMMLFieldOperatorValue> notConstraints) {
        // The builder to put in "and" the not constraints
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = builder.and();
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, NotDescr>, AndDescr> notBuilder = andBuilder.not().and();
        notConstraints.forEach(kiePMMLOperatorValue -> commonDeclarePatternWithConstraint(notBuilder, kiePMMLOperatorValue.getName(), kiePMMLOperatorValue.getConstraintsAsString()));
    }

    protected void commonDeclarePatternWithConstraint(final CEDescrBuilder<?, ?> descrBuilder, final String patternType, final String constraintString) {
        descrBuilder.pattern(patternType).constraint(constraintString);
    }

    protected void declareConstraintIn(final String patternType, final List<Object> values) {
        String constraints = getInNotInConstraint(values);
        builder.pattern(patternType).constraint(constraints);
    }

    protected void declareConstraintNotIn(final String patternType, final List<Object> values) {
        String constraints = getInNotInConstraint(values);
        builder.not().pattern(patternType).constraint(constraints);
    }

    protected void declareIfBreak(String ifBreakField, String ifBreakOperator, Object ifBreakValue) {
        builder.pattern(ifBreakField).id(INPUT_FIELD, false);
        final ConditionalBranchDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>> condBranchBuilder = builder.conditionalBranch();
        condBranchBuilder.condition().constraint(String.format(INPUT_FIELD_CONDITIONAL, ifBreakOperator, ifBreakValue));
        condBranchBuilder.consequence().breaking(true).name(BREAK_LABEL);
    }

    protected String getInNotInConstraint(final List<Object> values) {
        String expressionString = values.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ", "(", ")"));
        return String.format(VALUE_PATTERN, "in", expressionString);
    }
}
