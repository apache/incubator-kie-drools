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

/**
 * On large datasets, the constructed solution looks like pizza slices.
 */
public class DomicileAngleVisitDifficultyWeightFactory
        implements SelectionSorterWeightFactory<TspSolution, Visit> {

    @Override
    public DomicileAngleVisitDifficultyWeight createSorterWeight(TspSolution vehicleRoutingSolution, Visit visit) {
        Domicile domicile = vehicleRoutingSolution.getDomicile();
        return new DomicileAngleVisitDifficultyWeight(visit,
                visit.getLocation().getAngle(domicile.getLocation()),
                visit.getLocation().getDistanceTo(domicile.getLocation())
                        + domicile.getLocation().getDistanceTo(visit.getLocation()));
    }

    public static class DomicileAngleVisitDifficultyWeight
            implements Comparable<DomicileAngleVisitDifficultyWeight> {

        private static final Comparator<DomicileAngleVisitDifficultyWeight> COMPARATOR =
                comparingDouble((DomicileAngleVisitDifficultyWeight weight) -> weight.domicileAngle)
                        .thenComparingLong(weight -> weight.domicileRoundTripDistance) // Ascending (further from the depot are more difficult)
                        .thenComparing(weight -> weight.visit, comparingLong(Visit::getId));

        private final Visit visit;
        private final double domicileAngle;
        private final long domicileRoundTripDistance;

        public DomicileAngleVisitDifficultyWeight(Visit visit,
                double domicileAngle, long domicileRoundTripDistance) {
            this.visit = visit;
            this.domicileAngle = domicileAngle;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileAngleVisitDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
