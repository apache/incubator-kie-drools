/*
 * Copyright 2012 JBoss Inc
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

package org.optaplanner.examples.vehiclerouting.domain.timewindowed;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;

@XStreamAlias("VrpTimeWindowedCustomer")
public class VrpTimeWindowedCustomer extends VrpCustomer {

    private int readyTime;
    private int dueTime;
    private int serviceDuration;

    // Shadow variable
    private Integer arrivalTime;

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

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(int serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public Integer getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Integer arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getTimeWindowLabel() {
        return readyTime + "-" + dueTime;
    }

    public boolean isArrivalTimeValid() {
        return arrivalTime != null
                && readyTime <= arrivalTime
                && arrivalTime + serviceDuration <= dueTime;
    }

}
