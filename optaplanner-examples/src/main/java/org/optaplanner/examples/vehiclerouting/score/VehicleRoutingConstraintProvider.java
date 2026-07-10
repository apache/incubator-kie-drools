/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.vehiclerouting.score;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.sum;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;

public class VehicleRoutingConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                vehicleCapacity(factory),
                distanceToPreviousStandstill(factory),
                distanceFromLastCustomerToDepot(factory),
                arrivalAfterDueTime(factory)
        };
    }

    // ************************************************************************
    // Hard constraints
    // ************************************************************************

    protected Constraint vehicleCapacity(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .filter(customer -> customer.getVehicle() != null)
                .groupBy(Customer::getVehicle, sum(Customer::getDemand))
                .filter((vehicle, demand) -> demand > vehicle.getCapacity())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        (vehicle, demand) -> demand - vehicle.getCapacity())
                .asConstraint("vehicleCapacity");
    }

    // ************************************************************************
    // Soft constraints
    // ************************************************************************

    protected Constraint distanceToPreviousStandstill(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .filter(customer -> customer.getVehicle() != null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        Customer::getDistanceFromPreviousStandstill)
                .asConstraint("distanceToPreviousStandstill");
    }

    protected Constraint distanceFromLastCustomerToDepot(ConstraintFactory factory) {
        return factory.forEach(Customer.class)
                .filter(customer -> customer.getVehicle() != null && customer.getNextCustomer() == null)
                .penalizeLong(HardSoftLongScore.ONE_SOFT,
                        Customer::getDistanceToDepot)
                .asConstraint("distanceFromLastCustomerToDepot");
    }

    // ************************************************************************
    // TimeWindowed: additional hard constraints
    // ************************************************************************

    protected Constraint arrivalAfterDueTime(ConstraintFactory factory) {
        return factory.forEach(TimeWindowedCustomer.class)
                .filter(customer -> customer.getVehicle() != null && customer.getArrivalTime() > customer.getDueTime())
                .penalizeLong(HardSoftLongScore.ONE_HARD,
                        customer -> customer.getArrivalTime() - customer.getDueTime())
                .asConstraint("arrivalAfterDueTime");
    }

}
