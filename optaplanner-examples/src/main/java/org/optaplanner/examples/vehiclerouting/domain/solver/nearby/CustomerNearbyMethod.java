/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.examples.vehiclerouting.domain.solver.nearby;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearEntityNearbyMethod;
import org.optaplanner.core.impl.heuristic.solution.WorkingSolutionAware;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class CustomerNearbyMethod implements NearEntityNearbyMethod<Customer, Customer>, WorkingSolutionAware<VehicleRoutingSolution> {

    private Map<Customer, Customer[]> nearbyCustomerListMap = null;

    @Override
    public void setWorkingSolution(VehicleRoutingSolution workingSolution) {
        List<Customer> customerList = workingSolution.getCustomerList();
        nearbyCustomerListMap = new HashMap<Customer, Customer[]>(customerList.size());
        for (final Customer origin : customerList) {
            Customer[] nearbyStandstillList = new Customer[customerList.size()];
            for (int i = 0; i < nearbyStandstillList.length; i++) {
                nearbyStandstillList[i] = customerList.get(i);
            }
            Arrays.sort(nearbyStandstillList, new Comparator<Customer>() {
                @Override
                public int compare(Customer a, Customer b) {
                    int aDistance = origin.getDistanceTo(a);
                    int bDistance = origin.getDistanceTo(b);
                    if (aDistance < bDistance) {
                        return -1;
                    } else if (aDistance > bDistance) {
                        return 1;
                    } else {
                        return a.getLocation().getId().compareTo(b.getLocation().getId());
                    }
                }
            });
            nearbyCustomerListMap.put(origin, nearbyStandstillList);
        }
    }

    @Override
    public void unsetWorkingSolution() {
        nearbyCustomerListMap = null;
    }

    @Override
    public Customer getByNearbyIndex(Customer origin, int nearbyIndex) {
        Customer[] nearbyStandstillList = nearbyCustomerListMap.get(origin);
        return nearbyStandstillList[nearbyIndex];
    }

}
