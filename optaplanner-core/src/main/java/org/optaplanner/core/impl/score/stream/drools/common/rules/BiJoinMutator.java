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

import static org.drools.model.PatternDSL.PatternDef;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.declarationOf;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.drools.model.BetaIndex;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ViewItem;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;

final class BiJoinMutator<A, B> implements JoinMutator<UniRuleAssembler, BiRuleAssembler> {

    private final AbstractBiJoiner<A, B> biJoiner;

    public BiJoinMutator(AbstractConstraintModelJoiningNode<B, AbstractBiJoiner<A, B>> node) {
        this.biJoiner = node.get().get(0);
    }

    @Override
    public BiRuleAssembler apply(UniRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler) {
        JoinerType[] joinerTypes = biJoiner.getJoinerTypes();
        // Rebuild the A pattern, binding variables for left parts of the joins.
        PatternDef aJoiner = leftRuleAssembler.getLastPrimaryPattern();
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = declarationOf(Object.class, "joinVar" + currentMappingIndex);
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(currentMappingIndex);
            aJoiner.bind(joinVar, a -> leftMapping.apply((A) a));
            joinVars[currentMappingIndex] = joinVar;
        }
        PatternDef bJoiner = rightRuleAssembler.getLastPrimaryPattern();
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            JoinerType joinerType = joinerTypes[mappingIndex];
            Function<A, Object> leftMapping = biJoiner.getLeftMapping(mappingIndex);
            Function<B, Object> rightMapping = biJoiner.getRightMapping(mappingIndex);
            Function1<B, Object> rightExtractor = rightMapping::apply;
            // Only extract B; A is coming from a pre-bound join var.
            Predicate2<B, A> predicate = (b, a) -> joinerType.matches(a, rightExtractor.apply(b));
            BetaIndex<B, A, Object> index = betaIndexedBy(Object.class, Mutator.getConstraintType(joinerType),
                    mappingIndex, rightExtractor, leftMapping::apply);
            bJoiner.expr("Join using joiner #" + mappingIndex + " in " + biJoiner,
                    joinVars[mappingIndex], predicate, index);
        }
        return merge(leftRuleAssembler, rightRuleAssembler);
    }

    @Override
    public BiRuleAssembler newRuleAssembler(UniRuleAssembler leftRuleAssembler, UniRuleAssembler rightRuleAssembler,
            List<ViewItem> finishedExpressions, List<Variable> variables, List<PatternDef> primaryPatterns,
            Map<Integer, List<ViewItem>> dependentExpressionMap) {
        return new BiRuleAssembler(leftRuleAssembler::generateNextId,
                Math.max(leftRuleAssembler.getExpectedGroupByCount(), rightRuleAssembler.getExpectedGroupByCount()),
                finishedExpressions, variables, primaryPatterns, dependentExpressionMap);
    }

}
