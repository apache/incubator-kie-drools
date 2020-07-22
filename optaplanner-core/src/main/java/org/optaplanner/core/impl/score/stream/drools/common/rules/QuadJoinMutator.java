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
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;

final class QuadJoinMutator<A, B, C, D> implements JoinMutator<TriRuleAssembler, QuadRuleAssembler> {

    private final AbstractQuadJoiner<A, B, C, D> joiner;

    public QuadJoinMutator(AbstractConstraintModelJoiningNode<D, AbstractQuadJoiner<A, B, C, D>> node) {
        this.joiner = node.get().get(0);
    }

    @Override
    public QuadRuleAssembler apply(TriRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler) {
        QuadRuleAssembler quadRuleAssembler = merge(leftRuleAssembler, rightRuleAssembler);
        quadRuleAssembler.addFilterToLastPrimaryPattern((a, b, c, d) -> joiner.matches((A) a, (B) b, (C) c, (D) d));
        return quadRuleAssembler;
    }

    @Override
    public QuadRuleAssembler newRuleAssembler(TriRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        return new QuadRuleAssembler(leftRuleAssembler::generateNextId,
                Math.max(leftRuleAssembler.getExpectedGroupByCount(), rightRuleAssembler.getExpectedGroupByCount()),
                finishedExpressions, variables, primaryPatterns, dependentExpressionMap);
    }

}
