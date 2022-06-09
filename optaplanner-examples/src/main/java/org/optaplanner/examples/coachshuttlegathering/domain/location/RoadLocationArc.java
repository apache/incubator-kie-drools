package org.optaplanner.examples.coachshuttlegathering.domain.location;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("CsgRoadLocationArc")
public class RoadLocationArc {

    private int coachDistance;
    private int coachDuration;
    private int shuttleDistance;
    private int shuttleDuration;

    public RoadLocationArc() {
    }

    public int getCoachDistance() {
        return coachDistance;
    }

    public void setCoachDistance(int coachDistance) {
        this.coachDistance = coachDistance;
    }

    public int getCoachDuration() {
        return coachDuration;
    }

    public void setCoachDuration(int coachDuration) {
        this.coachDuration = coachDuration;
    }

    public int getShuttleDistance() {
        return shuttleDistance;
    }

    public void setShuttleDistance(int shuttleDistance) {
        this.shuttleDistance = shuttleDistance;
    }

    public int getShuttleDuration() {
        return shuttleDuration;
    }

    public void setShuttleDuration(int shuttleDuration) {
        this.shuttleDuration = shuttleDuration;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public String toString() {
        return "Road arc [coach: "
                + coachDistance + "m/" + coachDuration
                + "s, shuttle: "
                + shuttleDistance + "m/" + shuttleDuration + "s]";
    }

}
