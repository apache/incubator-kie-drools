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

package org.optaplanner.core.impl.phase.custom;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.AbstractPhaseFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

public class DefaultCustomPhaseFactory<Solution_> extends AbstractPhaseFactory<Solution_, CustomPhaseConfig> {

    public DefaultCustomPhaseFactory(CustomPhaseConfig phaseConfig) {
        super(phaseConfig);
    }

    @Override
    public CustomPhase<Solution_> buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller<Solution_> bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        DefaultCustomPhase phase = new DefaultCustomPhase(phaseIndex, solverConfigPolicy.getLogIndentation(),
                bestSolutionRecaller, buildPhaseTermination(phaseConfigPolicy, solverTermination));
        if (ConfigUtils.isEmptyCollection(phaseConfig.getCustomPhaseCommandClassList())
                && ConfigUtils.isEmptyCollection(phaseConfig.getCustomPhaseCommandList())) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <customPhaseCommandClass> in the <customPhase> configuration.");
        }

        List<CustomPhaseCommand<?>> customPhaseCommandList_ = new ArrayList<>(getCustomPhaseCommandListSize());
        if (phaseConfig.getCustomPhaseCommandClassList() != null) {
            for (Class<? extends CustomPhaseCommand> customPhaseCommandClass : phaseConfig.getCustomPhaseCommandClassList()) {
                customPhaseCommandList_.add(createCustomPhaseCommand(customPhaseCommandClass));
            }
        }
        if (phaseConfig.getCustomPhaseCommandList() != null) {
            customPhaseCommandList_.addAll(phaseConfig.getCustomPhaseCommandList());
        }
        phase.setCustomPhaseCommandList(customPhaseCommandList_);
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        return phase;
    }

    private CustomPhaseCommand<?> createCustomPhaseCommand(Class<? extends CustomPhaseCommand> customPhaseCommandClass) {
        CustomPhaseCommand<?> customPhaseCommand =
                ConfigUtils.newInstance(phaseConfig, "customPhaseCommandClass", customPhaseCommandClass);
        ConfigUtils.applyCustomProperties(customPhaseCommand, "customPhaseCommandClass", phaseConfig.getCustomProperties(),
                "customProperties");
        return customPhaseCommand;
    }

    private int getCustomPhaseCommandListSize() {
        return (phaseConfig.getCustomPhaseCommandClassList() == null ? 0 : phaseConfig.getCustomPhaseCommandClassList().size())
                + (phaseConfig.getCustomPhaseCommandList() == null ? 0 : phaseConfig.getCustomPhaseCommandList().size());
    }
}
