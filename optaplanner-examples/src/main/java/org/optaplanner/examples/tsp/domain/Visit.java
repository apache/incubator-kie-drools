package org.optaplanner.examples.tsp.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.solver.DomicileAngleVisitDifficultyWeightFactory;
import org.optaplanner.examples.tsp.domain.solver.DomicileDistanceStandstillStrengthWeightFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity(difficultyWeightFactoryClass = DomicileAngleVisitDifficultyWeightFactory.class)
public class Visit extends AbstractPersistable implements Standstill {

    private Location location;

    // Planning variables: changes during planning, between score calculations.
    private Standstill previousStandstill;

    public Visit() {
    }

    public Visit(long id, Location location) {
        super(id);
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @PlanningVariable(graphType = PlanningVariableGraphType.CHAINED,
            strengthWeightFactoryClass = DomicileDistanceStandstillStrengthWeightFactory.class)
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @JsonIgnore
    public long getDistanceFromPreviousStandstill() {
        if (previousStandstill == null) {
            return 0L;
        }
        return getDistanceFrom(previousStandstill);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @JsonIgnore
    public long getDistanceFrom(Standstill standstill) {
        return standstill.getLocation().getDistanceTo(location);
    }

    /**
     * @param standstill never null
     * @return a positive number, the distance multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    @Override
    public long getDistanceTo(Standstill standstill) {
        return location.getDistanceTo(standstill.getLocation());
    }

    @Override
    public String toString() {
        if (location.getName() == null) {
            return super.toString();
        }
        return location.getName();
    }

}
