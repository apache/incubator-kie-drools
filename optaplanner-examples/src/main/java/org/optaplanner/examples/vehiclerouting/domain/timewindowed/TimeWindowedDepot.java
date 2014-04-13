package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.vehiclerouting.domain.Depot;

@XStreamAlias("VrpTimeWindowedDepot")
public class TimeWindowedDepot extends Depot {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private int readyTime;
    private int dueTime;

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    /**
     * @return a positive number, the time multiplied by 1000 to avoid floating point arithmetic rounding errors
     */
    public int getDueTime() {
        return dueTime;
    }

    public void setDueTime(int dueTime) {
        this.dueTime = dueTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
