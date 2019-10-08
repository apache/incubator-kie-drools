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
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.drools.model.Declaration;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsScoringUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final DroolsAbstractUniConstraintStream<Solution_, A> parent;
    private final DroolsConstraint<Solution_> constraint;
    private final boolean noMatchWeigher;
    private final ToIntFunction<A> intMatchWeigher;
    private final ToLongFunction<A> longMatchWeigher;
    private final Function<A, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsConstraint<Solution_> constraint) {
        this(constraintFactory, parent, constraint, true, null, null, null);
    }

    public DroolsScoringUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsConstraint<Solution_> constraint, ToIntFunction<A> intMatchWeigher) {
        this(constraintFactory, parent, constraint, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsConstraint<Solution_> constraint, ToLongFunction<A> longMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsConstraint<Solution_> constraint, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent,
            DroolsConstraint<Solution_> constraint, boolean noMatchWeigher,
            ToIntFunction<A> intMatchWeigher, ToLongFunction<A> longMatchWeigher, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory);
        this.parent = parent;
        this.constraint = constraint;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public List<DroolsFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    @Override
    public void createRuleItemBuilders(List<RuleItemBuilder<?>> ruleItemBuilderList,
            Global<? extends AbstractScoreHolder> scoreHolderGlobal) {
        PatternDSL.PatternDef<A> parentPattern = getPattern();
        Declaration<A> aVar = getVariableDeclaration();
        ruleItemBuilderList.add(parentPattern);
        ConsequenceBuilder._2<? extends AbstractScoreHolder, A> consequence;
        if (intMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar)
                    .execute((drools, scoreHolder, a) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        int weightMultiplier = intMatchWeigher.applyAsInt(a);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (longMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar)
                    .execute((drools, scoreHolder, a) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        long weightMultiplier = longMatchWeigher.applyAsLong(a);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (bigDecimalMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar)
                    .execute((drools, scoreHolder, a) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        BigDecimal weightMultiplier = bigDecimalMatchWeigher.apply(a);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (noMatchWeigher) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar)
                    .execute((drools, scoreHolder, a) -> {
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
    public Declaration<A> getVariableDeclaration() {
        return parent.getVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getPattern() {
        return parent.getPattern();
    }

    @Override
    public String toString() {
        return "Scoring()";
    }

}
