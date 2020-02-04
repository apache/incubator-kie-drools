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
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;

import org.drools.model.AlphaIndex;
import org.drools.model.BetaIndex;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.RuleItemBuilder;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.Predicate2;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.FilteringBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.NoneBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiRuleStructure;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsCondition;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;
import org.optaplanner.core.impl.score.stream.drools.quad.DroolsQuadCondition;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;

import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.alphaIndexedBy;
import static org.drools.model.PatternDSL.betaIndexedBy;

public final class DroolsUniCondition<A, PatternVar>
        extends DroolsCondition<PatternVar, DroolsUniRuleStructure<A, PatternVar>> {

    public DroolsUniCondition(Class<A> aVariableType, LongSupplier variableIdSupplier) {
        this(new DroolsUniRuleStructure<>(aVariableType, variableIdSupplier));
    }

    public DroolsUniCondition(DroolsUniRuleStructure<A, PatternVar> ruleStructure) {
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

    public DroolsUniCondition<A, PatternVar> andFilter(Predicate<A> predicate) {
        Predicate1<PatternVar> filter = a -> predicate.test((A) a);
        AlphaIndex<PatternVar, Boolean> index = alphaIndexedBy(Boolean.class, Index.ConstraintType.EQUAL, -1,
                a -> predicate.test((A) a), true);
        UnaryOperator<PatternDef<PatternVar>> patternWithFilter =
                p -> p.expr("Filter using " + predicate, filter, index);
        DroolsUniRuleStructure<A, PatternVar> newStructure = ruleStructure.amend(patternWithFilter);
        return new DroolsUniCondition<>(newStructure);
    }

    public <NewA, __> DroolsUniCondition<NewA, NewA> andCollect(UniConstraintCollector<A, __, NewA> collector) {
        DroolsUniAccumulateFunctionBridge<A, __, NewA> bridge = new DroolsUniAccumulateFunctionBridge<>(collector);
        return collect(bridge, (pattern, tuple) -> pattern.bind(tuple, a -> (A) a));
    }

    public <NewA> DroolsUniCondition<NewA, NewA> andGroup(Function<A, NewA> groupKeyMapping) {
        return group((pattern, tuple) -> pattern.bind(tuple, a -> groupKeyMapping.apply((A) a)));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupWithCollect(
            Function<A, NewA> groupKeyMapping, UniConstraintCollector<A, ?, NewB> collector) {
        return groupWithCollect(() -> new DroolsUniToBiGroupByInvoker<>(groupKeyMapping, collector,
                getRuleStructure().getA()));
    }

    public <NewA, NewB> DroolsBiCondition<NewA, NewB, BiTuple<NewA, NewB>> andGroupBi(Function<A, NewA> groupKeyAMapping,
            Function<A, NewB> groupKeyBMapping) {
        return groupBi((pattern, tuple) -> pattern.bind(tuple, a -> {
            final NewA newA = groupKeyAMapping.apply((A) a);
            final NewB newB = groupKeyBMapping.apply((A) a);
            return new BiTuple<>(newA, newB);
        }));
    }

    public <NewA, NewB, NewC> DroolsTriCondition<NewA, NewB, NewC, TriTuple<NewA, NewB, NewC>> andGroupBiWithCollect(
            Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ?, NewC> collector) {
        return groupBiWithCollect(() -> new DroolsUniToTriGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collector, getRuleStructure().getA()));
    }

    public <NewA, NewB, NewC, NewD> DroolsQuadCondition<NewA, NewB, NewC, NewD, QuadTuple<NewA, NewB, NewC, NewD>>
    andGroupBiWithCollectBi(Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ?, NewC> collectorC, UniConstraintCollector<A, ?, NewD> collectorD) {
        return groupBiWithCollectBi(() -> new DroolsUniToQuadGroupByInvoker<>(groupKeyAMapping, groupKeyBMapping,
                collectorC, collectorD, getRuleStructure().getA()));
    }

    public <B, BPatternVar> DroolsBiCondition<A, B, BPatternVar> andJoin(DroolsUniCondition<B, BPatternVar> bCondition,
            AbstractBiJoiner<A, B> biJoiner) {
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        // We rebuild the A pattern, binding variables for left parts of the joins.
        Function<PatternDef<PatternVar>, PatternDef<PatternVar>> aJoiner = UnaryOperator.identity();
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = ruleStructure.createVariable("joinVar" + currentMappingIndex);
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            aJoiner = aJoiner.andThen(p -> p.bind(joinVar, a -> leftMapping.apply((A) a)));
            joinVars[currentMappingIndex] = joinVar;
        }
        DroolsUniRuleStructure<A, PatternVar> newARuleStructure = ruleStructure.amend(aJoiner::apply);
        // We rebuild the B pattern, joining with the new A pattern using its freshly bound join variables.
        Function<PatternDef<BPatternVar>, PatternDef<BPatternVar>> bJoiner = UnaryOperator.identity();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            int currentMappingIndex = mappingIndex;
            JoinerType joinerType = joinerTypes[currentMappingIndex];
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            Function<B, Object> rightMapping = biJoiner.getRightMapping(currentMappingIndex);
            Function1<BPatternVar, Object> rightExtractor = b -> rightMapping.apply((B) b);
            Predicate2<BPatternVar, A> predicate = (b, a) -> { // We only extract B; A is coming from a pre-bound join var.
                return joinerType.matches(a, rightExtractor.apply(b));
            };
            bJoiner = bJoiner.andThen(p -> {
                        BetaIndex<BPatternVar, A, Object> index = betaIndexedBy(Object.class, getConstraintType(joinerType),
                                currentMappingIndex, rightExtractor, leftMapping::apply);
                        return p.expr("Join using joiner #" + currentMappingIndex + " in " + biJoiner,
                                joinVars[currentMappingIndex], predicate, index);
                    });
        }
        DroolsUniRuleStructure<B, BPatternVar> newBRuleStructure = bCondition.ruleStructure.amend(bJoiner::apply);
        // And finally we return the new condition that is based on the new A and B patterns.
        return new DroolsBiCondition<>(new DroolsBiRuleStructure<>(newARuleStructure, newBRuleStructure,
                ruleStructure.getVariableIdSupplier()));
    }

    @SafeVarargs
    public final <B> DroolsUniCondition<A, PatternVar> andIfExists(Class<B> otherClass, BiJoiner<A, B>... biJoiners) {
        return andIfExistsOrNot(true, otherClass, biJoiners);
    }

    @SafeVarargs
    public final <B> DroolsUniCondition<A, PatternVar> andIfNotExists(Class<B> otherClass,
            BiJoiner<A, B>... biJoiners) {
        return andIfExistsOrNot(false, otherClass, biJoiners);
    }

    @SafeVarargs
    private final <B> DroolsUniCondition<A, PatternVar> andIfExistsOrNot(boolean shouldExist, Class<B> otherClass,
            BiJoiner<A, B>... biJoiners) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern
        AbstractBiJoiner<A, B> finalJoiner = null;
        BiPredicate<A, B> finalFilter = null;
        for (int i = 0; i < biJoiners.length; i++) {
            AbstractBiJoiner<A, B> biJoiner = (AbstractBiJoiner<A, B>) biJoiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (biJoiner instanceof NoneBiJoiner && biJoiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneBiJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(biJoiners) + " instead.");
            } else if (!(biJoiner instanceof FilteringBiJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + biJoiner + ") must not follow a filtering joiner ("
                            + biJoiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ?
                            biJoiner :
                            AbstractBiJoiner.merge(finalJoiner, biJoiner);
                }
            } else {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // We merge all filters into one, so that we don't pay the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ?
                        biJoiner.getFilter() :
                        finalFilter.and(biJoiner.getFilter());
            }
        }
        return applyJoiners(otherClass, finalJoiner, finalFilter, shouldExist);
    }

    private <B> DroolsUniCondition<A, PatternVar> applyJoiners(Class<B> otherClass, AbstractBiJoiner<A, B> biJoiner,
            BiPredicate<A, B> biPredicate, boolean shouldExist) {
        Variable<B> toExist = (Variable<B>) ruleStructure.createVariable(otherClass, "toExist");
        PatternDef<B> existencePattern = PatternDSL.pattern(toExist);
        if (biJoiner == null) {
            return applyFilters(ruleStructure, existencePattern, biPredicate, shouldExist);
        }
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        // We rebuild the A pattern, binding variables for left parts of the joins.
        Function<PatternDef<PatternVar>, PatternDef<PatternVar>> aJoiner = UnaryOperator.identity();
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = ruleStructure.createVariable("joinVar" + currentMappingIndex);
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            aJoiner = aJoiner.andThen(p -> p.bind(joinVar, a -> leftMapping.apply((A) a)));
            joinVars[currentMappingIndex] = joinVar;
        }
        DroolsUniRuleStructure<A, PatternVar> newARuleStructure = ruleStructure.amend(aJoiner::apply);
        // We create the B pattern, joining with the new A pattern using its freshly bound join variables.
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            int currentMappingIndex = mappingIndex;
            JoinerType joinerType = joinerTypes[currentMappingIndex];
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            Function<B, Object> rightMapping = biJoiner.getRightMapping(currentMappingIndex);
            Predicate2<B, A> predicate = (b, a) -> { // We only extract B; A is coming from a pre-bound join var.
                return joinerType.matches(a, rightMapping.apply(b));
            };
            BetaIndex<B, A, ?> index = betaIndexedBy(Object.class, getConstraintType(joinerType),
                    currentMappingIndex, rightMapping::apply, leftMapping::apply);
            existencePattern = existencePattern.expr("Join using joiner #" + currentMappingIndex + " in " + biJoiner,
                    joinVars[currentMappingIndex], predicate, index);
        }
        // And finally we add the filter to the B pattern
        return applyFilters(newARuleStructure, existencePattern, biPredicate, shouldExist);
    }

    private <B> DroolsUniCondition<A, PatternVar> applyFilters(
            DroolsUniRuleStructure<A, PatternVar> targetRuleStructure, PatternDef<B> existencePattern,
            BiPredicate<A, B> biPredicate, boolean shouldExist) {
        PatternDef<B> possiblyFilteredExistencePattern = biPredicate == null ?
                existencePattern :
                existencePattern.expr("Filter using " + biPredicate, ruleStructure.getA(),
                        (b, a) -> biPredicate.test(a, b));
        return new DroolsUniCondition<>(targetRuleStructure.existsOrNot(possiblyFilteredExistencePattern, shouldExist));
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
