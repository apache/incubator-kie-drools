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

package org.optaplanner.examples.vehiclerouting.solver.score;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator;
import org.optaplanner.examples.vehiclerouting.domain.VrpCustomer;
import org.optaplanner.examples.vehiclerouting.domain.VrpSchedule;
import org.optaplanner.examples.vehiclerouting.domain.VrpStandstill;
import org.optaplanner.examples.vehiclerouting.domain.VrpVehicle;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.VrpTimeWindowedSchedule;

public class VehicleRoutingIncrementalScoreCalculator extends AbstractIncrementalScoreCalculator<VrpSchedule> {

    private boolean timeWindowed;
    private Map<VrpVehicle, Integer> vehicleDemandMap;

    private int hardScore;
    private int softScore;

    public void resetWorkingSolution(VrpSchedule schedule) {
        timeWindowed = schedule instanceof VrpTimeWindowedSchedule;
        List<VrpVehicle> vehicleList = schedule.getVehicleList();
        vehicleDemandMap = new HashMap<VrpVehicle, Integer>(vehicleList.size());
        for (VrpVehicle vehicle : vehicleList) {
            vehicleDemandMap.put(vehicle, 0);
        }
        hardScore = 0;
        softScore = 0;
        for (VrpCustomer customer : schedule.getCustomerList()) {
            insertPreviousStandstill(customer);
            insertVehicle(customer);
            // Do not do insertNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
            if (timeWindowed) {
                insertMilliArrivalTime((VrpTimeWindowedCustomer) customer);
            }
        }
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        if (entity instanceof VrpVehicle) {
            return;
        }
        insertPreviousStandstill((VrpCustomer) entity);
        insertVehicle((VrpCustomer) entity);
        // Do not do insertNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
        if (timeWindowed) {
            insertMilliArrivalTime((VrpTimeWindowedCustomer) entity);
        }
    }

    public void beforeVariableChanged(Object entity, String variableName) {
        if (entity instanceof VrpVehicle) {
            return;
        }
        if (variableName.equals("previousStandstill")) {
            retractPreviousStandstill((VrpCustomer) entity);
        } else if (variableName.equals("vehicle"))   {
            retractVehicle((VrpCustomer) entity);
        } else if (variableName.equals("nextCustomer"))   {
            retractNextCustomer((VrpCustomer) entity);
        } else if (variableName.equals("milliArrivalTime"))   {
            retractMilliArrivalTime((VrpTimeWindowedCustomer) entity);
        } else {
            throw new IllegalArgumentException("Unsupported variableName (" + variableName + ").");
        }
    }

    public void afterVariableChanged(Object entity, String variableName) {
        if (entity instanceof VrpVehicle) {
            return;
        }
        if (variableName.equals("previousStandstill")) {
            insertPreviousStandstill((VrpCustomer) entity);
        } else if (variableName.equals("vehicle"))   {
            insertVehicle((VrpCustomer) entity);
        } else if (variableName.equals("nextCustomer"))   {
            insertNextCustomer((VrpCustomer) entity);
        } else if (variableName.equals("milliArrivalTime"))   {
            insertMilliArrivalTime((VrpTimeWindowedCustomer) entity);
        } else {
            throw new IllegalArgumentException("Unsupported variableName (" + variableName + ").");
        }
    }

    public void beforeEntityRemoved(Object entity) {
        if (entity instanceof VrpVehicle) {
            return;
        }
        retractPreviousStandstill((VrpCustomer) entity);
        retractVehicle((VrpCustomer) entity);
        // Do not do retractNextCustomer(customer) to avoid counting distanceFromLastCustomerToDepot twice
        if (timeWindowed) {
            retractMilliArrivalTime((VrpTimeWindowedCustomer) entity);
        }
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insertPreviousStandstill(VrpCustomer customer) {
        VrpStandstill previousStandstill = customer.getPreviousStandstill();
        if (previousStandstill != null) {
            // Score constraint distanceToPreviousStandstill
            softScore -= customer.getMilliDistanceToPreviousStandstill();
        }
    }

    private void retractPreviousStandstill(VrpCustomer customer) {
        VrpStandstill previousStandstill = customer.getPreviousStandstill();
        if (previousStandstill != null) {
            // Score constraint distanceToPreviousStandstill
            softScore += customer.getMilliDistanceToPreviousStandstill();
        }
    }

    private void insertVehicle(VrpCustomer customer) {
        VrpVehicle vehicle = customer.getVehicle();
        if (vehicle != null) {
            // Score constraint vehicleCapacity
            int capacity = vehicle.getCapacity();
            int oldDemand = vehicleDemandMap.get(vehicle);
            int newDemand = oldDemand + customer.getDemand();
            hardScore += Math.min(capacity - newDemand, 0) - Math.min(capacity - oldDemand, 0);
            vehicleDemandMap.put(vehicle, newDemand);
            if (customer.getNextCustomer() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore -= vehicle.getLocation().getMilliDistance(customer.getLocation());
            }
        }
    }

    private void retractVehicle(VrpCustomer customer) {
        VrpVehicle vehicle = customer.getVehicle();
        if (vehicle != null) {
            // Score constraint vehicleCapacity
            int capacity = vehicle.getCapacity();
            int oldDemand = vehicleDemandMap.get(vehicle);
            int newDemand = oldDemand - customer.getDemand();
            hardScore += Math.min(capacity - newDemand, 0) - Math.min(capacity - oldDemand, 0);
            vehicleDemandMap.put(vehicle, newDemand);
            if (customer.getNextCustomer() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore += vehicle.getLocation().getMilliDistance(customer.getLocation());
            }
        }
    }

    private void insertNextCustomer(VrpCustomer customer) {
        VrpVehicle vehicle = customer.getVehicle();
        if (vehicle != null) {
            if (customer.getNextCustomer() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore -= vehicle.getLocation().getMilliDistance(customer.getLocation());
            }
        }
    }

    private void retractNextCustomer(VrpCustomer customer) {
        VrpVehicle vehicle = customer.getVehicle();
        if (vehicle != null) {
            if (customer.getNextCustomer() == null) {
                // Score constraint distanceFromLastCustomerToDepot
                softScore += vehicle.getLocation().getMilliDistance(customer.getLocation());
            }
        }
    }

    private void insertMilliArrivalTime(VrpTimeWindowedCustomer customer) {
        Integer milliArrivalTime = customer.getMilliArrivalTime();
        if (milliArrivalTime != null) {
            int milliReadyTime = customer.getMilliReadyTime();
            int milliDueTime = customer.getMilliDueTime();
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

    private void retractMilliArrivalTime(VrpTimeWindowedCustomer customer) {
        Integer milliArrivalTime = customer.getMilliArrivalTime();
        if (milliArrivalTime != null) {
            int milliReadyTime = customer.getMilliReadyTime();
            int milliDueTime = customer.getMilliDueTime();
            if (milliDueTime < milliArrivalTime) {
                // Score constraint arrivalAfterDueTime
                hardScore += (milliArrivalTime - milliDueTime);
            }
            if (milliArrivalTime < milliReadyTime) {
                // Score constraint arrivalBeforeReadyTime
                // Many external benchmark records tend to ignore this constraint.
                // That heavily affects the attainable score.
                softScore += (milliReadyTime - milliArrivalTime);
            }
        }
    }

    public HardSoftScore calculateScore() {
        return HardSoftScore.valueOf(hardScore, softScore);
    }

}
