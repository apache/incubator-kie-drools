/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.examples.vehiclerouting.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.simple.SimpleScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpSchedule;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedSchedule;

public class VehicleRoutingSimpleScoreCalculator implements SimpleScoreCalculator<VrpSchedule> {

    public HardSoftScore calculateScore(VrpSchedule schedule) {
        boolean timeWindowed = schedule instanceof VrpTimeWindowedSchedule;
        List<VrpCustomer> customerList = schedule.getCustomerList();
        List<VrpVehicle> vehicleList = schedule.getVehicleList();
        Map<VrpVehicle, Integer> vehicleDemandMap = new HashMap<VrpVehicle, Integer>(vehicleList.size());
        for (VrpVehicle vehicle : vehicleList) {
            vehicleDemandMap.put(vehicle, 0);
        }
        int hardScore = 0;
        int softScore = 0;
        for (VrpCustomer customer : customerList) {
            VrpStandstill previousStandstill = customer.getPreviousStandstill();
            if (previousStandstill != null) {
                VrpVehicle vehicle = customer.getVehicle();
                vehicleDemandMap.put(vehicle, vehicleDemandMap.get(vehicle) + customer.getDemand());
                // Score constraint distanceToPreviousStandstill
                softScore -= customer.getMilliDistanceToPreviousStandstill();
                if (customer.getNextCustomer() == null) {
                    // Score constraint distanceFromLastCustomerToDepot
                    softScore -= vehicle.getLocation().getMilliDistance(customer.getLocation());
                }
                if (timeWindowed) {
                    VrpTimeWindowedCustomer timeWindowedCustomer = (VrpTimeWindowedCustomer) customer;
                    int milliReadyTime = timeWindowedCustomer.getMilliReadyTime();
                    int milliDueTime = timeWindowedCustomer.getMilliDueTime();
                    Integer milliArrivalTime = timeWindowedCustomer.getMilliArrivalTime();
                    if (milliDueTime < milliArrivalTime) {
                        // Score constraint arrivalAfterDueTime
                        hardScore -= (milliArrivalTime - milliDueTime);
                    }
                    if (milliArrivalTime < milliReadyTime) {
                        // Score constraint arrivalBeforeReadyTime
                        // Many external benchmark records tend to ignore this constraint.
                        // That heavily affects the attainable score.
                        softScore -= (milliReadyTime - milliArrivalTime);
                    }
                }
            }
        }
        for (Map.Entry<VrpVehicle, Integer> entry : vehicleDemandMap.entrySet()) {
            int capacity = entry.getKey().getCapacity();
            int demand = entry.getValue();
            if (demand > capacity) {
                // Score constraint vehicleCapacity
                hardScore -= (demand - capacity);
            }
        }
        return HardSoftScore.valueOf(hardScore, softScore);
    }

}
