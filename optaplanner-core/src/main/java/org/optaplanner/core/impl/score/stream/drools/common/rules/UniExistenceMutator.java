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

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.exists;
import static org.drools.model.DSL.not;
import static org.drools.model.PatternDSL.betaIndexedBy;
import static org.drools.model.PatternDSL.pattern;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.drools.model.BetaIndex;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Predicate2;
import org.drools.model.view.ExprViewItem;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.FilteringBiJoiner;
import org.optaplanner.core.impl.score.stream.bi.NoneBiJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;

final class UniExistenceMutator<A, B> implements Mutator {

    private final boolean shouldExist;
    private final Class<B> otherFactType;
    private final AbstractBiJoiner<A, B>[] joiners;

    public UniExistenceMutator(AbstractConstraintModelJoiningNode<B, AbstractBiJoiner<A, B>> node,
            boolean shouldExist) {
        this.shouldExist = shouldExist;
        this.otherFactType = node.getOtherFactType();
        this.joiners = node.get().stream()
                .toArray(AbstractBiJoiner[]::new);
    }

    private AbstractRuleAssembler applyJoiners(AbstractRuleAssembler ruleAssembler, AbstractBiJoiner<A, B> joiner,
            BiPredicate<A, B> predicate) {
        PatternDSL.PatternDef<A> primaryPattern = ruleAssembler.getLastPrimaryPattern();
        Variable<B> toExist = declarationOf(otherFactType, ruleAssembler.generateNextId("toExist"));
        PatternDSL.PatternDef<B> existencePattern = pattern(toExist);
        if (joiner == null) {
            return applyFilters(ruleAssembler, existencePattern, predicate);
        }
        JoinerType[] joinerTypes = joiner.getJoinerTypes();
        // Rebuild the A pattern, binding variables for left parts of the joins.
        Variable[] joinVars = new Variable[joinerTypes.length];
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind one join variable.
            int currentMappingIndex = mappingIndex;
            Variable<Object> joinVar = declarationOf(Object.class, "joinVar");
            Function<A, Object> leftMapping = joiner.getLeftMapping(currentMappingIndex);
            primaryPattern = primaryPattern.bind(joinVar, leftMapping::apply);
            joinVars[currentMappingIndex] = joinVar;
        }
        // Create the B pattern, joining with the new A pattern using its freshly bound join variables.
        for (int mappingIndex = 0; mappingIndex < joinerTypes.length; mappingIndex++) {
            // For each mapping, bind a join variable from A to B and index the binding.
            int currentMappingIndex = mappingIndex;
            JoinerType joinerType = joinerTypes[currentMappingIndex];
            Function<A, Object> leftMapping = joiner.getLeftMapping(currentMappingIndex);
            Function<B, Object> rightMapping = joiner.getRightMapping(currentMappingIndex);
            // Only extract B; A is coming from a pre-bound join var.
            Predicate2<B, A> joinPredicate = (b, a) -> joinerType.matches(a, rightMapping.apply(b));
            BetaIndex<B, A, ?> index = betaIndexedBy(Object.class, Mutator.getConstraintType(joinerType),
                    currentMappingIndex, rightMapping::apply, leftMapping::apply);
            existencePattern = existencePattern.expr("Join using joiner #" + currentMappingIndex + " in " + joiner,
                    joinVars[currentMappingIndex], joinPredicate, index);
        }
        // And finally add the filter to the B pattern.
        return applyFilters(ruleAssembler, existencePattern, predicate);
    }

    private AbstractRuleAssembler applyFilters(AbstractRuleAssembler ruleAssembler, PatternDSL.PatternDef<B> existencePattern,
            BiPredicate<A, B> biPredicate) {
        PatternDSL.PatternDef<B> possiblyFilteredExistencePattern = biPredicate == null ? existencePattern
                : existencePattern.expr("Filter using " + biPredicate, ruleAssembler.getVariable(0),
                        (b, a) -> biPredicate.test((A) a, b));
        ExprViewItem existenceExpression = exists(possiblyFilteredExistencePattern);
        if (!shouldExist) {
            existenceExpression = not(possiblyFilteredExistencePattern);
        }
        ruleAssembler.addDependentExpressionToLastPattern(existenceExpression);
        return ruleAssembler;
    }

    @Override
    public AbstractRuleAssembler apply(AbstractRuleAssembler ruleAssembler) {
        int indexOfFirstFilter = -1;
        // Prepare the joiner and filter that will be used in the pattern.
        AbstractBiJoiner<A, B> finalJoiner = null;
        BiPredicate<A, B> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractBiJoiner<A, B> biJoiner = joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (biJoiner instanceof NoneBiJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneBiJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(biJoiner instanceof FilteringBiJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + biJoiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? biJoiner : AbstractBiJoiner.merge(finalJoiner, biJoiner);
                }
            } else {
                if (!hasAFilter) { // From now on, only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? biJoiner.getFilter() : finalFilter.and(biJoiner.getFilter());
            }
        }
        return applyJoiners(ruleAssembler, finalJoiner, finalFilter);
    }

}
