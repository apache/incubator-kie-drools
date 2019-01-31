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
import org.optaplanner.core.api.score.stream.uni.UniCollector;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudBalancingConstraintProvider implements ConstraintProvider {

    @Override
    public void defineConstraints(ConstraintFactory constraintFactory) {
        requiredCpuPowerTotal(constraintFactory);
        requiredMemoryTotal(constraintFactory);
        requiredNetworkBandwidthTotal(constraintFactory);
    }

    protected void requiredCpuPowerTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredCpuPowerTotal", HardSoftScore.ofHard(1));
        c.select(CloudProcess.class)
                .filter(process -> process.getComputer() != null)
                .groupBy(CloudProcess::getComputer, UniCollector.summingInt(CloudProcess::getRequiredCpuPower))
                .filter((computer, requiredCpuPower) -> requiredCpuPower > computer.getCpuPower())
                .penalize((computer, requiredCpuPower) -> requiredCpuPower - computer.getCpuPower());
    }

    protected void requiredMemoryTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredMemoryTotal", HardSoftScore.ofHard(1));
        c.select(CloudProcess.class)
                .filter(process -> process.getComputer() != null)
                .groupBy(CloudProcess::getComputer, UniCollector.summingInt(CloudProcess::getRequiredMemory))
                .filter((computer, requiredMemory) -> requiredMemory > computer.getMemory())
                .penalize((computer, requiredMemory) -> requiredMemory - computer.getMemory());
    }

    protected void requiredNetworkBandwidthTotal(ConstraintFactory constraintFactory) {
        Constraint c = constraintFactory.newConstraintWithWeight("requiredNetworkBandwidthTotal", HardSoftScore.ofHard(1));
        c.select(CloudProcess.class)
                .filter(process -> process.getComputer() != null)
                .groupBy(CloudProcess::getComputer, UniCollector.summingInt(CloudProcess::getRequiredNetworkBandwidth))
                .filter((computer, requiredNetworkBandwidth) -> requiredNetworkBandwidth > computer.getNetworkBandwidth())
                .penalize((computer, requiredNetworkBandwidth) -> requiredNetworkBandwidth - computer.getNetworkBandwidth());
    }

}
