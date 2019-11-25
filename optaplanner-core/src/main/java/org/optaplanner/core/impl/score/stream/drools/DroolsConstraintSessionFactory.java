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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieSession;
import org.kie.internal.event.rule.RuleEventManager;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.drools.OptaPlannerRuleEventListener;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.core.impl.score.stream.ConstraintSessionFactory;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsGroupByAccumulator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DroolsConstraintSessionFactory<Solution_> implements ConstraintSessionFactory<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final KieBase kieBase;
    private final Map<Rule, DroolsConstraint<Solution_>> constraints;

    public DroolsConstraintSessionFactory(SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase,
            List<DroolsConstraint<Solution_>> constraintList) {
        this.solutionDescriptor = solutionDescriptor;
        this.kieBase = kieBase;
        this.constraints = constraintList.stream()
                .collect(toMap(constraint -> kieBase.getRule(constraint.getConstraintPackage(),
                        constraint.getConstraintName()), Function.identity()));
    }

    @Override
    public ConstraintSession<Solution_> buildSession(boolean constraintMatchEnabled, Solution_ workingSolution) {
        ScoreDefinition scoreDefinition = solutionDescriptor.getScoreDefinition();
        AbstractScoreHolder scoreHolder = (AbstractScoreHolder) scoreDefinition.buildScoreHolder(constraintMatchEnabled);
        /*
         * Used to convert justification list to the same format as the one used by Bavet constraint streams.
         * This is necessary because CS-D uses some advanced Drools constructions leveraging various metadata objects.
         * Had we not called this converter, these metadata objects (such as Pair instances) would have been present in
         * the justification list, defeating its purpose.
         */
        scoreHolder.setJustificationListConverter((justificationList, rule) -> unpair((List<Object>) justificationList,
                constraints.get(rule).getConstraintStreamCardinality()));
        constraints.forEach((rule, constraint) -> scoreHolder.configureConstraintWeight(rule,
                constraint.extractConstraintWeight(workingSolution)));
        KieSession kieSession = kieBase.newKieSession();
        ((RuleEventManager) kieSession).addEventListener(new OptaPlannerRuleEventListener()); // Enables undo in rules
        kieSession.setGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY, scoreHolder);
        return new DroolsConstraintSession<>(constraintMatchEnabled, kieSession, scoreHolder);
    }

    private static List<Object> unpair(List<Object> justificationList, int expectedJustificationCount) {
        return justificationList.stream()
                .flatMap(item -> {
                    if (item instanceof DroolsGroupByAccumulator.Pair) {
                        /*
                         * In the case of Drools-based CS, the justification may be both in the form of (A, B) and
                         * Pair<A, B>. In the latter case, we adapt to the former.
                         */
                        DroolsGroupByAccumulator.Pair<?, ?> pair = (DroolsGroupByAccumulator.Pair<?, ?>) item;
                        return Stream.of(pair.key, pair.value);
                    } else {
                        return Stream.of(item);
                    }
                })
                .limit(expectedJustificationCount) // Match cardinality of the constraint stream.
                .collect(toList());
    }

}
