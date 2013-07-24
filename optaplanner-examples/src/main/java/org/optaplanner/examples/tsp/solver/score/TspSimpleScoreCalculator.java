/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.examples.tsp.solver.score;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

public class TspSimpleScoreCalculator implements SimpleScoreCalculator<TravelingSalesmanTour> {

    public SimpleScore calculateScore(TravelingSalesmanTour tour) {
        List<Visit> visitList = tour.getVisitList();
        Set<Visit> tailVisitSet = new HashSet<Visit>(visitList);
        int score = 0;
        for (Visit visit : visitList) {
            Standstill previousStandstill = visit.getPreviousStandstill();
            if (previousStandstill != null) {
                score -= visit.getDistanceToPreviousStandstill();
                if (previousStandstill instanceof Visit) {
                    tailVisitSet.remove(previousStandstill);
                }
            }
        }
        // TODO support more than 1 domicile
        if (tour.getDomicileList().size() != 1) {
            throw new UnsupportedOperationException(
                    "The domicileList (" + tour.getDomicileList() + ") should be a singleton.");
        }
        Domicile domicile = tour.getDomicileList().get(0);
        for (Visit tailVisit : tailVisitSet) {
            if (tailVisit.getPreviousStandstill() != null) {
                score -= domicile.getCity().getDistance(tailVisit.getCity());
            }
        }
        return SimpleScore.valueOf(score);
    }

}
