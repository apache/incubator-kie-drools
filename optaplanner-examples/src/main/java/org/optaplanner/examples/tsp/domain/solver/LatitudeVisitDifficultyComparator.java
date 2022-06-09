package org.optaplanner.examples.tsp.domain.solver;

import java.util.Comparator;

import org.optaplanner.examples.tsp.domain.Visit;

public class LatitudeVisitDifficultyComparator implements Comparator<Visit> {

    // TODO experiment with (aLatitude - bLatitude) % 10
    private static final Comparator<Visit> COMPARATOR = Comparator
            .comparingDouble((Visit visit) -> visit.getLocation().getLatitude())
            .thenComparingDouble(visit -> visit.getLocation().getLongitude())
            .thenComparingLong(Visit::getId);

    @Override
    public int compare(Visit a, Visit b) {
        return COMPARATOR.compare(a, b);
    }
}
