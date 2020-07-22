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
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.ToIntQuadFunction;
import org.optaplanner.core.api.function.ToLongQuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.common.consequences.ConstraintConsequence;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.ConstraintGraphNode;

final class QuadRuleAssembler extends AbstractRuleAssembler<QuadPredicate> {

    private QuadPredicate filterToApplyToLastPrimaryPattern = null;

    public QuadRuleAssembler(UnaryOperator<String> idSupplier, int expectedGroupByCount,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        super(idSupplier, expectedGroupByCount, finishedExpressions, variables, primaryPatterns, dependentExpressionMap);
    }

    @Override
    protected void addFilterToLastPrimaryPattern(QuadPredicate quadPredicate) {
        if (filterToApplyToLastPrimaryPattern == null) {
            filterToApplyToLastPrimaryPattern = quadPredicate;
        } else {
            filterToApplyToLastPrimaryPattern = filterToApplyToLastPrimaryPattern.and(quadPredicate);
        }
    }

    @Override
    protected AbstractRuleAssembler join(UniRuleAssembler ruleAssembler, ConstraintGraphNode joinNode) {
        throw new UnsupportedOperationException("Impossible state: Penta streams are not supported.");
    }

    @Override
    protected AbstractRuleAssembler andThenExists(AbstractConstraintModelJoiningNode joiningNode, boolean shouldExist) {
        return new QuadExistenceMutator(joiningNode, shouldExist).apply(this);
    }

    @Override
    protected AbstractGroupByMutator new0Map1CollectGroupByMutator(Object collector) {
        return new QuadGroupBy0Map1CollectMutator<>((QuadConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new1Map0CollectGroupByMutator(Object mapping) {
        return new QuadGroupBy1Map0CollectMutator<>((QuadFunction) mapping);
    }

    @Override
    protected AbstractGroupByMutator new1Map1CollectGroupByMutator(Object mapping, Object collector) {
        return new QuadGroupBy1Map1CollectMutator<>((QuadFunction) mapping, (QuadConstraintCollector) collector);
    }

    @Override
    protected AbstractGroupByMutator new2Map0CollectGroupByMutator(Object mappingA, Object mappingB) {
        return new QuadGroupBy2Map0CollectMutator<>((QuadFunction) mappingA, (QuadFunction) mappingB);
    }

    @Override
    protected AbstractGroupByMutator new2Map1CollectGroupByMutator(Object mappingA, Object mappingB,
            Object collectorC) {
        return new QuadGroupBy2Map1CollectMutator<>((QuadFunction) mappingA, (QuadFunction) mappingB,
                (QuadConstraintCollector) collectorC);
    }

    @Override
    protected AbstractGroupByMutator new2Map2CollectGroupByMutator(Object mappingA, Object mappingB, Object collectorC,
            Object collectorD) {
        return new QuadGroupBy2Map2CollectMutator<>((QuadFunction) mappingA, (QuadFunction) mappingB,
                (QuadConstraintCollector) collectorC, (QuadConstraintCollector) collectorD);
    }

    @Override
    protected ConsequenceBuilder.ValidBuilder buildConsequence(DroolsConstraint constraint,
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal, Variable... variables) {
        ConstraintConsequence consequence = constraint.getConsequence();
        switch (consequence.getMatchWeightType()) {
            case INTEGER:
                ToIntQuadFunction intMatchWeighter = ((Supplier<ToIntQuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, intMatchWeighter.applyAsInt(a, b, c, d)));
            case LONG:
                ToLongQuadFunction longMatchWeighter = ((Supplier<ToLongQuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, longMatchWeighter.applyAsLong(a, b, c, d)));
            case BIG_DECIMAL:
                QuadFunction bigDecimalMatchWeighter = ((Supplier<QuadFunction>) consequence).get();
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore(constraint, (Drools) drools,
                                (AbstractScoreHolder) scoreHolder, (BigDecimal) bigDecimalMatchWeighter.apply(a, b, c, d)));
            case DEFAULT:
                return DSL.on(scoreHolderGlobal, variables[0], variables[1], variables[2], variables[3])
                        .execute((drools, scoreHolder, a, b, c, d) -> impactScore((Drools) drools,
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
        QuadPredicate predicate = filterToApplyToLastPrimaryPattern;
        getLastPrimaryPattern()
                .expr("Filter using " + predicate, getVariable(0), getVariable(1), getVariable(2), getVariable(3),
                        (fact, a, b, c, d) -> predicate.test(a, b, c, d));
        filterToApplyToLastPrimaryPattern = null;
    }

}
