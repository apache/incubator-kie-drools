/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;

public class DomicileDistanceStandstillStrengthWeightFactory implements SelectionSorterWeightFactory<TspSolution, Standstill> {

    @Override
    public DomicileDistanceStandstillStrengthWeight createSorterWeight(TspSolution tspSolution, Standstill standstill) {
        Domicile domicile = tspSolution.getDomicile();
        long domicileRoundTripDistance = domicile.getDistanceTo(standstill) + standstill.getDistanceTo(domicile);
        return new DomicileDistanceStandstillStrengthWeight(standstill, domicileRoundTripDistance);
    }

    public static class DomicileDistanceStandstillStrengthWeight implements Comparable<DomicileDistanceStandstillStrengthWeight> {

        private static final Comparator<DomicileDistanceStandstillStrengthWeight> COMPARATOR =
                // Decreasing: closer to depot is stronger
                Comparator.comparingLong((DomicileDistanceStandstillStrengthWeight weight) -> -weight.domicileRoundTripDistance)
                        .thenComparingDouble(weight -> weight.standstill.getLocation().getLatitude())
                        .thenComparingDouble(weight -> weight.standstill.getLocation().getLongitude());

        private final Standstill standstill;
        private final long domicileRoundTripDistance;

        public DomicileDistanceStandstillStrengthWeight(Standstill standstill, long domicileRoundTripDistance) {
            this.standstill = standstill;
            this.domicileRoundTripDistance = domicileRoundTripDistance;
        }

        @Override
        public int compareTo(DomicileDistanceStandstillStrengthWeight other) {
            return COMPARATOR.compare(this, other);
        }

    }

}
