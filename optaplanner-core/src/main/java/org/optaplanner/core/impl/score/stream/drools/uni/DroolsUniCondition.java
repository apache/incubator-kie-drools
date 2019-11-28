/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.model.BetaIndex;
import org.drools.model.DSL;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ExprViewItem;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAccumulateContext;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsPatternBuilder;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.from;
import static org.drools.model.PatternDSL.pattern;

public final class DroolsUniCondition<A> extends DroolsCondition<DroolsUniRuleStructure<A>> {

    public DroolsUniCondition(Class<A> aVariableType, LongSupplier variableIdSupplier) {
        this(new DroolsUniRuleStructure<>(aVariableType, variableIdSupplier));
    }

    public DroolsUniCondition(DroolsUniRuleStructure<A> ruleStructure) {
        super(ruleStructure);
    }

    public static Index.ConstraintType getConstraintType(JoinerType type) {
        switch (type) {
            case EQUAL:
                return Index.ConstraintType.EQUAL;
            case LESS_THAN:
                return Index.ConstraintType.LESS_THAN;
            case LESS_THAN_OR_EQUAL:
                return Index.ConstraintType.LESS_OR_EQUAL;
            case GREATER_THAN:
                return Index.ConstraintType.GREATER_THAN;
            case GREATER_THAN_OR_EQUAL:
                return Index.ConstraintType.GREATER_OR_EQUAL;
            default:
                throw new IllegalStateException("Unsupported joiner type (" + type + ").");
        }
    }

    public DroolsUniCondition<A> andFilter(Predicate<A> predicate) {
        Predicate1<Object> filter = a -> predicate.test((A) a);
        DroolsPatternBuilder<Object> patternWithFilter = ruleStructure.getPrimaryPattern()
                .expand(p -> p.expr("Filter using " + predicate, filter,
                                alphaIndexedBy(Boolean.class, Index.ConstraintType.EQUAL, -1, a -> predicate.test((A) a), true)));
        DroolsUniRuleStructure<A> newStructure = new DroolsUniRuleStructure<>(ruleStructure.getA(), patternWithFilter,
                ruleStructure.getSupportingRuleItems(), ruleStructure.getVariableIdSupplier());
        return new DroolsUniCondition<>(newStructure);
    }

    public <NewA, ResultContainer> DroolsUniCondition<NewA> andCollect(
            UniConstraintCollector<A, ResultContainer, NewA> collector) {
        Variable<A> inputVariable = ruleStructure.getA();
        PatternDSL.PatternDef<Object> innerAccumulatePattern = ruleStructure.getPrimaryPattern().build();
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Variable<NewA> outputVariable = (Variable<NewA>) declarationOf(Object.class);
        DroolsPatternBuilder<NewA> accumulateResult = new DroolsPatternBuilder<>(outputVariable);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, inputVariable).as(outputVariable));
        DroolsUniRuleStructure<NewA> newRuleStructure = new DroolsUniRuleStructure<>(outputVariable,
                accumulateResult, ruleStructure.rebuildSupportingRuleItems(outerAccumulatePattern),
                ruleStructure.getVariableIdSupplier());
        return new DroolsUniCondition<>(newRuleStructure);
    }

    public <NewA> DroolsUniCondition<NewA> andGroup(Function<A, NewA> groupKeyMapping) {
        Variable<NewA> mappedVariable = ruleStructure.createVariable("mapped");
        PatternDSL.PatternDef<Object> innerAccumulatePattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.bind(mappedVariable, k -> groupKeyMapping.apply((A) k)))
                .build();
        Variable<Set> setOfGroupKeys = ruleStructure.createVariable(Set.class, "setOfGroupKey");
        PatternDSL.PatternDef<Set> pattern = pattern(setOfGroupKeys)
                .expr("Set of groupKey", set -> !set.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Set::size, 0));
        ExprViewItem<Object> accumulate = DSL.accumulate(innerAccumulatePattern,
                accFunction(CollectSetAccumulateFunction.class, mappedVariable).as(setOfGroupKeys));
        Variable<NewA> groupKey = ruleStructure.createVariable("groupKey", from(setOfGroupKeys));
        DroolsPatternBuilder<NewA> finalGroupKeyPattern = new DroolsPatternBuilder<>(groupKey);
        DroolsUniRuleStructure<NewA> newRuleStructure = new DroolsUniRuleStructure<>(groupKey,
                finalGroupKeyPattern, ruleStructure.rebuildSupportingRuleItems(pattern, accumulate),
                ruleStructure.getVariableIdSupplier());
        return new DroolsUniCondition<>(newRuleStructure);
    }

    /**
     * The goal of this method is to create the left-hand side of a rule to look like this:
     *
     * <pre>
     * when
     *     set(size > 0): accumulate(Person(), $set: Pair.of(Person::getCity, ConstraintCollectors.count()))
     *     Pair($newA: left, $newB: right) from $set
     * then
     *     ...
     * end
     * </pre>
     * <p>
     * Note: This is pseudo-code and the actual Drools code will look slightly different in terms of syntax.
     * @param groupKeyMapping never null, grouping to apply
     * @param collector never null, collector to apply
     * @param <ResultContainer> implementation detail, unimportant
     * @param <NewA> type of the first logical fact
     * @param <NewB> type of the second logical fact
     * @return
     */
    public <ResultContainer, NewA, NewB> DroolsBiCondition<NewA, NewB> andGroupWithCollect(
            Function<A, NewA> groupKeyMapping, UniConstraintCollector<A, ResultContainer, NewB> collector) {
        Variable<A> collectingOnVar = ruleStructure.createVariable(ruleStructure.getA().getType(), "collectingOn");
        Variable<NewA> groupKeyVar = ruleStructure.createVariable("groupKey");
        Variable<Set> setOfPairsVar = ruleStructure.createVariable(Set.class, "setOfPairs");
        // Prepare the list of pairs.
        PatternDSL.PatternDef<Set> pattern = pattern(setOfPairsVar)
                .expr("Set of groupBy+collect pairs", set -> !set.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Set::size, 0));
        PatternDSL.PatternDef<Object> innerNewACollectingPattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.bind(groupKeyVar, a -> groupKeyMapping.apply((A) a))
                        .bind(collectingOnVar, a -> (A) a))
                .build();
        ExprViewItem<Object> accumulate = DSL.accumulate(innerNewACollectingPattern,
                accFunction(() -> new DroolsGroupByInvoker<>(collector, groupKeyVar, collectingOnVar))
                        .as(setOfPairsVar));
        // Load one pair from the list.
        Variable<DroolsGroupByAccumulator.Pair> onePairVar = ruleStructure.createVariable(
                DroolsGroupByAccumulator.Pair.class, "pair", from(setOfPairsVar));
        Variable<NewA> newAVar = ruleStructure.createVariable("newA");
        Variable<NewB> newBVar = ruleStructure.createVariable("newB");
        DroolsPatternBuilder<DroolsGroupByAccumulator.Pair> finalPairPattern = new DroolsPatternBuilder<>(onePairVar)
                .expand(p -> p.bind(newAVar, pair -> (NewA) pair.key))
                .expand(p -> p.bind(newBVar, pair -> (NewB) pair.value));
        DroolsBiRuleStructure<NewA, NewB> newRuleStructure = new DroolsBiRuleStructure<>(newAVar, newBVar,
                finalPairPattern, ruleStructure.rebuildSupportingRuleItems(pattern, accumulate),
                ruleStructure.getVariableIdSupplier());
        return new DroolsBiCondition<>(newRuleStructure);
    }

    public <B> DroolsBiCondition<A, B> andJoin(DroolsUniCondition<B> bCondition, AbstractBiJoiner<A, B> biJoiner) {
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        // We rebuild the A pattern, binding variables for left parts of the joins.
        DroolsPatternBuilder<Object> newAPattern = ruleStructure.getPrimaryPattern();
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = ruleStructure.createVariable("joinVar" + currentMappingIndex);
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            newAPattern = newAPattern.expand(
                    p -> p.bind(joinVar, a -> leftMapping.apply((A) a)));
            joinVars[currentMappingIndex] = joinVar;
        }
        DroolsUniRuleStructure<A> newARuleStructure = new DroolsUniRuleStructure<>(ruleStructure.getA(), newAPattern,
                ruleStructure.getSupportingRuleItems(), ruleStructure.getVariableIdSupplier());
        // We rebuild the B pattern, joining with the new A pattern using its freshly bound join variables.
        DroolsUniRuleStructure<B> bRuleStructure = bCondition.ruleStructure;
        Variable<B> bVariable = bRuleStructure.getA();
        DroolsPatternBuilder<Object> newBPattern = bRuleStructure.getPrimaryPattern();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            int currentMappingIndex = mappingIndex;
            JoinerType joinerType = joinerTypes[currentMappingIndex];
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            Function<B, Object> rightMapping = biJoiner.getRightMapping(currentMappingIndex);
            Function1<Object, Object> rightExtractor = b -> rightMapping.apply((B) b);
            Predicate2<Object, A> predicate = (b, a) -> { // We only extract B; A is coming from a pre-bound join var.
                return joinerType.matches(a, rightExtractor.apply(b));
            };
            newBPattern = newBPattern.expand(
                    p -> {
                        BetaIndex<Object, A, Object> betaIndex = betaIndexedBy(Object.class,
                                getConstraintType(joinerType), currentMappingIndex, rightExtractor, leftMapping::apply);
                        return p.expr("Join using joiner #" + currentMappingIndex + " in " + biJoiner,
                                joinVars[currentMappingIndex], predicate, betaIndex);
                    });
        }
        DroolsUniRuleStructure<B> newBRuleStructure = new DroolsUniRuleStructure<>(bVariable, newBPattern,
                bRuleStructure.getSupportingRuleItems(), ruleStructure.getVariableIdSupplier());
        // And finally we return the new condition that is based on the new A and B patterns.
        return new DroolsBiCondition<>(new DroolsBiRuleStructure<>(newARuleStructure, newBRuleStructure,
                ruleStructure.getVariableIdSupplier()));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, __) -> impactScore(drools, scoreHolder));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntFunction<A> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a) -> impactScore(drools, scoreHolder, matchWeighter.applyAsInt(a)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongFunction<A> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a) -> impactScore(drools, scoreHolder, matchWeighter.applyAsLong(a)));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            Function<A, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal,
                (drools, scoreHolder, a) -> impactScore(drools, scoreHolder, matchWeighter.apply(a)));
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block3<Drools, ScoreHolder, A> consequenceImpl) {
        ConsequenceBuilder._2<ScoreHolder, A> consequence = on(scoreHolderGlobal, ruleStructure.getA())
                .execute(consequenceImpl);
        return ruleStructure.rebuildSupportingRuleItems(ruleStructure.getPrimaryPattern().build(), consequence);
    }
}
