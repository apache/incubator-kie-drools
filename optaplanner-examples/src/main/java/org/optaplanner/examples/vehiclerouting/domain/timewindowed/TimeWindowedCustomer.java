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
import org.optaplanner.examples.vehiclerouting.domain.Customer;

@XStreamAlias("VrpTimeWindowedCustomer")
public class TimeWindowedCustomer extends Customer {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private int milliReadyTime;
    private int milliDueTime;
    private int milliServiceDuration;

    // Shadow variable
    private Integer milliArrivalTime;

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

    public int getMilliServiceDuration() {
        return milliServiceDuration;
    }

    public void setMilliServiceDuration(int milliServiceDuration) {
        this.milliServiceDuration = milliServiceDuration;
    }

    public Integer getMilliArrivalTime() {
        return milliArrivalTime;
    }

    public void setMilliArrivalTime(Integer milliArrivalTime) {
        this.milliArrivalTime = milliArrivalTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getTimeWindowLabel() {
        return milliReadyTime + "-" + milliDueTime;
    }

    public Integer getDepartureTime() {
        if (milliArrivalTime == null) {
            return null;
        }
        return Math.max(milliArrivalTime, milliReadyTime) + milliServiceDuration;
    }

    public boolean isArrivalBeforeReadyTime() {
        return milliArrivalTime != null
                && milliArrivalTime < milliReadyTime;
    }

    public boolean isArrivalAfterDueTime() {
        return milliArrivalTime != null
                && milliDueTime < milliArrivalTime;
    }

    @Override
    public TimeWindowedCustomer getNextCustomer() {
        return (TimeWindowedCustomer) super.getNextCustomer();
    }

}
