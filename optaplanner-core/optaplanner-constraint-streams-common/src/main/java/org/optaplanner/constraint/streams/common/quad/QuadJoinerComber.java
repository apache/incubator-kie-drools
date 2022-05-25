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

package org.optaplanner.constraint.streams.common.quad;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;

/**
 * Combs an array of {@link QuadJoiner} instances into a mergedJoiner and a mergedFiltering.
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public final class QuadJoinerComber<A, B, C, D> {

    public static <A, B, C, D> QuadJoinerComber<A, B, C, D> comb(QuadJoiner<A, B, C, D>[] joiners) {
        List<DefaultQuadJoiner<A, B, C, D>> defaultJoinerList = new ArrayList<>(joiners.length);
        List<QuadPredicate<A, B, C, D>> filteringList = new ArrayList<>(joiners.length);

        int indexOfFirstFilter = -1;
        // Make sure all indexing joiners, if any, come before filtering joiners. This is necessary for performance.
        for (int i = 0; i < joiners.length; i++) {
            QuadJoiner<A, B, C, D> joiner = joiners[i];
            if (joiner instanceof FilteringQuadJoiner) {
                // From now on, only allow filtering joiners.
                indexOfFirstFilter = i;
                filteringList.add(((FilteringQuadJoiner<A, B, C, D>) joiner).getFilter());
            } else if (joiner instanceof DefaultQuadJoiner) {
                if (indexOfFirstFilter >= 0) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                            "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                            "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
                }
                defaultJoinerList.add((DefaultQuadJoiner<A, B, C, D>) joiner);
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        DefaultQuadJoiner<A, B, C, D> mergedJoiner = DefaultQuadJoiner.merge(defaultJoinerList);
        QuadPredicate<A, B, C, D> mergedFiltering = mergeFiltering(filteringList);
        return new QuadJoinerComber<>(mergedJoiner, mergedFiltering);
    }

    private static <A, B, C, D> QuadPredicate<A, B, C, D> mergeFiltering(List<QuadPredicate<A, B, C, D>> filteringList) {
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
                return (A a, B b, C c, D d) -> {
                    for (QuadPredicate<A, B, C, D> predicate : filteringList) {
                        if (!predicate.test(a, b, c, d)) {
                            return false;
                        }
                    }
                    return true;
                };
        }
    }

    private final DefaultQuadJoiner<A, B, C, D> mergedJoiner;
    private final QuadPredicate<A, B, C, D> mergedFiltering;

    public QuadJoinerComber(DefaultQuadJoiner<A, B, C, D> mergedJoiner, QuadPredicate<A, B, C, D> mergedFiltering) {
        this.mergedJoiner = mergedJoiner;
        this.mergedFiltering = mergedFiltering;
    }

    /**
     * @return never null
     */
    public DefaultQuadJoiner<A, B, C, D> getMergedJoiner() {
        return mergedJoiner;
    }

    /**
     * @return null if not applicable
     */
    public QuadPredicate<A, B, C, D> getMergedFiltering() {
        return mergedFiltering;
    }

}
