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

package org.optaplanner.examples.cloudbalancing.optional.score;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

import static org.optaplanner.core.api.score.stream.common.ConstraintCollectors.*;

public class CloudBalancingConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        requiredCpuPowerTotal(constraintFactory);
        requiredMemoryTotal(constraintFactory);
        requiredNetworkBandwidthTotal(constraintFactory);
        computerCost(constraintFactory);
    }

    protected void requiredCpuPowerTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredCpuPowerTotal", HardSoftScore.ofHard(1));
        c.from(CloudProcess.class)
                .groupBy(CloudProcess::getComputer, sum(CloudProcess::getRequiredCpuPower))
                .filter((computer, requiredCpuPower) -> requiredCpuPower > computer.getCpuPower())
                .penalizeInt((computer, requiredCpuPower) -> requiredCpuPower - computer.getCpuPower());
    }

    protected void requiredMemoryTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredMemoryTotal", HardSoftScore.ofHard(1));
        c.from(CloudProcess.class)
                .groupBy(CloudProcess::getComputer, sum(CloudProcess::getRequiredMemory))
                .filter((computer, requiredMemory) -> requiredMemory > computer.getMemory())
                .penalizeInt((computer, requiredMemory) -> requiredMemory - computer.getMemory());
    }

    protected void requiredNetworkBandwidthTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredNetworkBandwidthTotal", HardSoftScore.ofHard(1));
        c.from(CloudProcess.class)
                .groupBy(CloudProcess::getComputer, sum(CloudProcess::getRequiredNetworkBandwidth))
                .filter((computer, requiredNetworkBandwidth) -> requiredNetworkBandwidth > computer.getNetworkBandwidth())
                .penalizeInt((computer, requiredNetworkBandwidth) -> requiredNetworkBandwidth - computer.getNetworkBandwidth());
    }

    protected void computerCost(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("computerCost", HardSoftScore.ofSoft(1));
        c.from(CloudProcess.class)
                // TODO Simplify by using exists()
                .groupBy(CloudProcess::getComputer, count())
                .penalizeInt((computer, count) -> computer.getCost());
    }

}
