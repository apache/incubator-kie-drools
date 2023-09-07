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

package org.optaplanner.examples.cloudbalancing.score;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.common.score.AbstractConstraintProviderTest;
import org.optaplanner.examples.common.score.ConstraintProviderTest;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class CloudBalancingConstraintProviderTest
        extends AbstractConstraintProviderTest<CloudBalancingConstraintProvider, CloudBalance> {

    @ConstraintProviderTest
    void requiredCpuPowerTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredCpuPowerTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredMemoryTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredMemoryTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void requiredNetworkBandwidthTotal(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        // Total = 2, available = 1.
        CloudProcess process1 = new CloudProcess(1, 1, 1, 1);
        process1.setComputer(computer1);
        CloudProcess process2 = new CloudProcess(2, 1, 1, 1);
        process2.setComputer(computer1);
        // Total = 1, available = 2.
        CloudProcess process3 = new CloudProcess(3, 1, 1, 1);
        process3.setComputer(computer2);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::requiredNetworkBandwidthTotal)
                .given(unassignedProcess, process1, process2, process3)
                .penalizesBy(1); // Only the first computer.
    }

    @ConstraintProviderTest
    void computerCost(ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> constraintVerifier) {
        CloudComputer computer1 = new CloudComputer(1, 1, 1, 1, 2);
        CloudComputer computer2 = new CloudComputer(2, 2, 2, 2, 4);
        CloudProcess unassignedProcess = new CloudProcess(0, 1, 1, 1);
        CloudProcess process = new CloudProcess(1, 1, 1, 1);
        process.setComputer(computer1);

        constraintVerifier.verifyThat(CloudBalancingConstraintProvider::computerCost)
                .given(computer1, computer2, unassignedProcess, process)
                .penalizesBy(2);
    }

    @Override
    protected ConstraintVerifier<CloudBalancingConstraintProvider, CloudBalance> createConstraintVerifier() {
        return ConstraintVerifier.build(new CloudBalancingConstraintProvider(), CloudBalance.class, CloudProcess.class);

    }
}
