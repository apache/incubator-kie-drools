package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.vehiclerouting.domain.Depot;

@XStreamAlias("VrpTimeWindowedDepot")
public class TimeWindowedDepot extends Depot {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private int milliReadyTime;
    private int milliDueTime;

    public int getMilliReadyTime() {
        return milliReadyTime;
    }

    public void setMilliReadyTime(int milliReadyTime) {
        this.milliReadyTime = milliReadyTime;
    }

    public int getMilliDueTime() {
        return milliDueTime;
    }

    public void setMilliDueTime(int milliDueTime) {
        this.milliDueTime = milliDueTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getTimeWindowLabel() {
        return milliReadyTime + "-" + milliDueTime;
    }

}
