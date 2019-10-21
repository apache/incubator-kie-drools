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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.model.Declaration;
import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block4;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsInferredMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsLogicalTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsMetadata;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsTriCondition;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsUniCondition;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

import static org.drools.model.DSL.on;

public final class DroolsBiCondition<A, B> {

    private final DroolsMetadata<Object, A> aMetadata;
    private final DroolsMetadata<Object, B> bMetadata;

    public DroolsBiCondition(DroolsMetadata<Object, A> aMetadata, DroolsMetadata<Object, B> bMetadata) {
        this.aMetadata = aMetadata;
        this.bMetadata = bMetadata;
    }

    public DroolsBiCondition(Declaration<DroolsLogicalTuple> aVariableDeclaration,
            Function<Declaration<DroolsLogicalTuple>, PatternDSL.PatternDef<DroolsLogicalTuple>> patternProvider) {
        // Share both the declaration and the pattern, as the data is all coming from the same DroolsLogicalTuple.
        PatternDSL.PatternDef<DroolsLogicalTuple> pattern = patternProvider.apply(aVariableDeclaration);
        this.aMetadata = (DroolsInferredMetadata) DroolsMetadata.ofInferred(aVariableDeclaration, () -> pattern, 0);
        this.bMetadata = (DroolsInferredMetadata) DroolsMetadata.ofInferred(aVariableDeclaration, () -> pattern, 1);
    }

    public DroolsMetadata<Object, A> getAMetadata() {
        return aMetadata;
    }

    public DroolsMetadata<Object, B> getBMetadata() {
        return bMetadata;
    }

    public DroolsBiCondition<A, B> andFilter(BiPredicate<A, B> predicate) {
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> bMetadata.buildPattern()
                .expr(aMetadata.getVariableDeclaration(),
                        (b, a) -> predicate.test(aMetadata.extract(a), bMetadata.extract(b)));
        return new DroolsBiCondition<>(aMetadata, bMetadata.substitute(patternSupplier));
    }

    public <C> DroolsTriCondition<A, B, C> andJoin(DroolsUniCondition<C> cCondition,
            AbstractTriJoiner<A, B, C> triJoiner) {
        DroolsMetadata<Object, C> cMetadata = cCondition.getAMetadata();
        // The expression ID is required yet seemingly unused. A random UUID is generated.
        Supplier<PatternDSL.PatternDef<Object>> newPattern = () -> cMetadata.buildPattern()
                .expr(UUID.randomUUID().toString(), aMetadata.getVariableDeclaration(),
                        bMetadata.getVariableDeclaration(),
                        (c, a, b) -> matches(triJoiner, aMetadata.extract(a), bMetadata.extract(b),
                                cMetadata.extract(c)));
        return new DroolsTriCondition<>(aMetadata, bMetadata, cMetadata.substitute(newPattern));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, __, ___) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntBiFunction<A, B> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b) -> {
            int weightMultiplier = matchWeighter.applyAsInt(aMetadata.extract(a), bMetadata.extract(b));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongBiFunction<A, B> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b) -> {
            long weightMultiplier = matchWeighter.applyAsLong(aMetadata.extract(a), bMetadata.extract(b));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            BiFunction<A, B, BigDecimal> matchWeighter) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b) -> {
            BigDecimal weightMultiplier = matchWeighter.apply(aMetadata.extract(a), bMetadata.extract(b));
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier);
        });
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal, Block4<Drools, ScoreHolder, Object, Object> consequenceImpl) {
        ConsequenceBuilder._3<ScoreHolder, Object, Object> consequence =
                on(scoreHolderGlobal, aMetadata.getVariableDeclaration(), bMetadata.getVariableDeclaration())
                        .execute(consequenceImpl);
        if (aMetadata instanceof DroolsInferredMetadata && bMetadata instanceof DroolsInferredMetadata) {
            // In case of logical tuples, both patterns will be the same logical tuple, and therefore we just add one.
            return Stream.of(bMetadata.buildPattern(), consequence)
                    .collect(Collectors.toList());
        } else {
            return Arrays.asList(aMetadata.buildPattern(), bMetadata.buildPattern(), consequence);
        }
    }

    private static <A, B, C> boolean matches(AbstractTriJoiner<A, B, C> triJoiner, A a, B b, C c) {
        Object[] leftMappings = triJoiner.getLeftCombinedMapping().apply(a, b);
        Object[] rightMappings = triJoiner.getRightCombinedMapping().apply(c);
        JoinerType[] joinerTypes = triJoiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            JoinerType joinerType = joinerTypes[i];
            if (!joinerType.matches(leftMappings[i], rightMappings[i])) {
                return false;
            }
        }
        return true;
    }

}
