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

package org.optaplanner.core.impl.score.stream.common;

import java.util.Arrays;

public abstract class AbstractConstraintStreamHelper<Right, JoinedStream, Joiner, Predicate> {

    protected abstract JoinedStream doJoin(Class<Right> otherClass);

    protected abstract JoinedStream doJoin(Class<Right> otherClass, Joiner joiner);

    protected abstract JoinedStream doJoin(Class<Right> otherClass, Joiner... joiners);

    protected abstract JoinedStream filter(JoinedStream stream, Predicate predicate);

    protected abstract Joiner mergeJoiners(Joiner... joiners);

    protected abstract boolean isFilteringJoiner(Joiner joiner);

    protected abstract Predicate extractPredicate(Joiner joiner);

    protected abstract Predicate mergePredicates(Predicate predicate1, Predicate predicate2);

    public final JoinedStream join(Class<Right> otherClass, Joiner... joiners) {
        int joinerCount = joiners.length;
        int indexOfFirstFilter = -1;
        // Make sure all indexing joiners, if any, come before filtering joiners. This is necessary for performance.
        for (int i = 0; i < joinerCount; i++) {
            Joiner joiner = joiners[i];
            if (indexOfFirstFilter >= 0) {
                if (!isFilteringJoiner(joiner)) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                            "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                            "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
                }
            } else {
                if (isFilteringJoiner(joiner)) {
                    // From now on, we only allow filtering joiners.
                    indexOfFirstFilter = i;
                }
            }
        }
        if (indexOfFirstFilter < 0) { // Only found indexing joiners.
            Joiner mergedJoiners = mergeJoiners(joiners);
            return doJoin(otherClass, mergedJoiners);
        }
        // Assemble the join stream that may be followed by filter stream.
        JoinedStream joined = indexOfFirstFilter == 0 ? doJoin(otherClass)
                : doJoin(otherClass, Arrays.copyOf(joiners, indexOfFirstFilter));
        int filterCount = joinerCount - indexOfFirstFilter;
        if (filterCount == 0) { // No filters, return the original join stream.
            return joined;
        }
        // Merge all filters into one to avoid paying the penalty for lack of indexing more than once.
        Joiner filteringJoiner = joiners[indexOfFirstFilter];
        Predicate resultingFilter = extractPredicate(filteringJoiner);
        for (int i = indexOfFirstFilter + 1; i < joinerCount; i++) {
            Joiner otherFilteringJoiner = joiners[i];
            Predicate otherFilter = extractPredicate(otherFilteringJoiner);
            resultingFilter = mergePredicates(resultingFilter, otherFilter);
        }
        return filter(joined, resultingFilter);
    }

}
