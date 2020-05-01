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

package org.optaplanner.examples.tsp.optional.score;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Visit;

public final class TspConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                distanceToPreviousStandstill(constraintFactory),
                distanceFromLastVisitToDomicile(constraintFactory)
        };
    }

    private Constraint distanceToPreviousStandstill(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Visit.class)
                .penalizeLong("Distance to previous standstill",
                        SimpleLongScore.ONE,
                        Visit::getDistanceFromPreviousStandstill);
    }

    private Constraint distanceFromLastVisitToDomicile(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Visit.class)
                .ifNotExists(Visit.class, Joiners.equal(visit -> visit, Visit::getPreviousStandstill))
                .join(Domicile.class)
                .penalizeLong("Distance from last visit to domicile",
                        SimpleLongScore.ONE,
                        Visit::getDistanceTo);
    }

}
