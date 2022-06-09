package org.optaplanner.examples.tsp.domain.solver;

import java.util.Comparator;

import org.optaplanner.examples.tsp.domain.Visit;

public class LongitudeVisitDifficultyComparator implements Comparator<Visit> {

    // TODO experiment with (aLongitude - bLongitude) % 10
    private static final Comparator<Visit> COMPARATOR = Comparator
            .comparingDouble((Visit visit) -> visit.getLocation().getLongitude())
            .thenComparingDouble(visit -> visit.getLocation().getLatitude())
            .thenComparingLong(Visit::getId);

    @Override
    public int compare(Visit a, Visit b) {
        return COMPARATOR.compare(a, b);
    }

}
