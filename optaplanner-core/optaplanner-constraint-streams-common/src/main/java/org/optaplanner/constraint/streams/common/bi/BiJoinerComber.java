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

package org.optaplanner.constraint.streams.common.bi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;

/**
 * Combs an array of {@link BiJoiner} instances into a mergedJoiner and a mergedFiltering.
 * 
 * @param <A>
 * @param <B>
 */
public final class BiJoinerComber<A, B> {

    public static <A, B> BiJoinerComber<A, B> comb(BiJoiner<A, B>[] joiners) {
        List<DefaultBiJoiner<A, B>> defaultJoinerList = new ArrayList<>(joiners.length);
        List<BiPredicate<A, B>> filteringList = new ArrayList<>(joiners.length);

        int indexOfFirstFilter = -1;
        // Make sure all indexing joiners, if any, come before filtering joiners. This is necessary for performance.
        for (int i = 0; i < joiners.length; i++) {
            BiJoiner<A, B> joiner = joiners[i];
            if (joiner instanceof FilteringBiJoiner) {
                // From now on, only allow filtering joiners.
                indexOfFirstFilter = i;
                filteringList.add(((FilteringBiJoiner<A, B>) joiner).getFilter());
            } else if (joiner instanceof DefaultBiJoiner) {
                if (indexOfFirstFilter >= 0) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                            "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                            "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
                }
                defaultJoinerList.add((DefaultBiJoiner<A, B>) joiner);
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        DefaultBiJoiner<A, B> mergedJoiner = DefaultBiJoiner.merge(defaultJoinerList);
        BiPredicate<A, B> mergedFiltering = mergeFiltering(filteringList);
        return new BiJoinerComber<>(mergedJoiner, mergedFiltering);
    }

    private static <A, B> BiPredicate<A, B> mergeFiltering(List<BiPredicate<A, B>> filteringList) {
        if (filteringList.isEmpty()) {
            return null;
        }
        switch (filteringList.size()) {
            case 1:
                return filteringList.get(0);
            case 2:
                return filteringList.get(0).and(filteringList.get(1));
            default:
                // Avoid predicate.and() when more than 2 predicates for debugging and potentially performance
                return (A a, B b) -> {
                    for (BiPredicate<A, B> predicate : filteringList) {
                        if (!predicate.test(a, b)) {
                            return false;
                        }
                    }
                    return true;
                };
        }
    }

    private DefaultBiJoiner<A, B> mergedJoiner;
    private final BiPredicate<A, B> mergedFiltering;

    public BiJoinerComber(DefaultBiJoiner<A, B> mergedJoiner, BiPredicate<A, B> mergedFiltering) {
        this.mergedJoiner = mergedJoiner;
        this.mergedFiltering = mergedFiltering;
    }

    /**
     * @return never null
     */
    public DefaultBiJoiner<A, B> getMergedJoiner() {
        return mergedJoiner;
    }

    /**
     * @return null if not applicable
     */
    public BiPredicate<A, B> getMergedFiltering() {
        return mergedFiltering;
    }

    public void addJoiner(DefaultBiJoiner<A, B> extraJoiner) {
        mergedJoiner = mergedJoiner.and(extraJoiner);
    }

}
