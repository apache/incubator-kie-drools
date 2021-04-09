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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.drools.ancompiler.KieBaseUpdaterANC;
import org.drools.model.Model;
import org.drools.model.impl.ModelImpl;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.DirectFiringOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;

public final class DroolsConstraintSessionFactory<Solution_, Score_ extends Score<Score_>> {

    private final ScoreDefinition<Score_> scoreDefinition;
    private final List<DroolsConstraint<Solution_>> constraintList;
    private final boolean droolsAlphaNetworkCompilationEnabled;

    private KieBaseCache<Solution_, Score_> kieBaseCache = null;

    public DroolsConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor,
            DroolsConstraintFactory<Solution_> constraintFactory, boolean droolsAlphaNetworkCompilationEnabled,
            Constraint... constraints) {
        this.scoreDefinition = solutionDescriptor.getScoreDefinition();
        this.constraintList = Arrays.stream(constraints)
                .map(constraint -> {
                    if (constraint.getConstraintFactory() != constraintFactory) {
                        throw new IllegalStateException("Impossible state: The constraint (" +
                                constraint.getConstraintId() + ") created by the wrong factory.");
                    }
                    return (DroolsConstraint<Solution_>) constraint;
                }).collect(Collectors.toList());
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
    }

    private static KieBase buildKieBaseFromModel(Model model, boolean droolsAlphaNetworkCompilationEnabled) {
        KieBaseConfiguration kieBaseConfiguration = KieServices.get().newKieBaseConfiguration();
        kieBaseConfiguration.setOption(KieBaseMutabilityOption.DISABLED); // For performance; applicable to DRL too.
        kieBaseConfiguration.setProperty(PropertySpecificOption.PROPERTY_NAME,
                PropertySpecificOption.DISABLED.name()); // Users of CS must not rely on underlying Drools gimmicks.
        KieBase kieBase = KieBaseBuilder.createKieBaseFromModel(model, kieBaseConfiguration);
        if (droolsAlphaNetworkCompilationEnabled) {
            KieBaseUpdaterANC.generateAndSetInMemoryANC(kieBase); // Enable Alpha Network Compiler for performance.
        }
        return kieBase;
    }

    private static KieSession buildKieSessionFromKieBase(KieBase kieBase) {
        KieSessionConfiguration config = KieServices.get().newKieSessionConfiguration();
        config.setOption(DirectFiringOption.YES); // For performance; not applicable to DRL due to insertLogical etc.
        Environment environment = KieServices.get().newEnvironment();
        return kieBase.newKieSession(config, environment);
    }

    public boolean isDroolsAlphaNetworkCompilationEnabled() {
        return droolsAlphaNetworkCompilationEnabled;
    }

    public SessionDescriptor<Score_> buildSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        Score_ zeroScore = scoreDefinition.getZeroScore();

        // Extract constraint weights, excluding constraints where weight is zero.
        Map<DroolsConstraint<Solution_>, Score_> constraintWeightMap = constraintList.stream()
                .map(constraint -> {
                    Object weight = constraint.extractConstraintWeight(workingSolution); // Expensive, only do once.
                    return new Object[] { constraint, weight };
                })
                .filter(constraintAndWeight -> !constraintAndWeight[1].equals(zeroScore)) // Exclude zero-weighted.
                .collect(Collectors.toMap(
                        constraintAndWeight -> (DroolsConstraint<Solution_>) constraintAndWeight[0],
                        constraintAndWeight -> (Score_) constraintAndWeight[1]));

        // Creating KieBase is expensive. Therefore we only do it when there has been a change in constraint weights.
        if (kieBaseCache == null || !kieBaseCache.isUpToDate(constraintWeightMap)) {
            ScoreInliner<Score_> scoreInliner = scoreDefinition.buildScoreInliner(constraintMatchEnabled);
            Model model = constraintWeightMap.entrySet().stream()
                    .map(entry -> entry.getKey().buildRule(scoreInliner, entry.getValue()))
                    .reduce(new ModelImpl(), ModelImpl::addRule, (m, __) -> m);
            KieBase kieBase = buildKieBaseFromModel(model, droolsAlphaNetworkCompilationEnabled);
            kieBaseCache = new KieBaseCache<>(constraintWeightMap, kieBase, scoreInliner);
        }

        // Create the session itself.
        KieSession kieSession = buildKieSessionFromKieBase(kieBaseCache.getKieBase());
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener()); // Enables undo in rules.
        return new SessionDescriptor<>(kieSession, kieBaseCache.getScoreInliner());
    }

    public static final class SessionDescriptor<Score_ extends Score<Score_>> {

        private final KieSession session;
        private final ScoreInliner<Score_> scoreInliner;

        public SessionDescriptor(KieSession session, ScoreInliner<Score_> scoreInliner) {
            this.session = session;
            this.scoreInliner = scoreInliner;
        }

        public KieSession getSession() {
            return session;
        }

        public ScoreInliner<Score_> getScoreInliner() {
            return scoreInliner;
        }
    }

    public static final class KieBaseCache<Solution_, Score_ extends Score<Score_>> {

        private final Map<DroolsConstraint<Solution_>, Score_> constraintWeightMap;
        private final KieBase kieBase;
        private final ScoreInliner<Score_> scoreInliner;

        public KieBaseCache(Map<DroolsConstraint<Solution_>, Score_> constraintWeightMap, KieBase kieBase,
                ScoreInliner<Score_> scoreInliner) {
            this.constraintWeightMap = Objects.requireNonNull(constraintWeightMap);
            this.kieBase = Objects.requireNonNull(kieBase);
            this.scoreInliner = Objects.requireNonNull(scoreInliner);
        }

        public boolean isUpToDate(Map<DroolsConstraint<Solution_>, Score_> currentConstraintWeightMap) {
            return constraintWeightMap.equals(currentConstraintWeightMap);
        }

        public KieBase getKieBase() {
            return kieBase;
        }

        public ScoreInliner<Score_> getScoreInliner() {
            return scoreInliner;
        }
    }

}
