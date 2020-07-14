package org.acme.facilitylocation.domain;

import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class FacilityLocationProblem {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "facilityRange")
    private List<Facility> facilities;
    @PlanningEntityCollectionProperty
    private List<Consumer> consumers;

    @PlanningScore
    private HardSoftLongScore score;
    @ConstraintConfigurationProvider
    private FacilityLocationConstraintConfiguration constraintConfiguration = new FacilityLocationConstraintConfiguration();

    private Location southWestCorner;
    private Location northEastCorner;

    public FacilityLocationProblem() {
    }

    public FacilityLocationProblem(
            List<Facility> facilities,
            List<Consumer> consumers,
            Location southWestCorner,
            Location northEastCorner) {
        this.facilities = facilities;
        this.consumers = consumers;
        this.southWestCorner = southWestCorner;
        this.northEastCorner = northEastCorner;
    }

    public static FacilityLocationProblem empty() {
        FacilityLocationProblem problem = new FacilityLocationProblem(
                emptyList(),
                emptyList(),
                new Location(-90, -180),
                new Location(90, 180));
        problem.setScore(HardSoftLongScore.ZERO);
        return problem;
    }

    public List<Facility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
    }

    public List<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<Consumer> consumers) {
        this.consumers = consumers;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    public FacilityLocationConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }

    public void setConstraintConfiguration(FacilityLocationConstraintConfiguration constraintConfiguration) {
        this.constraintConfiguration = constraintConfiguration;
    }

    public List<Location> getBounds() {
        return Arrays.asList(southWestCorner, northEastCorner);
    }

    public long getTotalCost() {
        return facilities.stream()
                .filter(Facility::isUsed)
                .mapToLong(Facility::getSetupCost)
                .sum();
    }

    public long getPotentialCost() {
        return facilities.stream()
                .mapToLong(Facility::getSetupCost)
                .sum();
    }

    public String getTotalDistance() {
        long distance = consumers.stream()
                .filter(Consumer::isAssigned)
                .mapToLong(Consumer::distanceFromFacility)
                .sum();
        return distance / 1000 + " km";
    }

    @Override
    public String toString() {
        return "FacilityLocationProblem{" +
                "facilities: " + facilities.size() +
                ", consumers: " + consumers.size() +
                ", score: " + score +
                '}';
    }
}
