package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.solver.ArrivalTimeUpdatingVariableListener;

@PlanningEntity
public class TimeWindowedCustomer extends Customer {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private long readyTime;
    private long dueTime;
    private long serviceDuration;

    // Shadow variable
    private Long arrivalTime;

    public TimeWindowedCustomer() {
    }

    public TimeWindowedCustomer(long id, Location location, int demand, long readyTime, long dueTime, long serviceDuration) {
        super(id, location, demand);
        this.readyTime = readyTime;
        this.dueTime = dueTime;
        this.serviceDuration = serviceDuration;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(long readyTime) {
        this.readyTime = readyTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getDueTime() {
        return dueTime;
    }

    public void setDueTime(long dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(long serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    // Arguable, to adhere to API specs (although this works), nextCustomer should also be a source,
    // because this shadow must be triggered after nextCustomer (but there is no need to be triggered by nextCustomer)
    @ShadowVariable(variableListenerClass = ArrivalTimeUpdatingVariableListener.class, sourceVariableName = "vehicle")
    @ShadowVariable(variableListenerClass = ArrivalTimeUpdatingVariableListener.class, sourceVariableName = "previousCustomer")
    public Long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public Long getDepartureTime() {
        if (arrivalTime == null) {
            return null;
        }
        return Math.max(arrivalTime, readyTime) + serviceDuration;
    }

    public boolean isArrivalBeforeReadyTime() {
        return arrivalTime != null
                && arrivalTime < readyTime;
    }

    public boolean isArrivalAfterDueTime() {
        return arrivalTime != null
                && dueTime < arrivalTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public long getTimeWindowGapTo(TimeWindowedCustomer other) {
        // dueTime doesn't account for serviceDuration
        long latestDepartureTime = dueTime + serviceDuration;
        long otherLatestDepartureTime = other.getDueTime() + other.getServiceDuration();
        if (latestDepartureTime < other.getReadyTime()) {
            return other.getReadyTime() - latestDepartureTime;
        }
        if (otherLatestDepartureTime < readyTime) {
            return readyTime - otherLatestDepartureTime;
        }
        return 0L;
    }

}
