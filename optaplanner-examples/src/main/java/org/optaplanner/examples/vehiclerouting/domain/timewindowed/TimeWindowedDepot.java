package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;

public class TimeWindowedDepot extends Depot {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private long readyTime;
    private long dueTime;

    public TimeWindowedDepot() {
    }

    public TimeWindowedDepot(long id, Location location, long readyTime, long dueTime) {
        super(id, location);
        this.readyTime = readyTime;
        this.dueTime = dueTime;
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

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
