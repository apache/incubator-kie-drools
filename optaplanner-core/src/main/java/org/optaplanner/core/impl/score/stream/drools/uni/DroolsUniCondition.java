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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.model.AlphaIndex;
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
import org.drools.model.view.ViewItem;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAccumulateContext;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;

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
        AlphaIndex<Object, Boolean> index = alphaIndexedBy(Boolean.class, Index.ConstraintType.EQUAL, -1,
                a -> predicate.test((A) a), true);
        UnaryOperator<PatternDSL.PatternDef<Object>> patternWithFilter =
                p -> p.expr("Filter using " + predicate, filter, index);
        DroolsUniRuleStructure<A> newStructure = ruleStructure.amend(patternWithFilter);
        return new DroolsUniCondition<>(newStructure);
    }

    public <NewA, ResultContainer> DroolsUniCondition<NewA> andCollect(
            UniConstraintCollector<A, ResultContainer, NewA> collector) {
        Variable<A> inputVariable = ruleStructure.getA();
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleStructure.getPrimaryPattern().build());
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Variable<NewA> outputVariable = (Variable<NewA>) declarationOf(Object.class, "collected");
        ViewItem<?> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, inputVariable).as(outputVariable));
        DroolsUniRuleStructure<NewA> newRuleStructure = ruleStructure.recollect(outputVariable, outerAccumulatePattern);
        return new DroolsUniCondition<>(newRuleStructure);
    }

    public <NewA> DroolsUniCondition<NewA> andGroup(Function<A, NewA> groupKeyMapping) {
        Variable<NewA> mappedVariable = ruleStructure.createVariable("mapped");
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(ruleStructure.getPrimaryPattern()
                .expand(p -> p.bind(mappedVariable, k -> groupKeyMapping.apply((A) k)))
                .build());
        Variable<Set<NewA>> setOfGroupKeys = (Variable<Set<NewA>>) ruleStructure.createVariable(Set.class, "setOfGroupKey");
        AlphaIndex<Set<NewA>, Integer> index = alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1,
                Set::size, 0);
        PatternDSL.PatternDef<Set<NewA>> pattern = pattern(setOfGroupKeys)
                .expr("Set of " + mappedVariable.getName(), set -> !set.isEmpty(), index);
        ViewItem<?> accumulate = DSL.accumulate(innerAccumulatePattern,
                accFunction(CollectSetAccumulateFunction.class, mappedVariable).as(setOfGroupKeys));
        DroolsUniRuleStructure<NewA> newRuleStructure = ruleStructure.regroup(setOfGroupKeys, pattern, accumulate);
        return new DroolsUniCondition<>(newRuleStructure);
    }

    public <ResultContainer, NewA, NewB> DroolsBiCondition<NewA, NewB> andGroupWithCollect(
            Function<A, NewA> groupKeyMapping, UniConstraintCollector<A, ResultContainer, NewB> collector) {
        Variable<A> collectingOnVar =
                (Variable<A>) ruleStructure.createVariable(ruleStructure.getA().getType(), "collectingOn");
        Variable<NewA> groupKeyVar = ruleStructure.createVariable("groupKey");
        Variable<Set<BiTuple<NewA, NewB>>> setOfPairsVar =
                (Variable<Set<BiTuple<NewA, NewB>>>) ruleStructure.createVariable(Set.class, "setOfPairs");
        String exprName = "Set of pairs of " + collectingOnVar.getName() + " and " + groupKeyVar.getName();
        PatternDSL.PatternDef<Set<BiTuple<NewA, NewB>>> pattern = pattern(setOfPairsVar)
                .expr(exprName, set -> !set.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Set::size, 0));
        // Prepare the list of pairs.
        PatternDSL.PatternDef<Object> innerNewACollectingPattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.bind(groupKeyVar, a -> groupKeyMapping.apply((A) a))
                        .bind(collectingOnVar, a -> (A) a))
                .build();
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(innerNewACollectingPattern);
        ViewItem<?> accumulate = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> new DroolsUniGroupByInvoker<>(collector, groupKeyVar, collectingOnVar))
                        .as(setOfPairsVar));
        // Load one pair from the list.
        Variable<BiTuple<NewA, NewB>> onePairVar =
                (Variable<BiTuple<NewA, NewB>>) ruleStructure.createVariable(BiTuple.class, "pair", from(setOfPairsVar));
        DroolsBiRuleStructure<NewA, NewB> newRuleStructure = ruleStructure.regroupBi(onePairVar, pattern, accumulate);
        return new DroolsBiCondition<>(newRuleStructure);
    }

    /**
     * The goal of this method is to create the left-hand side of a rule to look like this:
     *
     * <pre>
     * when
     *     set(size > 0): accumulate(Person(), $set: collectSet(Pair.of(Person::getCity, Person::getName)))
     *     Pair($newA: left, $newB: right) from $set
     * then
     *     ...
     * end
     * </pre>
     * <p>
     * Note: This is pseudo-code and the actual Drools code will look slightly different in terms of syntax.
     * @param groupKeyAMapping never null, first grouping to apply
     * @param groupKeyBMapping never null, second grouping to apply
     * @param <NewA> type of the first logical fact
     * @param <NewB> type of the second logical fact
     * @return
     */
    public <NewA, NewB> DroolsBiCondition<NewA, NewB> andGroupBi(Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping) {
        Variable<BiTuple<NewA, NewB>> pairVar = ruleStructure.createVariable("groupedPair");
        Variable<Set<BiTuple<NewA, NewB>>> setOfPairsVar =
                (Variable<Set<BiTuple<NewA, NewB>>>) ruleStructure.createVariable(Set.class, "setOf" + pairVar.getName());
        // Prepare the list of pairs.
        PatternDSL.PatternDef<Set<BiTuple<NewA, NewB>>> pattern = pattern(setOfPairsVar)
                .expr("Set of " + pairVar.getName(), set -> !set.isEmpty(),
                        alphaIndexedBy(Integer.class, Index.ConstraintType.GREATER_THAN, -1, Set::size, 0));
        PatternDSL.PatternDef<Object> innerNewACollectingPattern = ruleStructure.getPrimaryPattern()
                .expand(p -> p.bind(pairVar,
                        a -> new BiTuple<>(groupKeyAMapping.apply((A) a), groupKeyBMapping.apply((A) a))))
                .build();
        ViewItem<?> innerAccumulatePattern = getInnerAccumulatePattern(innerNewACollectingPattern);
        ExprViewItem<Object> accumulate = DSL.accumulate(innerAccumulatePattern,
                accFunction(CollectSetAccumulateFunction::new, pairVar).as(setOfPairsVar));
        // Load one pair from the list.
        Variable<BiTuple<NewA, NewB>> onePairVar =
                (Variable<BiTuple<NewA, NewB>>) ruleStructure.createVariable(BiTuple.class, "pair", from(setOfPairsVar));
        DroolsBiRuleStructure<NewA, NewB> newRuleStructure = ruleStructure.regroupBi(onePairVar, pattern, accumulate);
        return new DroolsBiCondition<>(newRuleStructure);
    }

    public <B> DroolsBiCondition<A, B> andJoin(DroolsUniCondition<B> bCondition, AbstractBiJoiner<A, B> biJoiner) {
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        // We rebuild the A pattern, binding variables for left parts of the joins.
        Function<PatternDSL.PatternDef<Object>, PatternDSL.PatternDef<Object>> aJoiner = UnaryOperator.identity();
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = ruleStructure.createVariable("joinVar" + currentMappingIndex);
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            aJoiner = aJoiner.andThen(p -> p.bind(joinVar, a -> leftMapping.apply((A) a)));
            joinVars[currentMappingIndex] = joinVar;
        }
        DroolsUniRuleStructure<A> newARuleStructure = ruleStructure.amend(aJoiner::apply);
        // We rebuild the B pattern, joining with the new A pattern using its freshly bound join variables.
        Function<PatternDSL.PatternDef<Object>, PatternDSL.PatternDef<Object>> bJoiner = UnaryOperator.identity();
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
            bJoiner = bJoiner.andThen(p -> {
                        BetaIndex<Object, A, Object> index = betaIndexedBy(Object.class, getConstraintType(joinerType),
                                currentMappingIndex, rightExtractor, leftMapping::apply);
                        return p.expr("Join using joiner #" + currentMappingIndex + " in " + biJoiner,
                                joinVars[currentMappingIndex], predicate, index);
                    });
        }
        DroolsUniRuleStructure<B> newBRuleStructure = bCondition.ruleStructure.amend(bJoiner::apply);
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
        return ruleStructure.finish(consequence);
    }
}
