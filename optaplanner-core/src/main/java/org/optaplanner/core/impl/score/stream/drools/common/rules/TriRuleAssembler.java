/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;

final class TriRuleAssembler extends AbstractRuleAssembler<TriPredicate> {

    private TriPredicate filterToApplyToLastPrimaryPattern = null;

    public TriRuleAssembler(UnaryOperator<String> idSupplier, int expectedGroupByCount,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        super(idSupplier, expectedGroupByCount, finishedExpressions, variables, primaryPatterns, dependentExpressionMap);
    }

    @Override
    protected void addFilterToLastPrimaryPattern(TriPredicate triPredicate) {
        if (filterToApplyToLastPrimaryPattern == null) {
            filterToApplyToLastPrimaryPattern = triPredicate;
        } else {
            filterToApplyToLastPrimaryPattern = filterToApplyToLastPrimaryPattern.and(triPredicate);
        }
    }

    @Override
    protected AbstractRuleAssembler join(UniRuleAssembler ruleAssembler, ConstraintGraphNode joinNode) {
        return new QuadJoinMutator<>((AbstractConstraintModelJoiningNode) joinNode)
                .apply(this, ruleAssembler);
    }

    @Override
    protected AbstractRuleAssembler andThenExists(AbstractConstraintModelJoiningNode joiningNode, boolean shouldExist) {
        return new TriExistenceMutator(joiningNode, shouldExist).apply(this);
    }

    @Override
    protected AbstractGroupByMutator new0Map1CollectGroupByMutator(Object collector) {
        return new TriGroupBy0Map1CollectMutator<>((TriConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new1Map0CollectGroupByMutator(Object mapping) {
        return new TriGroupBy1Map0CollectMutator<>((TriFunction) mapping);
    }

    @Override
    protected AbstractGroupByMutator new1Map1CollectGroupByMutator(Object mapping, Object collector) {
        return new TriGroupBy1Map1CollectMutator<>((TriFunction) mapping, (TriConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new2Map0CollectGroupByMutator(Object mappingA, Object mappingB) {
        return new TriGroupBy2Map0CollectMutator<>((TriFunction) mappingA, (TriFunction) mappingB);
    }

    @Override
    protected AbstractGroupByMutator new2Map1CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC) {
        return new TriGroupBy2Map1CollectMutator<>((TriFunction) mappingA, (TriFunction) mappingB,
                (TriConstraintCollector) collectorC);
    }

    @Override
    protected AbstractGroupByMutator new2Map2CollectGroupByMutator(Object mappingA, Object mappingB, Object collectorC,
            Object collectorD) {
        return new TriGroupBy2Map2CollectMutator<>((TriFunction) mappingA, (TriFunction) mappingB,
                (TriConstraintCollector) collectorC, (TriConstraintCollector) collectorD);
    }

    @Override
    protected ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable... variables) {
        ConstraintConsequence consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntTriFunction intMatchWeighter = ((Supplier<ToIntTriFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2])
                        .execute((drools, scoreHolder, a, b, c) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, intMatchWeighter.applyAsInt(a, b, c)));
            case LONG:
                ToLongTriFunction longMatchWeighter = ((Supplier<ToLongTriFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2])
                        .execute((drools, scoreHolder, a, b, c) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, longMatchWeighter.applyAsLong(a, b, c)));
            case BIG_DECIMAL:
                TriFunction bigDecimalMatchWeighter = ((Supplier<TriFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2])
                        .execute((drools, scoreHolder, a, b, c) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, (BigDecimal) bigDecimalMatchWeighter.apply(a, b, c)));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2])
                        .execute((drools, scoreHolder, a, b, c) -> impactScore((Drools) drools,
                                (AbstractScoreHolder) scoreHolder));
            default:
                throw new UnsupportedOperationException(consequence.getMatchWeightType().toString());
        }
    }

    @Override
    protected void applyFilterToLastPrimaryPattern() {
        if (filterToApplyToLastPrimaryPattern == null) {
            return;
        }
        TriPredicate predicate = filterToApplyToLastPrimaryPattern;
        getLastPrimaryPattern()
                .expr("Filter using " + predicate, getVariable(0), getVariable(1), getVariable(2),
                        (fact, a, b, c) -> predicate.test(a, b, c));
        filterToApplyToLastPrimaryPattern = null;
    }

}
