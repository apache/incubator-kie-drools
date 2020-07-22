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

import java.util.List;
import java.util.Map;

import org.drools.model.PatternDSL.PatternDef;
import org.drools.model.Variable;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

final class TriJoinMutator<A, B, C> implements JoinMutator<BiRuleAssembler, TriRuleAssembler> {

    private final AbstractTriJoiner<A, B, C> joiner;

    public TriJoinMutator(AbstractConstraintModelJoiningNode<C, AbstractTriJoiner<A, B, C>> node) {
        this.joiner = node.get().get(0);
    }

    @Override
    public TriRuleAssembler apply(BiRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler) {
        TriRuleAssembler triRuleAssembler = merge(leftRuleAssembler, rightRuleAssembler);
        triRuleAssembler.addFilterToLastPrimaryPattern((a, b, c) -> joiner.matches((A) a, (B) b, (C) c));
        return triRuleAssembler;
    }

    @Override
    public TriRuleAssembler newRuleAssembler(BiRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        return new TriRuleAssembler(leftRuleAssembler::generateNextId,
                Math.max(leftRuleAssembler.getExpectedGroupByCount(), rightRuleAssembler.getExpectedGroupByCount()),
                finishedExpressions, variables, primaryPatterns, dependentExpressionMap);
    }

}
