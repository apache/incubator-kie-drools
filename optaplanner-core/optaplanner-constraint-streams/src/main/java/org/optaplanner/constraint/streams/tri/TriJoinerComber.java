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

package org.optaplanner.constraint.streams.tri;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

/**
 * Combs an array of {@link TriJoiner} instances into a mergedJoiner and a mergedFiltering.
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 */
public class TriJoinerComber<A, B, C> {

    public static <A, B, C> TriJoinerComber<A, B, C> comb(TriJoiner<A, B, C>[] joiners) {
        List<DefaultTriJoiner<A, B, C>> defaultJoinerList = new ArrayList<>(joiners.length);
        List<TriPredicate<A, B, C>> filteringList = new ArrayList<>(joiners.length);

        int indexOfFirstFilter = -1;
        // Make sure all indexing joiners, if any, come before filtering joiners. This is necessary for performance.
        for (int i = 0; i < joiners.length; i++) {
            TriJoiner<A, B, C> joiner = joiners[i];
            if (joiner instanceof FilteringTriJoiner) {
                // From now on, only allow filtering joiners.
                indexOfFirstFilter = i;
                filteringList.add(((FilteringTriJoiner<A, B, C>) joiner).getFilter());
            } else if (joiner instanceof DefaultTriJoiner) {
                if (indexOfFirstFilter >= 0) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                            "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                            "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
                }
                defaultJoinerList.add((DefaultTriJoiner<A, B, C>) joiner);
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        DefaultTriJoiner<A, B, C> mergedJoiner = DefaultTriJoiner.merge(defaultJoinerList);
        TriPredicate<A, B, C> mergedFiltering = mergeFiltering(filteringList);
        return new TriJoinerComber<>(mergedJoiner, mergedFiltering);
    }

    private static <A, B, C> TriPredicate<A, B, C> mergeFiltering(List<TriPredicate<A, B, C>> filteringList) {
        if (filteringList.size() == 0) {
            return null;
        } else if (filteringList.size() == 1) {
            return filteringList.get(0);
        } else if (filteringList.size() == 2) {
            return filteringList.get(0).and(filteringList.get(1));
        } else {
            // Avoid predicate.and() when more than 2 predicates for debugging and potentially performance
            TriPredicate<A, B, C>[] predicates = filteringList.toArray(TriPredicate[]::new);
            return (A a, B b, C c) -> {
                for (TriPredicate<A, B, C> predicate : predicates) {
                    if (!predicate.test(a, b, c)) {
                        return false;
                    }
                }
                return true;
            };
        }
    }

    private DefaultTriJoiner<A, B, C> mergedJoiner;
    private TriPredicate<A, B, C> mergedFiltering;

    public TriJoinerComber(DefaultTriJoiner<A, B, C> mergedJoiner, TriPredicate<A, B, C> mergedFiltering) {
        this.mergedJoiner = mergedJoiner;
        this.mergedFiltering = mergedFiltering;
    }

    /**
     * @return never null
     */
    public DefaultTriJoiner<A, B, C> getMergedJoiner() {
        return mergedJoiner;
    }

    /**
     * @return null if not applicable
     */
    public TriPredicate<A, B, C> getMergedFiltering() {
        return mergedFiltering;
    }

}
