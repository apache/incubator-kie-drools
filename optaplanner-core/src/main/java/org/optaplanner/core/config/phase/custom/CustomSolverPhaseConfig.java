/*
 * Copyright 2011 JBoss Inc
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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.commons.collections.CollectionUtils;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.phase.SolverPhaseConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.phase.custom.CustomSolverPhase;
import org.optaplanner.core.impl.phase.custom.CustomSolverPhaseCommand;
import org.optaplanner.core.impl.phase.custom.DefaultCustomSolverPhase;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.termination.Termination;

@XStreamAlias("customSolverPhase")
public class CustomSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamImplicit(itemFieldName = "customSolverPhaseCommandClass")
    protected List<Class<CustomSolverPhaseCommand>> customSolverPhaseCommandClassList = null;

    protected Boolean forceUpdateBestSolution = null;

    public List<Class<CustomSolverPhaseCommand>> getCustomSolverPhaseCommandClassList() {
        return customSolverPhaseCommandClassList;
    }

    public void setCustomSolverPhaseCommandClassList(List<Class<CustomSolverPhaseCommand>> customSolverPhaseCommandClassList) {
        this.customSolverPhaseCommandClassList = customSolverPhaseCommandClassList;
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

    public CustomSolverPhase buildSolverPhase(int phaseIndex, EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition, Termination solverTermination) {
        DefaultCustomSolverPhase customSolverPhase = new DefaultCustomSolverPhase();
        configureSolverPhase(customSolverPhase, phaseIndex, environmentMode, scoreDefinition, solverTermination);
        if (CollectionUtils.isEmpty(customSolverPhaseCommandClassList)) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <customSolverPhaseCommandClass> in the <customSolverPhase> configuration.");
        }
        List<CustomSolverPhaseCommand> customSolverPhaseCommandList
                = new ArrayList<CustomSolverPhaseCommand>(customSolverPhaseCommandClassList.size());
        for (Class<CustomSolverPhaseCommand> customSolverPhaseCommandClass : customSolverPhaseCommandClassList) {
            CustomSolverPhaseCommand customSolverPhaseCommand = ConfigUtils.newInstance(this,
                    "customSolverPhaseCommandClass", customSolverPhaseCommandClass);
            customSolverPhaseCommandList.add(customSolverPhaseCommand);
        }
        customSolverPhase.setCustomSolverPhaseCommandList(customSolverPhaseCommandList);
        customSolverPhase.setForceUpdateBestSolution(forceUpdateBestSolution == null ? false : forceUpdateBestSolution);
        return customSolverPhase;
    }

    public void inherit(CustomSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        customSolverPhaseCommandClassList = ConfigUtils.inheritMergeableListProperty(
                customSolverPhaseCommandClassList, inheritedConfig.getCustomSolverPhaseCommandClassList());
        forceUpdateBestSolution = ConfigUtils.inheritOverwritableProperty(forceUpdateBestSolution,
                inheritedConfig.getForceUpdateBestSolution());
    }

}
