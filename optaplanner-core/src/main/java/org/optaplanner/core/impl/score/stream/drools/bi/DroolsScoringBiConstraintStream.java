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
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.drools.model.Declaration;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsScoringBiConstraintStream<Solution_, A, B> extends DroolsAbstractBiConstraintStream<Solution_, A, B> {

    private final boolean noMatchWeigher;
    private final ToIntBiFunction<A, B> intMatchWeigher;
    private final ToLongBiFunction<A, B> longMatchWeigher;
    private final BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, ToIntBiFunction<A, B> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, ToLongBiFunction<A, B> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringBiConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent, boolean noMatchWeigher,
            ToIntBiFunction<A, B> intMatchWeigher, ToLongBiFunction<A, B> longMatchWeigher,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent);
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public void createRuleItemBuilders(List<RuleItemBuilder<?>> ruleItemBuilderList,
            Global<? extends AbstractScoreHolder> scoreHolderGlobal) {
        ruleItemBuilderList.add(getAPattern());
        ruleItemBuilderList.add(getBPattern());
        Declaration<A> aVar = getLeftVariableDeclaration();
        Declaration<B> bVar = getRightVariableDeclaration();
        ConsequenceBuilder._3<? extends AbstractScoreHolder, A, B> consequence;
        if (intMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar)
                    .execute((drools, scoreHolder, a, b) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        int weightMultiplier = intMatchWeigher.applyAsInt(a, b);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (longMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar)
                    .execute((drools, scoreHolder, a, b) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        long weightMultiplier = longMatchWeigher.applyAsLong(a, b);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (bigDecimalMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar)
                    .execute((drools, scoreHolder, a, b) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        BigDecimal weightMultiplier = bigDecimalMatchWeigher.apply(a, b);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (noMatchWeigher) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar)
                    .execute((drools, scoreHolder, a, b) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        scoreHolder.impactScore(kcontext);
                    });
        } else {
            throw new IllegalStateException("Impossible state: noMatchWeigher (" + noMatchWeigher + ").");
        }
        ruleItemBuilderList.add(consequence);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public Declaration<A> getLeftVariableDeclaration() {
        return parent.getLeftVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getAPattern() {
        return parent.getAPattern();
    }

    @Override
    public Declaration<B> getRightVariableDeclaration() {
        return parent.getRightVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<B> getBPattern() {
        return parent.getBPattern();
    }

    @Override
    public String toString() {
        return "BiScoring()";
    }

}
