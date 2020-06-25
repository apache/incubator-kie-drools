/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
