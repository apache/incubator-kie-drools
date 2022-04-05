/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public abstract class AbstractConstraintStreamHelper<Right, JoinedStream, Joiner, Predicate> {

    protected abstract JoinedStream doJoin(UniConstraintStream<Right> otherStream, List<Joiner> joiners);

    protected abstract JoinedStream filter(JoinedStream stream, Predicate predicate);

    protected abstract boolean isFilteringJoiner(Joiner joiner);

    protected abstract Predicate extractPredicate(Joiner joiner);

    protected abstract Predicate mergePredicates(Predicate predicate1, Predicate predicate2);

    /**
     * Converts a set of joiners into a single composite joiner, optionally followed by some filters.
     * Ensures that the joiners are properly ordered filters-last.
     *
     * @param otherStream never null
     * @param joiners never null
     * @return never null, stream with all the joiners applied
     */
    public final JoinedStream join(UniConstraintStream<Right> otherStream, Joiner... joiners) {
        int joinerCount = joiners.length;
        if (joinerCount == 0) {
            return doJoin(otherStream, Arrays.asList(joiners));
        }
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
            return doJoin(otherStream, Arrays.asList(joiners));
        }
        // Assemble the join stream that may be followed by filter stream.
        JoinedStream joined = indexOfFirstFilter == 0 ? doJoin(otherStream, Collections.emptyList())
                : doJoin(otherStream, Arrays.asList(Arrays.copyOf(joiners, indexOfFirstFilter)));
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
