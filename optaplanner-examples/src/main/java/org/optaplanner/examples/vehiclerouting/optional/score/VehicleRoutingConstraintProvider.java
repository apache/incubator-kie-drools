/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.optional.score;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        vehicleCapacity(constraintFactory);
        distanceToPreviousStandstill(constraintFactory);
        distanceFromLastCustomerToDepot(constraintFactory);
        arrivalAfterDueTime(constraintFactory);
    }

    protected void vehicleCapacity(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("vehicleCapacity", HardSoftLongScore.ofHard(1L));
        c.from(Customer.class)
                .groupBy(Customer::getVehicle, sum(Customer::getDemand))
                .filter((vehicle, demand) -> demand > vehicle.getCapacity())
                .penalizeLong((vehicle, demand) -> demand - vehicle.getCapacity());
    }

    protected void distanceToPreviousStandstill(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("distanceToPreviousStandstill", HardSoftLongScore.ofSoft(1L));
        c.from(Customer.class)
                .penalizeLong(Customer::getDistanceFromPreviousStandstill);
    }

    protected void distanceFromLastCustomerToDepot(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("distanceFromLastCustomerToDepot", HardSoftLongScore.ofSoft(1L));
        c.from(Customer.class)
                .filter(customer -> customer.getNextCustomer() == null)
                .penalizeLong(customer -> customer.getDistanceTo(customer.getVehicle()));
    }

    protected void arrivalAfterDueTime(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("arrivalAfterDueTime", HardSoftLongScore.ofHard(1L));
        c.from(TimeWindowedCustomer.class)
                .filter(customer -> customer.getArrivalTime() > customer.getDueTime())
                .penalizeLong(customer -> customer.getArrivalTime() - customer.getDueTime());
    }

}
