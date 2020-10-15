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

package org.optaplanner.examples.vehiclerouting.domain.timewindowed.solver;

import java.util.Objects;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;

// TODO When this class is added only for TimeWindowedCustomer, use TimeWindowedCustomer instead of Customer
public class ArrivalTimeUpdatingVariableListener implements VariableListener<VehicleRoutingSolution, Customer> {

    @Override
    public void beforeEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateArrivalTime(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        if (customer instanceof TimeWindowedCustomer) {
            updateArrivalTime(scoreDirector, (TimeWindowedCustomer) customer);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<VehicleRoutingSolution> scoreDirector, Customer customer) {
        // Do nothing
    }

    protected void updateArrivalTime(ScoreDirector<VehicleRoutingSolution> scoreDirector,
            TimeWindowedCustomer sourceCustomer) {
        Standstill previousStandstill = sourceCustomer.getPreviousStandstill();
        Long departureTime = previousStandstill == null ? null
                : (previousStandstill instanceof TimeWindowedCustomer)
                        ? ((TimeWindowedCustomer) previousStandstill).getDepartureTime()
                        : ((TimeWindowedDepot) ((Vehicle) previousStandstill).getDepot()).getReadyTime();
        TimeWindowedCustomer shadowCustomer = sourceCustomer;
        Long arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        while (shadowCustomer != null && !Objects.equals(shadowCustomer.getArrivalTime(), arrivalTime)) {
            scoreDirector.beforeVariableChanged(shadowCustomer, "arrivalTime");
            shadowCustomer.setArrivalTime(arrivalTime);
            scoreDirector.afterVariableChanged(shadowCustomer, "arrivalTime");
            departureTime = shadowCustomer.getDepartureTime();
            shadowCustomer = shadowCustomer.getNextCustomer();
            arrivalTime = calculateArrivalTime(shadowCustomer, departureTime);
        }
    }

    private Long calculateArrivalTime(TimeWindowedCustomer customer, Long previousDepartureTime) {
        if (customer == null || customer.getPreviousStandstill() == null) {
            return null;
        }
        if (customer.getPreviousStandstill() instanceof Vehicle) {
            // PreviousStandstill is the Vehicle, so we leave from the Depot at the best suitable time
            return Math.max(customer.getReadyTime(),
                    previousDepartureTime + customer.getDistanceFromPreviousStandstill());
        }
        return previousDepartureTime + customer.getDistanceFromPreviousStandstill();
    }

}
