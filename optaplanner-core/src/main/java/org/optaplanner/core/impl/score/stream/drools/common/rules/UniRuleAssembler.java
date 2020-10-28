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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsVariableFactory;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;

final class UniRuleAssembler extends AbstractRuleAssembler<Predicate> {

    private Predicate filterToApplyToLastPrimaryPattern = null;

    public UniRuleAssembler(DroolsVariableFactory variableFactory, ConstraintGraphNode previousNode,
            int expectedGroupByCount) {
        super(variableFactory, previousNode, expectedGroupByCount);
    }

    public UniRuleAssembler(DroolsVariableFactory variableFactory, int expectedGroupByCount,
            List<ViewItem> finishedExpressions, Variable aVariable, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        super(variableFactory, expectedGroupByCount, finishedExpressions, primaryPatterns, dependentExpressionMap,
                aVariable);
    }

    @Override
    protected void addFilterToLastPrimaryPattern(Predicate predicate) {
        if (filterToApplyToLastPrimaryPattern == null) {
            filterToApplyToLastPrimaryPattern = predicate;
        } else {
            filterToApplyToLastPrimaryPattern = filterToApplyToLastPrimaryPattern.and(predicate);
        }
    }

    @Override
    protected AbstractRuleAssembler join(UniRuleAssembler ruleAssembler, ConstraintGraphNode joinNode) {
        return new BiJoinMutator<>((AbstractConstraintModelJoiningNode) joinNode)
                .apply(this, ruleAssembler);
    }

    @Override
    protected AbstractRuleAssembler andThenExists(AbstractConstraintModelJoiningNode joiningNode, boolean shouldExist) {
        return new UniExistenceMutator(joiningNode, shouldExist).apply(this);
    }

    @Override
    protected AbstractGroupByMutator new0Map1CollectGroupByMutator(Object collector) {
        return new UniGroupBy0Map1CollectMutator<>((UniConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new1Map0CollectGroupByMutator(Object mapping) {
        if (getExpectedGroupByCount() == 1) {
            return new UniGroupBy1Map0CollectFastMutator<>((Function) mapping);
        } else {
            return new UniGroupBy1Map0CollectMutator<>((Function) mapping);
        }
    }

    @Override
    protected AbstractGroupByMutator new1Map1CollectGroupByMutator(Object mapping, Object collector) {
        if (getExpectedGroupByCount() == 1) {
            return new UniGroupBy1Map1CollectFastMutator<>((Function) mapping, (UniConstraintCollector) collector);
        } else {
            return new UniGroupBy1Map1CollectMutator<>((Function) mapping, (UniConstraintCollector) collector);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map0CollectGroupByMutator(Object mappingA, Object mappingB) {
        if (getExpectedGroupByCount() == 1) {
            return new UniGroupBy2Map0CollectFastMutator<>((Function) mappingA, (Function) mappingB);
        } else {
            return new UniGroupBy2Map0CollectMutator<>((Function) mappingA, (Function) mappingB);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map1CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC) {
        if (getExpectedGroupByCount() == 1) {
            return new UniGroupBy2Map1CollectFastMutator<>((Function) mappingA, (Function) mappingB,
                    (UniConstraintCollector) collectorC);
        } else {
            return new UniGroupBy2Map1CollectMutator<>((Function) mappingA, (Function) mappingB,
                    (UniConstraintCollector) collectorC);
        }
    }

    @Override
    protected AbstractGroupByMutator new2Map2CollectGroupByMutator(Object mappingA, Object mappingB, Object collectorC,
            Object collectorD) {
        if (getExpectedGroupByCount() == 1) {
            return new UniGroupBy2Map2CollectFastMutator<>((Function) mappingA, (Function) mappingB,
                    (UniConstraintCollector) collectorC, (UniConstraintCollector) collectorD);
        } else {
            return new UniGroupBy2Map2CollectMutator<>((Function) mappingA, (Function) mappingB,
                    (UniConstraintCollector) collectorC, (UniConstraintCollector) collectorD);
        }
    }

    @Override
    protected ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable... variables) {
        ConstraintConsequence consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntFunction intMatchWeighter = ((Supplier<ToIntFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, intMatchWeighter.applyAsInt(a)));
            case LONG:
                ToLongFunction longMatchWeighter = ((Supplier<ToLongFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, longMatchWeighter.applyAsLong(a)));
            case BIG_DECIMAL:
                Function bigDecimalMatchWeighter = ((Supplier<Function>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, (BigDecimal) bigDecimalMatchWeighter.apply(a)));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0])
                        .execute((drools, scoreHolder, a) -> impactScore((Drools) drools,
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
        Predicate predicate = filterToApplyToLastPrimaryPattern;
        getLastPrimaryPattern()
                .expr("Filter using " + predicate, getVariable(0), (fact, a) -> predicate.test(a));
        filterToApplyToLastPrimaryPattern = null;
    }

}
