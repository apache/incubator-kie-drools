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

package org.drools.planner.config.phase.custom;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.phase.SolverPhaseConfig;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.phase.custom.CustomSolverPhase;
import org.drools.planner.core.phase.custom.CustomSolverPhaseCommand;
import org.drools.planner.core.phase.custom.DefaultCustomSolverPhase;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.drools.planner.core.termination.Termination;

@XStreamAlias("customSolverPhase")
public class CustomSolverPhaseConfig extends SolverPhaseConfig {

    // Warning: all fields are null (and not defaulted) because they can be inherited
    // and also because the input config file should match the output config file

    @XStreamImplicit(itemFieldName = "customSolverPhaseCommandClass")
    protected List<Class<CustomSolverPhaseCommand>> customSolverPhaseCommandClassList = null;

    public List<Class<CustomSolverPhaseCommand>> getCustomSolverPhaseCommandClassList() {
        return customSolverPhaseCommandClassList;
    }

    public void setCustomSolverPhaseCommandClassList(List<Class<CustomSolverPhaseCommand>> customSolverPhaseCommandClassList) {
        this.customSolverPhaseCommandClassList = customSolverPhaseCommandClassList;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public CustomSolverPhase buildSolverPhase(EnvironmentMode environmentMode,
            SolutionDescriptor solutionDescriptor, ScoreDefinition scoreDefinition, Termination solverTermination) {
        DefaultCustomSolverPhase customSolverPhase = new DefaultCustomSolverPhase();
        configureSolverPhase(customSolverPhase, environmentMode, scoreDefinition, solverTermination);
        if (customSolverPhaseCommandClassList == null || customSolverPhaseCommandClassList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Configure at least 1 <customSolverPhaseCommandClass> in the customSolverPhase configuration.");
        }
        List<CustomSolverPhaseCommand> customSolverPhaseCommandList
                = new ArrayList<CustomSolverPhaseCommand>(customSolverPhaseCommandClassList.size());
        for (Class<CustomSolverPhaseCommand> customSolverPhaseCommandClass : customSolverPhaseCommandClassList) {
            try {
                customSolverPhaseCommandList.add(customSolverPhaseCommandClass.newInstance());
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("customSolverPhaseCommandClass ("
                        + customSolverPhaseCommandClass.getName() + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("customSolverPhaseCommandClass ("
                        + customSolverPhaseCommandClass.getName() + ") does not have a public no-arg constructor", e);
            }
        }
        customSolverPhase.setCustomSolverPhaseCommandList(customSolverPhaseCommandList);
        return customSolverPhase;
    }

    public void inherit(CustomSolverPhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        if (customSolverPhaseCommandClassList == null) {
            customSolverPhaseCommandClassList = inheritedConfig.getCustomSolverPhaseCommandClassList();
        } else if (inheritedConfig.getCustomSolverPhaseCommandClassList() != null) {
            // The inherited customSolverPhaseCommandClassList should be before the non-inherited one
            List<Class<CustomSolverPhaseCommand>> mergedList = new ArrayList<Class<CustomSolverPhaseCommand>>(
                    inheritedConfig.getCustomSolverPhaseCommandClassList());
            mergedList.addAll(customSolverPhaseCommandClassList);
            customSolverPhaseCommandClassList = mergedList;
        }
    }

}
