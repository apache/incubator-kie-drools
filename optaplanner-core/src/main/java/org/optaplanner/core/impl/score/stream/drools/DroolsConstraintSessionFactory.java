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

import java.util.List;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.OptaPlannerRuleEventListener;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;

public class DroolsConstraintSessionFactory<Solution_> implements ConstraintSessionFactory<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final KieBase kieBase;
    private List<DroolsConstraint<Solution_>> constraintList;

    public DroolsConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase,
            List<DroolsConstraint<Solution_>> constraintList) {
        this.solutionDescriptor = solutionDescriptor;
        this.kieBase = kieBase;
        this.constraintList = constraintList;
    }

    @Override
    public ConstraintSession<Solution_> buildSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        ScoreDefinition scoreDefinition = solutionDescriptor.getScoreDefinition();
        AbstractScoreHolder scoreHolder = (AbstractScoreHolder) scoreDefinition.buildScoreHolder(constraintMatchEnabled);
        for (DroolsConstraint<Solution_> constraint : constraintList) {
            Score<?> constraintWeight = constraint.extractConstraintWeight(workingSolution);
            Rule rule = kieBase.getRule(constraint.getConstraintPackage(), constraint.getConstraintName());
            scoreHolder.configureConstraintWeight(rule, constraintWeight);
        }
        KieSession kieSession = kieBase.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener()); // Enables undo in rules
        kieSession.setGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY, scoreHolder);
        return new DroolsConstraintSession<>(constraintMatchEnabled, kieSession, scoreHolder);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
