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

package org.optaplanner.examples.taskassigning.persistence;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.taskassigning.domain.Customer;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;

public class TaskAssigningSolutionFileIO extends AbstractJsonSolutionFileIO<TaskAssigningSolution> {

    public TaskAssigningSolutionFileIO() {
        super(TaskAssigningSolution.class);
    }

    @Override
    public TaskAssigningSolution read(File inputSolutionFile) {
        TaskAssigningSolution taskAssigningSolution = super.read(inputSolutionFile);

        var customersById = taskAssigningSolution.getCustomerList().stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));
        /*
         * Replace the duplicate customer instances in the affinityMap by references to instances from
         * the customerList.
         */
        for (Employee employee : taskAssigningSolution.getEmployeeList()) {
            var newTravelDistanceMap = deduplicateMap(employee.getAffinityMap(),
                    customersById, Customer::getId);
            employee.setAffinityMap(newTravelDistanceMap);
        }
        return taskAssigningSolution;
    }

}
