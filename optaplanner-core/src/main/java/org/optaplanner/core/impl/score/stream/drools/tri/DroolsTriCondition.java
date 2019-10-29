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

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.drools.model.Drools;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Predicate3;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsInferredMetadata;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsMetadata;

import static org.drools.model.DSL.on;

public final class DroolsTriCondition<A, B, C> {

    private final DroolsMetadata<Object, A> aMetadata;
    private final DroolsMetadata<Object, B> bMetadata;
    private final DroolsMetadata<Object, C> cMetadata;

    public DroolsTriCondition(DroolsMetadata<Object, A> aMetadata, DroolsMetadata<Object, B> bMetadata,
            DroolsMetadata<Object, C> cMetadata) {
        this.aMetadata = aMetadata;
        this.bMetadata = bMetadata;
        this.cMetadata = cMetadata;
    }

    public DroolsMetadata<Object, A> getAMetadata() {
        return aMetadata;
    }

    public DroolsMetadata<Object, B> getBMetadata() {
        return bMetadata;
    }

    public DroolsMetadata<Object, C> getCMetadata() {
        return cMetadata;
    }

    public DroolsTriCondition<A, B, C> andFilter(TriPredicate<A, B, C> predicate) {
        Predicate3<Object, Object, Object> filter = (c, a, b) -> predicate.test(aMetadata.extract(a),
                bMetadata.extract(b), cMetadata.extract(c));
        Supplier<PatternDSL.PatternDef<Object>> patternSupplier = () -> cMetadata.buildPattern()
                .expr("Filter using " + predicate, aMetadata.getVariableDeclaration(),
                        bMetadata.getVariableDeclaration(), filter);
        return new DroolsTriCondition<>(aMetadata, bMetadata, cMetadata.substitute(patternSupplier));
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal) {
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, __, ___, ____) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext);
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToIntTriFunction<A, B, C> matchWeighter) {
        ToIntTriFunction<Object, Object, Object> weightMultiplier = (a, b, c) -> matchWeighter.applyAsInt(
                aMetadata.extract(a), bMetadata.extract(b), cMetadata.extract(c));
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b, c) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.applyAsInt(a, b, c));
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            ToLongTriFunction<A, B, C> matchWeighter) {
        ToLongTriFunction<Object, Object, Object> weightMultiplier = (a, b, c) -> matchWeighter.applyAsLong(
                aMetadata.extract(a), bMetadata.extract(b), cMetadata.extract(c));
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b, c) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.applyAsLong(a, b, c));
        });
    }

    public List<RuleItemBuilder<?>> completeWithScoring(
            Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal,
            TriFunction<A, B, C, BigDecimal> matchWeighter) {
        TriFunction<Object, Object, Object, BigDecimal> weightMultiplier = (a, b, c) -> matchWeighter.apply(
                aMetadata.extract(a), bMetadata.extract(b), cMetadata.extract(c));
        return completeWithScoring(scoreHolderGlobal, (drools, scoreHolder, a, b, c) -> {
            RuleContext kcontext = (RuleContext) drools;
            scoreHolder.impactScore(kcontext, weightMultiplier.apply(a, b, c));
        });
    }

    private <ScoreHolder extends AbstractScoreHolder<?>> List<RuleItemBuilder<?>> completeWithScoring(
            Global<ScoreHolder> scoreHolderGlobal,
            Block5<Drools, ScoreHolder, Object, Object, Object> consequenceImpl) {
        ConsequenceBuilder._4<ScoreHolder, Object, Object, Object> consequence =
                on(scoreHolderGlobal, aMetadata.getVariableDeclaration(), bMetadata.getVariableDeclaration(),
                        cMetadata.getVariableDeclaration())
                        .execute(consequenceImpl);
        if (aMetadata instanceof DroolsInferredMetadata && bMetadata instanceof DroolsInferredMetadata &&
                cMetadata instanceof DroolsInferredMetadata) {
            // In case of logical tuples, all patterns will be the same logical tuple, and therefore we just add one.
            return Arrays.asList(cMetadata.buildPattern(), consequence);
        } else {
            return Arrays.asList(aMetadata.buildPattern(), bMetadata.buildPattern(), cMetadata.buildPattern(),
                    consequence);
        }
    }

}
