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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.model.BetaIndex;
import org.drools.model.DSL;
import org.drools.model.Declaration;
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
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsBiCondition;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAccumulateContext;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsGenuineMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsInferredMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsLogicalTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsMetadata;

import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.from;
import static org.drools.model.PatternDSL.pattern;

public final class DroolsUniCondition<A> {

    private final DroolsMetadata<Object, A> aMetadata;

    public DroolsUniCondition(Class<A> aVariableType) {
        Declaration<A> aVariableDeclaration = declarationOf(aVariableType);
        this.aMetadata = (DroolsGenuineMetadata) DroolsMetadata.ofGenuine(aVariableDeclaration);
    }

    public DroolsUniCondition(Declaration<DroolsLogicalTuple> aVariableDeclaration,
            Function<Declaration<DroolsLogicalTuple>, PatternDSL.PatternDef<DroolsLogicalTuple>> patternProvider) {
        this.aMetadata = (DroolsInferredMetadata) DroolsMetadata.ofInferred(aVariableDeclaration,
                () -> patternProvider.apply(aVariableDeclaration));
    }

    private DroolsUniCondition(DroolsMetadata<Object, A> aMetadata) {
        this.aMetadata = aMetadata;
    }

    public DroolsMetadata<Object, A> getAMetadata() {
        return aMetadata;
    }

    public DroolsUniCondition<A> andFilter(Predicate<A> predicate) {
        Predicate1<Object> filter = (Predicate1<Object>) filter(predicate, aMetadata);
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> aMetadata.buildPattern()
                .expr("Filter using " + predicate, filter);
        return new DroolsUniCondition<>(aMetadata.substitute(patternSupplier));
    }

    private <B> PatternDSL.PatternDef<B> join(PatternDSL.PatternDef<B> pattern, DroolsMetadata<Object, B> bMetadata,
            AbstractBiJoiner<A, B> biJoiner, int mappingIndex) {
        JoinerType joinerType = biJoiner.getJoinerTypes()[mappingIndex];
        Function1<A, Object> leftExtractor = transformingExtractor(biJoiner.getLeftMapping(mappingIndex), aMetadata);
        Function1<B, Object> rightExtractor = transformingExtractor(biJoiner.getRightMapping(mappingIndex), bMetadata);
        Predicate2<B, A> predicate = (b, a) -> {
            Object left = leftExtractor.apply(a);
            Object right = rightExtractor.apply(b);
            return joinerType.matches(left, right);
        };
        BetaIndex<B, A, Object> betaIndex = betaIndexedBy(Object.class, getConstraintType(joinerType), mappingIndex,
                rightExtractor, leftExtractor);
        Declaration<A> aVariableDeclaration = (Declaration<A>) aMetadata.getVariableDeclaration();
        return pattern.expr("Join using joiner #" + mappingIndex + " in " + biJoiner, aVariableDeclaration,
                predicate, betaIndex);
    }

    private static <A> Predicate1<A> filter(Predicate<A> predicate, DroolsMetadata<Object, A> metadata) {
        // No need to convert logical fact into genuine fact. Will be called many times, this micro-optimization helps.
        if (metadata instanceof DroolsGenuineMetadata) {
            return predicate::test;
        } else {
            Function<Object, A> extractor = metadata::extract;
            return (a) -> predicate.test(extractor.apply(a));
        }
    }

    private static <A, X> Function1<A, X> transformingExtractor(Function<A, X> mapping,
            DroolsMetadata<Object, A> metadata) {
        // No need to convert logical fact into genuine fact. Will be called many times, this micro-optimization helps.
        if (metadata instanceof DroolsGenuineMetadata) {
            return mapping::apply;
        } else {
            Function<Object, X> extractingMapper = mapping.compose(metadata::extract);
            return extractingMapper::apply;
        }
    }

    public <B> DroolsBiCondition<A, B> andJoin(DroolsUniCondition<B> bCondition, AbstractBiJoiner<A, B> biJoiner) {
        DroolsMetadata<Object, B> bMetadata = bCondition.aMetadata;
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> {
            PatternDSL.PatternDef pattern = bMetadata.buildPattern();
            JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
            for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
                pattern = join(pattern, bMetadata, biJoiner, mappingIndex);
            }
            return pattern;
        };
        return new DroolsBiCondition<>(aMetadata, bMetadata.substitute(patternSupplier));
    }

    /**
     * The goal of this method is to create the left-hand side of a rule to look like this:
     *
     * <pre>
     * when
     *     accumulate(Person(), $set: collectSet(Person::getCity))
     *     $newA : City() from $set // grouping
     *     accumulate(Person(getCity() == $newA), $newB: collect(ConstraintCollectors.count()))
     * then
     *     insertLogical($newA, $newB);
     * end
     * </pre>
     *
     * Note: This is pseudo-code and the actual Drools code will look slightly different in terms of syntax.
     * @param ruleId never null, id of the context in which the new facts will be inserted
     * @param groupKeyMapping never null, grouping to apply
     * @param collector never null, collector to apply
     * @param <ResultContainer> implementation detail, unimportant
     * @param <NewA> type of the first logical fact
     * @param <NewB> type of the second logical fact
     * @return
     */
    public <ResultContainer, NewA, NewB> List<RuleItemBuilder<?>> completeWithLogicalInsert(
            Object ruleId, Function<A, NewA> groupKeyMapping,
            UniConstraintCollector<A, ResultContainer, NewB> collector) {
        Function1<Object, NewA> grouper = (Function1<Object, NewA>) transformingExtractor(groupKeyMapping, aMetadata);
        // Accumulate all NewA into a set.
        Declaration<NewA> innerNewADeclaration = (Declaration<NewA>) declarationOf(Object.class);
        PatternDSL.PatternDef<Object> innerNewACollectingPattern = aMetadata.buildPattern()
                .bind(innerNewADeclaration, grouper);
        Declaration<Collection> setOfNewADeclaration = declarationOf(Collection.class);
        ExprViewItem<Object> collectingPattern = DSL.accumulate(innerNewACollectingPattern,
                accFunction(CollectSetAccumulateFunction::new, innerNewADeclaration).as(setOfNewADeclaration));
        // Operate individually with every NewA from that set, creating the final grouping.
        Declaration<NewA> actualNewADeclaration = (Declaration<NewA>) declarationOf(Object.class,
                from(setOfNewADeclaration));
        // And run an accumulate on all A which match NewA, applying the collector.
        Predicate2<Object, NewA> matcher = (a, newA) -> grouper.apply(a).equals(newA);
        BetaIndex<Object, NewA, Object> index = betaIndexedBy(Object.class, Index.ConstraintType.EQUAL, 0,
                grouper::apply, u -> u);
        PatternDSL.PatternDef<Object> innerAccumulatePattern = aMetadata.buildPattern()
                .expr("Filter by group key mapping " + groupKeyMapping, actualNewADeclaration, matcher, index)
                .bind(aMetadata.getVariableDeclaration(), aMetadata::extract);
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Declaration<Object> accumulateResultVariable = declarationOf(Object.class);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, aMetadata.getVariableDeclaration()).as(accumulateResultVariable));
        ConsequenceBuilder._2<?, ?> consequence = on(actualNewADeclaration, accumulateResultVariable)
                .execute((drools, newA, newB) -> {
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, newA, newB));
                });
        return Arrays.asList(collectingPattern, pattern(setOfNewADeclaration), pattern(actualNewADeclaration),
                outerAccumulatePattern, consequence);
    }

    public <ResultContainer, NewA> List<RuleItemBuilder<?>> completeWithLogicalInsert(
            Object ruleId, UniConstraintCollector<A, ResultContainer, NewA> collector) {
        DroolsMetadata<Object, A> inputMetadata = getAMetadata();
        Variable<Object> inputVariable = inputMetadata.getVariableDeclaration();
        PatternDSL.PatternDef<Object> innerAccumulatePattern = inputMetadata.buildPattern().bind(inputVariable,
                inputMetadata::extract);
        AccumulateFunction<DroolsAccumulateContext<ResultContainer>> accumulateFunction =
                new DroolsUniAccumulateFunctionBridge<>(collector);
        Variable<Object> outputVariable = declarationOf(Object.class);
        ExprViewItem<Object> outerAccumulatePattern = DSL.accumulate(innerAccumulatePattern,
                accFunction(() -> accumulateFunction, inputVariable).as(outputVariable));
        ConsequenceBuilder._1<?> consequence = on(outputVariable)
                .execute((drools, newA) -> {
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, newA));
                });
        return Arrays.asList(outerAccumulatePattern, consequence);
    }

    public <GroupKey_> List<RuleItemBuilder<?>> completeWithLogicalInsert(Object ruleId,
            Function<A, GroupKey_> groupKeyMapping) {
        Function1<A, GroupKey_> grouper = transformingExtractor(groupKeyMapping, aMetadata);
        ConsequenceBuilder._1<?> consequence = on(aMetadata.getVariableDeclaration())
                .execute((drools, a) -> {
                    GroupKey_ aMapped = grouper.apply((A) a);
                    RuleContext kcontext = (RuleContext) drools;
                    kcontext.insertLogical(new DroolsLogicalTuple(ruleId, aMapped));
                });
        return Arrays.asList(aMetadata.buildPattern(), consequence);
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, __) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntFunction<A> matchWeighter) {
        ToIntFunction<Object> weightMultiplier = a -> matchWeighter.applyAsInt(aMetadata.extract(a));
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.applyAsInt(a));
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongFunction<A> matchWeighter) {
        ToLongFunction<Object> weightMultiplier = a -> matchWeighter.applyAsLong(aMetadata.extract(a));
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.applyAsLong(a));
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            Function<A, BigDecimal> matchWeighter) {
        Function<Object, BigDecimal> weightMultiplier = matchWeighter.compose(aMetadata::extract);
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.apply(a));
        });
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block3<Drools, ScoreHolder, Object> consequenceImpl) {
        ConsequenceBuilder._2<ScoreHolder, Object> consequence =
                on(scoreHolderGlobal, aMetadata.getVariableDeclaration())
                        .execute(consequenceImpl);
        return Arrays.asList(aMetadata.buildPattern(), consequence);
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

}
