/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;

interface JoinMutator<LeftAssembler_ extends AbstractRuleAssembler, ResultAssembler_ extends AbstractRuleAssembler>
        extends BiFunction<LeftAssembler_, UniRuleAssembler, ResultAssembler_> {

    default ResultAssembler_ merge(LeftAssembler_ leftRuleAssembler, UniRuleAssembler rightRuleAssembler) {
        leftRuleAssembler.applyFilterToLastPrimaryPattern();
        rightRuleAssembler.applyFilterToLastPrimaryPattern();
        List<ViewItem> newFinishedExpressions = new ArrayList<>(leftRuleAssembler.getFinishedExpressions());
        newFinishedExpressions.addAll(rightRuleAssembler.getFinishedExpressions());
        List<Variable> newVariables = new ArrayList<>(leftRuleAssembler.getVariables());
        newVariables.addAll(rightRuleAssembler.getVariables());
        List<PatternDef> newPrimaryPatterns = new ArrayList<>(leftRuleAssembler.getPrimaryPatterns());
        Map<Integer, List<ViewItem>> newDependentExpressionMap = new HashMap<>(leftRuleAssembler.getDependentExpressionMap());
        int startingPatternId = newPrimaryPatterns.size();
        for (int i = 0; i < rightRuleAssembler.getPrimaryPatterns().size(); i++) {
            newPrimaryPatterns.add(rightRuleAssembler.getPrimaryPatterns().get(i));
            int newPatternId = startingPatternId + i;
            newDependentExpressionMap
                    .put(newPatternId, rightRuleAssembler.getDependentExpressionMap().getOrDefault(i, new ArrayList<>(0)));
        }
        return newRuleAssembler(leftRuleAssembler, rightRuleAssembler, newFinishedExpressions, newVariables,
                newPrimaryPatterns, newDependentExpressionMap);
    }

    ResultAssembler_ newRuleAssembler(LeftAssembler_ leftRuleAssembler, UniRuleAssembler rightRuleAssembler,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap);

}
