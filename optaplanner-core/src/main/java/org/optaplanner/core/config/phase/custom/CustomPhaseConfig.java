/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.config.phase.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.optaplanner.core.config.heuristic.policy.HeuristicConfigPolicy;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.config.util.KeyAsElementMapConverter;
import org.optaplanner.core.impl.phase.custom.CustomPhase;
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.phase.custom.DefaultCustomPhase;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.termination.Termination;

@XStreamAlias("customPhase")
public class CustomPhaseConfig extends PhaseConfig<CustomPhaseConfig> {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamImplicit(itemFieldName = "customPhaseCommandClass")
    protected List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList = null;

    @XStreamConverter(KeyAsElementMapConverter.class)
    protected Map<String, String> customProperties = null;
    protected Boolean forceUpdateBestSolution = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    public List<Class<? extends CustomPhaseCommand>> getCustomPhaseCommandClassList() {
        return customPhaseCommandClassList;
    }

    public void setCustomPhaseCommandClassList(List<Class<? extends CustomPhaseCommand>> customPhaseCommandClassList) {
        this.customPhaseCommandClassList = customPhaseCommandClassList;
    }

    public Map<String, String> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, String> customProperties) {
        this.customProperties = customProperties;
    }

    public Boolean getForceUpdateBestSolution() {
        return forceUpdateBestSolution;
    }

    public void setForceUpdateBestSolution(Boolean forceUpdateBestSolution) {
        this.forceUpdateBestSolution = forceUpdateBestSolution;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    @Override
    public CustomPhase buildPhase(int phaseIndex, HeuristicConfigPolicy solverConfigPolicy,
            BestSolutionRecaller bestSolutionRecaller, Termination solverTermination) {
        HeuristicConfigPolicy phaseConfigPolicy = solverConfigPolicy.createPhaseConfigPolicy();
        DefaultCustomPhase phase = new DefaultCustomPhase(
                phaseIndex, solverConfigPolicy.getLogIndentation(), bestSolutionRecaller,
                buildPhaseTermination(phaseConfigPolicy, solverTermination));
        if (ConfigUtils.isEmptyCollection(customPhaseCommandClassList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <customPhaseCommandClass> in the <customPhase> configuration.");
        }
        List<CustomPhaseCommand> customPhaseCommandList = new ArrayList<>(customPhaseCommandClassList.size());
        for (Class<? extends CustomPhaseCommand> customPhaseCommandClass : customPhaseCommandClassList) {
            CustomPhaseCommand customPhaseCommand = ConfigUtils.newInstance(this,
                    "customPhaseCommandClass", customPhaseCommandClass);
            ConfigUtils.applyCustomProperties(customPhaseCommand, "customPhaseCommandClass",
                    customProperties, "customProperties");
            customPhaseCommandList.add(customPhaseCommand);
        }
        phase.setCustomPhaseCommandList(customPhaseCommandList);
        phase.setForceUpdateBestSolution(forceUpdateBestSolution == null ? false : forceUpdateBestSolution);
        EnvironmentMode environmentMode = phaseConfigPolicy.getEnvironmentMode();
        if (environmentMode.isNonIntrusiveFullAsserted()) {
            phase.setAssertStepScoreFromScratch(true);
        }
        return phase;
    }

    @Override
    public void inherit(CustomPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        customPhaseCommandClassList = ConfigUtils.inheritMergeableListProperty(
                customPhaseCommandClassList, inheritedConfig.getCustomPhaseCommandClassList());
        customProperties = ConfigUtils.inheritMergeableMapProperty(
                customProperties, inheritedConfig.getCustomProperties());
        forceUpdateBestSolution = ConfigUtils.inheritOverwritableProperty(forceUpdateBestSolution,
                inheritedConfig.getForceUpdateBestSolution());
    }

}
