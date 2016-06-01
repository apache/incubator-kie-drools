/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.vehiclerouting.solver.solution.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.phase.custom.AbstractCustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO PLANNER-380 Delete this class. Temporary implementation until BUOY_FIT is implemented as a Construction Heuristic
public class BuoyVehicleRoutingSolutionInitializer extends AbstractCustomPhaseCommand<VehicleRoutingSolution> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void changeWorkingSolution(ScoreDirector<VehicleRoutingSolution> scoreDirector) {
        VehicleRoutingSolution solution = scoreDirector.getWorkingSolution();
        List<Vehicle> vehicleList = solution.getVehicleList();
        List<Customer> customerList = solution.getCustomerList();
        List<Standstill> standstillList = new ArrayList<>(vehicleList.size() + customerList.size());
        standstillList.addAll(vehicleList);
        standstillList.addAll(customerList);
        logger.info("Starting sorting");
        Map<Standstill, Customer[]> nearbyMap = new HashMap<>(standstillList.size());
        for (final Standstill origin : standstillList) {
            Customer[] nearbyCustomers = customerList.toArray(new Customer[0]);
            Arrays.sort(nearbyCustomers, new Comparator<Standstill>() {
                @Override
                public int compare(Standstill a, Standstill b) {
                    double aDistance = origin.getLocation().getDistanceTo(a.getLocation());
                    double bDistance = origin.getLocation().getDistanceTo(b.getLocation());
                    return Double.compare(aDistance, bDistance);
                }
            });
            nearbyMap.put(origin, nearbyCustomers);
        }
        logger.info("Done sorting");

        List<Standstill> buoyList = new ArrayList<>(vehicleList);

        int NEARBY_LIMIT = 40;
        while (true) {
            Score stepScore = null;
            int stepBuoyIndex = -1;
            Customer stepEntity = null;
            for (int i = 0; i < buoyList.size(); i++) {
                Standstill buoy = buoyList.get(i);

                Customer[] nearbyCustomers = nearbyMap.get(buoy);
                int j = 0;
                for (Customer customer : nearbyCustomers) {
                    if (customer.getPreviousStandstill() != null) {
                        continue;
                    }
                    scoreDirector.beforeVariableChanged(customer, "previousStandstill");
                    customer.setPreviousStandstill(buoy);
                    scoreDirector.afterVariableChanged(customer, "previousStandstill");
                    scoreDirector.triggerVariableListeners();
                    Score score = scoreDirector.calculateScore();
                    scoreDirector.beforeVariableChanged(customer, "previousStandstill");
                    customer.setPreviousStandstill(null);
                    scoreDirector.afterVariableChanged(customer, "previousStandstill");
                    scoreDirector.triggerVariableListeners();
                    if (stepScore == null || score.toInitializedScore().compareTo(stepScore.toInitializedScore()) > 0) {
                        stepScore = score;
                        stepBuoyIndex = i;
                        stepEntity = customer;
                    }
                    if (j >= NEARBY_LIMIT) {
                        break;
                    }
                    j++;
                }
            }
            if (stepEntity == null) {
                break;
            }
            Standstill stepValue = buoyList.set(stepBuoyIndex, stepEntity);
            scoreDirector.beforeVariableChanged(stepEntity, "previousStandstill");
            stepEntity.setPreviousStandstill(stepValue);
            scoreDirector.afterVariableChanged(stepEntity, "previousStandstill");
            scoreDirector.triggerVariableListeners();
            logger.debug("    Score ({}), assigned customer ({}) to stepValue ({}).", stepScore, stepEntity, stepValue);
        }
    }

}
