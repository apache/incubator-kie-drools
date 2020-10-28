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
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;

final class BiRuleAssembler extends AbstractRuleAssembler<BiPredicate> {

    private BiPredicate filterToApplyToLastPrimaryPattern = null;

    public BiRuleAssembler(DroolsVariableFactory variableFactory, int expectedGroupByCount,
            List<ViewItem> finishedExpressions, Variable aVariable, Variable bVariable, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        super(variableFactory, expectedGroupByCount, finishedExpressions, primaryPatterns, dependentExpressionMap,
                aVariable, bVariable);
    }

    @Override
    protected void addFilterToLastPrimaryPattern(BiPredicate biPredicate) {
        if (filterToApplyToLastPrimaryPattern == null) {
            filterToApplyToLastPrimaryPattern = biPredicate;
        } else {
            filterToApplyToLastPrimaryPattern = filterToApplyToLastPrimaryPattern.and(biPredicate);
        }
    }

    @Override
    protected AbstractRuleAssembler join(UniRuleAssembler ruleAssembler, ConstraintGraphNode joinNode) {
        return new TriJoinMutator<>((AbstractConstraintModelJoiningNode) joinNode)
                .apply(this, ruleAssembler);
    }

    @Override
    protected AbstractRuleAssembler andThenExists(AbstractConstraintModelJoiningNode joiningNode, boolean shouldExist) {
        return new BiExistenceMutator(joiningNode, shouldExist).apply(this);
    }

    @Override
    protected AbstractGroupByMutator new0Map1CollectGroupByMutator(Object collector) {
        return new BiGroupBy0Map1CollectMutator<>((BiConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new1Map0CollectGroupByMutator(Object mapping) {
        if (getExpectedGroupByCount() == 1) {
            return new BiGroupBy1Map0CollectFastMutator<>((BiFunction) mapping);
        } else {
            return new BiGroupBy1Map0CollectMutator<>((BiFunction) mapping);
        }
    }

    @Override
    protected AbstractGroupByMutator new1Map1CollectGroupByMutator(Object mapping, Object collector) {
        if (getExpectedGroupByCount() == 1) {
            return new BiGroupBy1Map1CollectFastMutator<>((BiFunction) mapping, (BiConstraintCollector) collector);
        } else {
            return new BiGroupBy1Map1CollectMutator<>((BiFunction) mapping, (BiConstraintCollector) collector);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map0CollectGroupByMutator(Object mappingA, Object mappingB) {
        if (getExpectedGroupByCount() == 1) {
            return new BiGroupBy2Map0CollectFastMutator<>((BiFunction) mappingA, (BiFunction) mappingB);
        } else {
            return new BiGroupBy2Map0CollectMutator<>((BiFunction) mappingA, (BiFunction) mappingB);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map1CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC) {
        if (getExpectedGroupByCount() == 1) {
            return new BiGroupBy2Map1CollectFastMutator<>((BiFunction) mappingA, (BiFunction) mappingB,
                    (BiConstraintCollector) collectorC);
        } else {
            return new BiGroupBy2Map1CollectMutator<>((BiFunction) mappingA, (BiFunction) mappingB,
                    (BiConstraintCollector) collectorC);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map2CollectGroupByMutator(Object mappingA, Object mappingB, Object collectorC,
            Object collectorD) {
        if (getExpectedGroupByCount() == 1) {
            return new BiGroupBy2Map2CollectFastMutator<>((BiFunction) mappingA, (BiFunction) mappingB,
                    (BiConstraintCollector) collectorC, (BiConstraintCollector) collectorD);
        } else {
            return new BiGroupBy2Map2CollectMutator<>((BiFunction) mappingA, (BiFunction) mappingB,
                    (BiConstraintCollector) collectorC, (BiConstraintCollector) collectorD);
        }
    }

    @Override
    protected ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable... variables) {
        ConstraintConsequence consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntBiFunction intMatchWeighter = ((Supplier<ToIntBiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, intMatchWeighter.applyAsInt(a, b)));
            case LONG:
                ToLongBiFunction longMatchWeighter = ((Supplier<ToLongBiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, longMatchWeighter.applyAsLong(a, b)));
            case BIG_DECIMAL:
                BiFunction bigDecimalMatchWeighter = ((Supplier<BiFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, (BigDecimal) bigDecimalMatchWeighter.apply(a, b)));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0], variables[1])
                        .execute((drools, scoreHolder, a, b) -> impactScore((Drools) drools,
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
        BiPredicate predicate = filterToApplyToLastPrimaryPattern;
        getLastPrimaryPattern()
                .expr("Filter using " + predicate, getVariable(0), getVariable(1),
                        (fact, a, b) -> predicate.test(a, b));
        filterToApplyToLastPrimaryPattern = null;
    }

}
