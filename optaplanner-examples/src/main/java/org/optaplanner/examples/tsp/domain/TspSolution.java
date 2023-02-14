package org.optaplanner.examples.tsp.domain;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningSolution
public class TspSolution extends AbstractPersistable {

    private String name;
    protected DistanceType distanceType;
    protected String distanceUnitOfMeasurement;
    private List<Location> locationList;
    private Domicile domicile;

    private List<Visit> visitList;

    private SimpleLongScore score;

    public TspSolution() {
    }

    public TspSolution(long id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DistanceType getDistanceType() {
        return distanceType;
    }

    public void setDistanceType(DistanceType distanceType) {
        this.distanceType = distanceType;
    }

    public String getDistanceUnitOfMeasurement() {
        return distanceUnitOfMeasurement;
    }

    public void setDistanceUnitOfMeasurement(String distanceUnitOfMeasurement) {
        this.distanceUnitOfMeasurement = distanceUnitOfMeasurement;
    }

    @ProblemFactCollectionProperty
    public List<Location> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
    }

    @ProblemFactProperty
    public Domicile getDomicile() {
        return domicile;
    }

    public void setDomicile(Domicile domicile) {
        this.domicile = domicile;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    @PlanningScore
    public SimpleLongScore getScore() {
        return score;
    }

    public void setScore(SimpleLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ValueRangeProvider
    @JsonIgnore
    public List<Domicile> getDomicileRange() {
        return Collections.singletonList(domicile);
    }

    @JsonIgnore
    public String getDistanceString(NumberFormat numberFormat) {
        if (score == null) {
            return null;
        }
        long distance = -score.score();
        if (distanceUnitOfMeasurement == null) {
            return numberFormat.format(distance / 1000.0);
        }
        switch (distanceUnitOfMeasurement) {
            case "sec": // TODO why are the values 1000 larger?
                long hours = distance / 3600000;
                long minutes = distance % 3600000 / 60000;
                long seconds = distance % 60000 / 1000;
                long milliseconds = distance % 1000;
                return hours + "h " + minutes + "m " + seconds + "s " + milliseconds + "ms";
            case "km": { // TODO why are the values 1000 larger?
                long km = distance / 1000;
                long meter = distance % 1000;
                return km + "km " + meter + "m";
            }
            case "meter": {
                long km = distance / 1000;
                long meter = distance % 1000;
                return km + "km " + meter + "m";
            }
            default:
                return numberFormat.format(distance / 1000.0) + " " + distanceUnitOfMeasurement;
        }
    }

}
