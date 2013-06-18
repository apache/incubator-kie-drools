package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.vehiclerouting.domain.VrpDepot;

@XStreamAlias("VrpTimeWindowedDepot")
public class VrpTimeWindowedDepot extends VrpDepot {

    private int readyTime;
    private int dueTime;

    public int getReadyTime() {
        return readyTime;
    }

    public void setReadyTime(int readyTime) {
        this.readyTime = readyTime;
    }

    public int getDueTime() {
        return dueTime;
    }

    public void setDueTime(int dueTime) {
        this.dueTime = dueTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getTimeWindowLabel() {
        return readyTime + "-" + dueTime;
    }

}
