/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.drools.model.Model;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBase;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.OptaPlannerRuleEventListener;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;

public final class DroolsConstraintSessionFactory<Solution_, Score_ extends Score<Score_>>
        implements ConstraintSessionFactory<Solution_, Score_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final Model originalModel;
    private final KieBase originalKieBase;
    private final Map<Rule, DroolsConstraint<Solution_>> compiledRuleToConstraintMap;
    private final Map<String, org.drools.model.Rule> constraintToModelRuleMap;
    private KieBase currentKieBase;
    private Set<String> currentlyDisabledConstraintIdSet = null;

    public DroolsConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor, Model model,
            DroolsConstraint<Solution_>[] constraints) {
        this.solutionDescriptor = solutionDescriptor;
        this.originalModel = model;
        this.originalKieBase = buildKieBaseFromModel(model);
        this.currentKieBase = originalKieBase;
        this.compiledRuleToConstraintMap = Arrays.stream(constraints)
                .collect(toMap(constraint -> currentKieBase.getRule(constraint.getConstraintPackage(),
                        constraint.getConstraintName()), Function.identity()));
        this.constraintToModelRuleMap = Arrays.stream(constraints)
                .collect(toMap(Constraint::getConstraintId, constraint -> model.getRules().stream()
                        .filter(rule -> Objects.equals(rule.getName(), constraint.getConstraintName()))
                        .filter(rule -> Objects.equals(rule.getPackage(), constraint.getConstraintPackage()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Impossible state: Rule for constraint (" +
                                constraint + ") not found."))));
    }

    private static KieBase buildKieBaseFromModel(Model model) {
        return KieBaseBuilder.createKieBaseFromModel(model, KieBaseMutabilityOption.DISABLED);
    }

    @Override
    public ConstraintSession<Solution_, Score_> buildSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        ScoreDefinition<Score_> scoreDefinition = solutionDescriptor.getScoreDefinition();
        AbstractScoreHolder<Score_> scoreHolder = scoreDefinition.buildScoreHolder(constraintMatchEnabled);
        // Determine which rules to enable based on the fact that their constraints carry weight.
        Score_ zeroScore = scoreDefinition.getZeroScore();
        Set<String> disabledConstraintIdSet = new LinkedHashSet<>(0);
        compiledRuleToConstraintMap.forEach((compiledRule, constraint) -> {
            Score_ constraintWeight = (Score_) constraint.extractConstraintWeight(workingSolution);
            scoreHolder.configureConstraintWeight(compiledRule, constraintWeight);
            if (constraintWeight.equals(zeroScore)) {
                disabledConstraintIdSet.add(constraint.getConstraintId());
            }
        });
        // Determine the KieBase to use.
        if (disabledConstraintIdSet.isEmpty()) { // Shortcut; don't change the original KieBase.
            currentKieBase = originalKieBase;
            currentlyDisabledConstraintIdSet = null;
        } else if (!disabledConstraintIdSet.equals(currentlyDisabledConstraintIdSet)) {
            // Only rebuild the active KieBase when the set of disabled constraints changed.
            ModelImpl model = new ModelImpl().withGlobals(originalModel.getGlobals());
            constraintToModelRuleMap.forEach((constraintId, modelRule) -> {
                if (disabledConstraintIdSet.contains(constraintId)) {
                    return;
                }
                model.addRule(modelRule);
            });
            currentKieBase = buildKieBaseFromModel(model);
            currentlyDisabledConstraintIdSet = disabledConstraintIdSet;
        }
        // Create the session itself.
        KieSession kieSession = currentKieBase.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener()); // Enables undo in rules.
        kieSession.setGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY, scoreHolder);
        return new DroolsConstraintSession<>(solutionDescriptor, kieSession, scoreHolder);
    }

}
