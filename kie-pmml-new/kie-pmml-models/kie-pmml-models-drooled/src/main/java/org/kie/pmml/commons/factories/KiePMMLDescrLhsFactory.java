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

import java.util.List;
import java.util.Map;

import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.ConditionalBranchDescrBuilder;
import org.drools.compiler.lang.api.PatternDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.executor.KiePMMLStatusHolder;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.BREAK_LABEL;
import static org.kie.pmml.commons.factories.KiePMMLDescrRulesFactory.STATUS_HOLDER;

/**
 * Class used to generate <b>Rules</b> (descr) out of a <b>Queue&lt;KiePMMLDrooledRule&gt;</b>
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

    public void declareLhs(final KiePMMLDrooledRule rule) {
        logger.debug("declareLhs {}", rule);
        final PatternDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>> patternDescrBuilder = builder.pattern(KiePMMLStatusHolder.class.getSimpleName()).id(STATUS_HOLDER, false);
        if (rule.getStatusConstraint() != null) {
            patternDescrBuilder.constraint(rule.getStatusConstraint());
        }
        if (rule.getAndConstraints() != null) {
            rule.getAndConstraints().forEach((type, kiePMMLOperatorValues) -> declareConstraintAndOr("&&", type, kiePMMLOperatorValues));
        }
        if (rule.getOrConstraints() != null) {
            rule.getOrConstraints().forEach((type, kiePMMLOperatorValues) -> declareConstraintAndOr("||", type, kiePMMLOperatorValues));
        }
        if (rule.getXorConstraints() != null) {
            declareConstraintsXor(rule.getXorConstraints());
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

    protected void declareConstraintAndOr(final String operator, final String patternType, final List<KiePMMLOperatorValue> kiePMMLOperatorValues) {
        StringBuilder constraintBuilder = new StringBuilder();
        for (int i = 0; i < kiePMMLOperatorValues.size(); i++) {
            KiePMMLOperatorValue kiePMMLOperatorValue = kiePMMLOperatorValues.get(i);
            if (i > 0) {
                constraintBuilder.append(" ");
                constraintBuilder.append(operator);
                constraintBuilder.append(" ");
            }
            constraintBuilder.append(String.format(VALUE_PATTERN, kiePMMLOperatorValue.getOperator(), kiePMMLOperatorValue.getValue()));
        }
        builder.pattern(patternType).constraint(constraintBuilder.toString());
    }

    protected void declareConstraintsXor(final Map<String, List<KiePMMLOperatorValue>> xorConstraints) {
        if (xorConstraints.size() != 2) {
            throw new KiePMMLException("Expecting two fields for XOR constraints, retrieved " + xorConstraints.size());
        }
        final String[] keys = xorConstraints.keySet().toArray(new String[0]);
        final List<KiePMMLOperatorValue>[] values = new List[xorConstraints.size()];
        for (int i = 0; i < keys.length; i++) {
            values[i] = xorConstraints.get(keys[i]);
        }
        // The builder to put in "and" the not and the exists constraints
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = builder.and();
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, NotDescr>, AndDescr> notBuilder = andBuilder.not().and();
        declareNotConstraint(notBuilder, keys[0], values[0]);
        declareNotConstraint(notBuilder, keys[1], values[1]);
        final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, ExistsDescr>, OrDescr> existsBuilder = andBuilder.exists().or();
        declareExistsConstraint(existsBuilder, keys[0], values[0]);
        declareExistsConstraint(existsBuilder.or(), keys[1], values[1]);
    }

    protected void declareNotConstraint(final CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr>, NotDescr>, AndDescr> notBuilder, final String patternType, final List<KiePMMLOperatorValue> kiePMMLOperatorValues) {
        StringBuilder constraintBuilder = new StringBuilder();
        for (int i = 0; i < kiePMMLOperatorValues.size(); i++) {
            KiePMMLOperatorValue kiePMMLOperatorValue = kiePMMLOperatorValues.get(i);
            if (i > 0) {
                constraintBuilder.append(" && ");
            }
            constraintBuilder.append(String.format(VALUE_PATTERN, kiePMMLOperatorValue.getOperator(), kiePMMLOperatorValue.getValue()));
        }
        notBuilder.pattern(patternType).constraint(constraintBuilder.toString());
    }

    protected void declareExistsConstraint(final CEDescrBuilder<?, ?> existsBuilder, final String patternType, final List<KiePMMLOperatorValue> kiePMMLOperatorValues) {
        StringBuilder constraintBuilder = new StringBuilder();
        for (int i = 0; i < kiePMMLOperatorValues.size(); i++) {
            KiePMMLOperatorValue kiePMMLOperatorValue = kiePMMLOperatorValues.get(i);
            if (i > 0) {
                constraintBuilder.append(" || ");
            }
            constraintBuilder.append(String.format(VALUE_PATTERN, kiePMMLOperatorValue.getOperator(), kiePMMLOperatorValue.getValue()));
        }
        existsBuilder.pattern(patternType).constraint(constraintBuilder.toString());
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
        StringBuilder constraintBuilder = new StringBuilder();
        constraintBuilder.append("(");
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            if (i > 0) {
                constraintBuilder.append(", ");
            }
            constraintBuilder.append(value);
        }
        constraintBuilder.append(")");
        return String.format(VALUE_PATTERN, "in", constraintBuilder.toString());
    }
}
