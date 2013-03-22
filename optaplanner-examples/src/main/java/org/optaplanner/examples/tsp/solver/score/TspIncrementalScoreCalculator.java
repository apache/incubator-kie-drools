/*
 * Copyright 2012 JBoss Inc
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

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.tsp.domain.Appearance;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.domain.Visit;

public class TspIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<TravelingSalesmanTour> {

    private Domicile domicile;

    private int score;

    public void resetWorkingSolution(TravelingSalesmanTour tour) {
        if (tour.getDomicileList().size() != 1) {
            throw new UnsupportedOperationException(
                    "The domicileList (" + tour.getDomicileList() + ") should be a singleton.");
        }
        domicile = tour.getDomicileList().get(0);
        score = 0;
        for (Visit visit : tour.getVisitList()) {
            insert(visit);
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert((Visit) entity);
    }

    public void beforeAllVariablesChanged(Object entity) {
        retract((Visit) entity);
    }

    public void afterAllVariablesChanged(Object entity) {
        insert((Visit) entity);
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        retract((Visit) entity);
    }

    public void afterVariableChanged(Object entity, String variableName) {
        insert((Visit) entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract((Visit) entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Visit visit) {
        Appearance previousAppearance = visit.getPreviousAppearance();
        if (previousAppearance != null) {
            score -= visit.getDistanceToPreviousAppearance();
            // HACK: This counts too much, but the insert/retracts balance each other out
            score += domicile.getCity().getDistance(previousAppearance.getCity());
            score -= domicile.getCity().getDistance(visit.getCity());
        }
    }

    private void retract(Visit visit) {
        Appearance previousAppearance = visit.getPreviousAppearance();
        if (previousAppearance != null) {
            score += visit.getDistanceToPreviousAppearance();
            // HACK: This counts too much, but the insert/retracts balance each other out
            score -= domicile.getCity().getDistance(previousAppearance.getCity());
            score += domicile.getCity().getDistance(visit.getCity());
        }
    }

    public SimpleScore calculateScore() {
        return SimpleScore.valueOf(score);
    }

}
