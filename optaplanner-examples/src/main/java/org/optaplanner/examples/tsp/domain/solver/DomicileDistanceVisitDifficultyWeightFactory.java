/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.tsp.domain.solver;

import java.util.Comparator;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;

import static java.util.Comparator.*;

public class DomicileDistanceVisitDifficultyWeightFactory implements SelectionSorterWeightFactory<TspSolution, Visit> {

    @Override
    public DomicileDistanceVisitDifficultyWeight createSorterWeight(TspSolution tspSolution, Visit visit) {
        Domicile domicile = tspSolution.getDomicile();
        long domicileRoundTripDistance = domicile.getDistanceTo(visit) + visit.getDistanceTo(domicile);
        return new DomicileDistanceVisitDifficultyWeight(visit, domicileRoundTripDistance);
    }

    public static class DomicileDistanceVisitDifficultyWeight implements Comparable<DomicileDistanceVisitDifficultyWeight> {

        private static final Comparator<DomicileDistanceVisitDifficultyWeight> COMPARATOR =
                // Decreasing: closer to depot is stronger
                comparingLong((DomicileDistanceVisitDifficultyWeight weight) -> -weight.domicileRoundTripDistance)
                        .thenComparingDouble(weight -> weight.visit.getLocation().getLatitude())
                        .thenComparing(weight -> weight.visit, comparingLong(Visit::getId));

        private final Visit visit;
        private final long domicileRoundTripDistance;

        public DomicileDistanceVisitDifficultyWeight(Visit visit, long domicileRoundTripDistance) {
            this.visit = visit;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileDistanceVisitDifficultyWeight other) {
            return COMPARATOR.compare(this,other);
        }

    }

}
