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

package org.optaplanner.core.impl.score.stream.drools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.drools.model.Global;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBase;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.constraintweight.descriptor.ConstraintConfigurationDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;
import org.optaplanner.core.impl.score.stream.InnerConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsFromUniConstraintStream;

import static org.drools.model.DSL.globalOf;

public final class DroolsConstraintFactory<Solution_> implements InnerConstraintFactory<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final String defaultConstraintPackage;
    private final AtomicInteger createdRuleCounter = new AtomicInteger();

    public DroolsConstraintFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        ConstraintConfigurationDescriptor<Solution_> configurationDescriptor
                = solutionDescriptor.getConstraintConfigurationDescriptor();
        if (configurationDescriptor == null) {
            defaultConstraintPackage = solutionDescriptor.getSolutionClass().getPackage().getName();
        } else {
            defaultConstraintPackage = configurationDescriptor.getConstraintPackage();
        }
    }

    @Override
    public <A> UniConstraintStream<A> fromUnfiltered(Class<A> fromClass) {
        return new DroolsFromUniConstraintStream<>(this, fromClass);
    }

    @Override
    public <A> BiConstraintStream<A, A> fromUniquePair(Class<A> fromClass, BiJoiner<A, A> joiner) {
        MemberAccessor planningIdMemberAccessor = ConfigUtils.findPlanningIdMemberAccessor(fromClass);
        if (planningIdMemberAccessor == null) {
            throw new IllegalArgumentException("The fromClass (" + fromClass + ") has no member with a @"
                    + PlanningId.class.getSimpleName() + " annotation,"
                    + " so the pairs can be made unique ([A,B] vs [B,A]).");
        }
        Function<A, Comparable> planningIdGetter = (fact) -> (Comparable<?>) planningIdMemberAccessor.executeGetter(fact);
        return from(fromClass).join(fromClass, joiner, Joiners.lessThan(planningIdGetter));
    }

    // ************************************************************************
    // SessionFactory creation
    // ************************************************************************

    @Override
    public ConstraintSessionFactory<Solution_> buildSessionFactory(Constraint[] constraints) {
        ModelImpl model = new ModelImpl();

        AbstractScoreHolder<?> scoreHolder = (AbstractScoreHolder<?>) solutionDescriptor.getScoreDefinition()
                .buildScoreHolder(false);
        Class<? extends AbstractScoreHolder<?>> scoreHolderClass =
                (Class<? extends AbstractScoreHolder<?>>) scoreHolder.getClass();
        Global<? extends AbstractScoreHolder<?>> scoreHolderGlobal = globalOf(scoreHolderClass,
                solutionDescriptor.getSolutionClass().getPackage().getName(),
                DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY);
        model.addGlobal(scoreHolderGlobal);

        List<DroolsConstraint<Solution_>> droolsConstraintList = new ArrayList<>(constraints.length);
        Set<String> constraintIdSet = new HashSet<>(constraints.length);
        // Rule library is used to ensure some rules, such as groupBy()s, are shared when their streams are reused.
        Map<DroolsAbstractConstraintStream<Solution_>, Rule> ruleLibrary = new LinkedHashMap<>();
        for (Constraint constraint : constraints) {
            if (constraint.getConstraintFactory() != this) {
                throw new IllegalStateException("The constraint (" + constraint.getConstraintId()
                        + ") must be created from the same constraintFactory.");
            }
            boolean added = constraintIdSet.add(constraint.getConstraintId());
            if (!added) {
                throw new IllegalStateException(
                        "There are 2 constraints with the same constraintName (" + constraint.getConstraintName()
                                + ") in the same constraintPackage (" + constraint.getConstraintPackage() + ").");
            }
            DroolsConstraint<Solution_> droolsConstraint = (DroolsConstraint) constraint;
            droolsConstraintList.add(droolsConstraint);
            droolsConstraint.createRules(ruleLibrary, scoreHolderGlobal);
        }
        ruleLibrary.values().forEach(model::addRule);
        // TODO when trace is active, show the Rule (DRL or exectable model) in logging
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model);
        return new DroolsConstraintSessionFactory<>(solutionDescriptor, kieBase, droolsConstraintList);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    /**
     * The constraint creating code needs to call this method to retrieve an ID of the rule.
     * This ID then needs to be used in the rule name for easier debugging.
     * It is imperative that this method only be called once per every rule created, as it maintains an internal
     * counter of created rules.
     * The sequence of IDs returned will start with 1 and continue towards {@link Integer#MAX_VALUE}.
     * As long as rule creation calls this method in the same order, the rules will always receive the same ID.
     * @return A unique numeric ID of the rule.
     */
    public int getNextRuleId() {
        return createdRuleCounter.incrementAndGet();
    }

    @Override
    public String getDefaultConstraintPackage() {
        return defaultConstraintPackage;
    }

}
