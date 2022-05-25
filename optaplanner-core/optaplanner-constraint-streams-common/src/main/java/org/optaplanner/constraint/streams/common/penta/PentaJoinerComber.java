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

package org.optaplanner.constraint.streams.common.penta;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;

/**
 * Combs an array of {@link PentaJoiner} instances into a mergedJoiner and a mergedFiltering.
 * 
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 * @param <E>
 */
public final class PentaJoinerComber<A, B, C, D, E> {

    public static <A, B, C, D, E> PentaJoinerComber<A, B, C, D, E> comb(PentaJoiner<A, B, C, D, E>[] joiners) {
        List<DefaultPentaJoiner<A, B, C, D, E>> defaultJoinerList = new ArrayList<>(joiners.length);
        List<PentaPredicate<A, B, C, D, E>> filteringList = new ArrayList<>(joiners.length);

        int indexOfFirstFilter = -1;
        // Make sure all indexing joiners, if any, come before filtering joiners. This is necessary for performance.
        for (int i = 0; i < joiners.length; i++) {
            PentaJoiner<A, B, C, D, E> joiner = joiners[i];
            if (joiner instanceof FilteringPentaJoiner) {
                // From now on, only allow filtering joiners.
                indexOfFirstFilter = i;
                filteringList.add(((FilteringPentaJoiner<A, B, C, D, E>) joiner).getFilter());
            } else if (joiner instanceof DefaultPentaJoiner) {
                if (indexOfFirstFilter >= 0) {
                    throw new IllegalStateException("Indexing joiner (" + joiner + ") must not follow " +
                            "a filtering joiner (" + joiners[indexOfFirstFilter] + ").\n" +
                            "Maybe reorder the joiners such that filtering() joiners are later in the parameter list.");
                }
                defaultJoinerList.add((DefaultPentaJoiner<A, B, C, D, E>) joiner);
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        DefaultPentaJoiner<A, B, C, D, E> mergedJoiner = DefaultPentaJoiner.merge(defaultJoinerList);
        PentaPredicate<A, B, C, D, E> mergedFiltering = mergeFiltering(filteringList);
        return new PentaJoinerComber<>(mergedJoiner, mergedFiltering);
    }

    private static <A, B, C, D, E> PentaPredicate<A, B, C, D, E>
            mergeFiltering(List<PentaPredicate<A, B, C, D, E>> filteringList) {
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
                return (A a, B b, C c, D d, E e) -> {
                    for (PentaPredicate<A, B, C, D, E> predicate : filteringList) {
                        if (!predicate.test(a, b, c, d, e)) {
                            return false;
                        }
                    }
                    return true;
                };
        }
    }

    private final DefaultPentaJoiner<A, B, C, D, E> mergedJoiner;
    private final PentaPredicate<A, B, C, D, E> mergedFiltering;

    public PentaJoinerComber(DefaultPentaJoiner<A, B, C, D, E> mergedJoiner, PentaPredicate<A, B, C, D, E> mergedFiltering) {
        this.mergedJoiner = mergedJoiner;
        this.mergedFiltering = mergedFiltering;
    }

    /**
     * @return never null
     */
    public DefaultPentaJoiner<A, B, C, D, E> getMergedJoiner() {
        return mergedJoiner;
    }

    /**
     * @return null if not applicable
     */
    public PentaPredicate<A, B, C, D, E> getMergedFiltering() {
        return mergedFiltering;
    }

}
