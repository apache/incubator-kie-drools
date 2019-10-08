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
import java.util.List;

import org.drools.model.Declaration;
import org.drools.model.Global;
import org.drools.model.PatternDSL;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraint;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;

public final class DroolsScoringTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final DroolsConstraint<Solution_> constraint;
    private final boolean noMatchWeigher;
    private final ToIntTriFunction<A, B, C> intMatchWeigher;
    private final ToLongTriFunction<A, B, C> longMatchWeigher;
    private final TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher;

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, DroolsConstraint<Solution_> constraint) {
        this(constraintFactory, parent, constraint, true, null, null, null);
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, DroolsConstraint<Solution_> constraint,
            ToIntTriFunction<A, B, C> intMatchWeigher) {
        this(constraintFactory, parent, constraint, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, DroolsConstraint<Solution_> constraint,
            ToLongTriFunction<A, B, C> longMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, DroolsConstraint<Solution_> constraint,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private DroolsScoringTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent, DroolsConstraint<Solution_> constraint,
            boolean noMatchWeigher, ToIntTriFunction<A, B, C> intMatchWeigher,
            ToLongTriFunction<A, B, C> longMatchWeigher, TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent);
        this.constraint = constraint;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    public void createRuleItemBuilders(List<RuleItemBuilder<?>> ruleItemBuilderList,
            Global<? extends AbstractScoreHolder> scoreHolderGlobal) {
        ruleItemBuilderList.add(getAPattern());
        ruleItemBuilderList.add(getBPattern());
        ruleItemBuilderList.add(getCPattern());
        Declaration<A> aVar = getAVariableDeclaration();
        Declaration<B> bVar = getBVariableDeclaration();
        Declaration<C> cVar = getCVariableDeclaration();
        ConsequenceBuilder._4<? extends AbstractScoreHolder, A, B, C> consequence;
        if (intMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar, cVar)
                    .execute((drools, scoreHolder, a, b, c) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        int weightMultiplier = intMatchWeigher.applyAsInt(a, b, c);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (longMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar, cVar)
                    .execute((drools, scoreHolder, a, b, c) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        long weightMultiplier = longMatchWeigher.applyAsLong(a, b, c);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (bigDecimalMatchWeigher != null) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar, cVar)
                    .execute((drools, scoreHolder, a, b, c) -> {
                        RuleContext kcontext = (RuleContext) drools;
                        BigDecimal weightMultiplier = bigDecimalMatchWeigher.apply(a, b, c);
                        scoreHolder.impactScore(kcontext, weightMultiplier);
                    });
        } else if (noMatchWeigher) {
            consequence = PatternDSL.on(scoreHolderGlobal, aVar, bVar, cVar)
                    .execute((drools, scoreHolder, a, b, c) -> {
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
    public Declaration<A> getAVariableDeclaration() {
        return parent.getAVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<A> getAPattern() {
        return parent.getAPattern();
    }

    @Override
    public Declaration<B> getBVariableDeclaration() {
        return parent.getBVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<B> getBPattern() {
        return parent.getBPattern();
    }

    @Override
    public Declaration<C> getCVariableDeclaration() {
        return parent.getCVariableDeclaration();
    }

    @Override
    public PatternDSL.PatternDef<C> getCPattern() {
        return parent.getCPattern();
    }

    @Override
    public String toString() {
        return "TriScoring()";
    }


}
