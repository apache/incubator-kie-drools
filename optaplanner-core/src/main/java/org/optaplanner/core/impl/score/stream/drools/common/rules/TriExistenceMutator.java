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
import static org.drools.model.PatternDSL.pattern;

import java.util.Arrays;

import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.view.ExprViewItem;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.AbstractConstraintModelJoiningNode;
import org.optaplanner.core.impl.score.stream.quad.AbstractQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.FilteringQuadJoiner;
import org.optaplanner.core.impl.score.stream.quad.NoneQuadJoiner;
import org.optaplanner.core.impl.score.stream.tri.NoneTriJoiner;

final class TriExistenceMutator<A, B, C, D> implements Mutator {

    private final boolean shouldExist;
    private final Class<D> otherFactType;
    private final AbstractQuadJoiner<A, B, C, D>[] joiners;

    public TriExistenceMutator(AbstractConstraintModelJoiningNode<D, AbstractQuadJoiner<A, B, C, D>> node,
            boolean shouldExist) {
        this.shouldExist = shouldExist;
        this.otherFactType = node.getOtherFactType();
        this.joiners = node.get().stream()
                .toArray(AbstractQuadJoiner[]::new);
    }

    private AbstractRuleAssembler applyJoiners(AbstractRuleAssembler ruleAssembler, AbstractQuadJoiner<A, B, C, D> joiner,
            QuadPredicate<A, B, C, D> predicate) {
        if (joiner == null) {
            return applyFilters(ruleAssembler, predicate);
        }
        // There is no delta index in Drools, therefore we replace joining with a filter.
        QuadPredicate<A, B, C, D> joinFilter = joiner::matches;
        QuadPredicate<A, B, C, D> result = predicate == null ? joinFilter : joinFilter.and(predicate);
        // And finally we add the filter to the D pattern,
        return applyFilters(ruleAssembler, result);
    }

    private AbstractRuleAssembler applyFilters(AbstractRuleAssembler ruleAssembler, QuadPredicate<A, B, C, D> predicate) {
        Variable<D> toExist = declarationOf(otherFactType, ruleAssembler.generateNextId("triToExist"));
        PatternDSL.PatternDef<D> existencePattern = pattern(toExist);
        PatternDSL.PatternDef<D> possiblyFilteredExistencePattern = predicate == null ? existencePattern
                : existencePattern.expr("Filter using " + predicate, ruleAssembler.getVariable(0),
                        ruleAssembler.getVariable(1), ruleAssembler.getVariable(2),
                        (d, a, b, c) -> predicate.test((A) a, (B) b, (C) c, d));
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
        // Prepare the joiner and filter that will be used in the pattern
        AbstractQuadJoiner<A, B, C, D> finalJoiner = null;
        QuadPredicate<A, B, C, D> finalFilter = null;
        for (int i = 0; i < joiners.length; i++) {
            AbstractQuadJoiner<A, B, C, D> joiner = joiners[i];
            boolean hasAFilter = indexOfFirstFilter >= 0;
            if (joiner instanceof NoneQuadJoiner && joiners.length > 1) {
                throw new IllegalStateException("If present, " + NoneTriJoiner.class + " must be the only joiner, got "
                        + Arrays.toString(joiners) + " instead.");
            } else if (!(joiner instanceof FilteringQuadJoiner)) {
                if (hasAFilter) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow a filtering joiner ("
                            + joiners[indexOfFirstFilter] + ").");
                } else { // Merge this Joiner with the existing Joiners.
                    finalJoiner = finalJoiner == null ? joiner : AbstractQuadJoiner.merge(finalJoiner, joiner);
                }
            } else {
                if (!hasAFilter) { // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
                // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
                finalFilter = finalFilter == null ? joiner.getFilter() : finalFilter.and(joiner.getFilter());
            }
        }
        return applyJoiners(ruleAssembler, finalJoiner, finalFilter);
    }

}
