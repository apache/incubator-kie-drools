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

package org.drools.planner.core.heuristic.selector.variable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.event.SolverPhaseLifecycleListener;
import org.drools.planner.core.phase.step.AbstractStepScope;

public class PlanningVariableWalker implements SolverPhaseLifecycleListener {
    
    private final PlanningEntityDescriptor planningEntityDescriptor;
    private List<PlanningValueWalker> planningValueWalkerList;

    private WorkingMemory workingMemory;

    private Object planningEntity;
    private FactHandle planningEntityFactHandle;

    public PlanningVariableWalker(PlanningEntityDescriptor planningEntityDescriptor) {
        this.planningEntityDescriptor = planningEntityDescriptor;
    }

    public void setPlanningValueWalkerList(List<PlanningValueWalker> planningValueWalkerList) {
        this.planningValueWalkerList = planningValueWalkerList;
    }

    public Map<PlanningVariableDescriptor, Object> getVariableToValueMap() {
        Map<PlanningVariableDescriptor, Object> variableToValueMap
                = new LinkedHashMap<PlanningVariableDescriptor, Object>(planningValueWalkerList.size());
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            variableToValueMap.put(planningValueWalker.getPlanningVariableDescriptor(),
                    planningValueWalker.getWorkingValue());
        }
        return variableToValueMap;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.phaseStarted(solverPhaseScope);
        }
        workingMemory = solverPhaseScope.getWorkingMemory();
    }

    public void beforeDeciding(AbstractStepScope stepScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.beforeDeciding(stepScope);
        }
    }

    public void stepDecided(AbstractStepScope stepScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.stepDecided(stepScope);
        }
    }

    public void stepTaken(AbstractStepScope stepScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.stepTaken(stepScope);
        }
    }

    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.phaseEnded(solverPhaseScope);
        }
        workingMemory = null;
        planningEntity = null;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void initWalk(Object planningEntity) {
        this.planningEntity = planningEntity;
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.initWalk(planningEntity);
        }
        // Insert must happen after every planningValueWalker.initWalk() to avoid a NullPointerException, for example:
        // Rules use Lecture.getDay(), which is implemented as "return period.getDay();"
        // so the planning variable period cannot be null when the planning entity is inserted.
        planningEntityFactHandle = workingMemory.insert(planningEntity);
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.initPlanningEntityFactHandle(planningEntityFactHandle);
        }
    }

    public boolean hasWalk() {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            if (planningValueWalker.hasWalk()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void walk() {
        // Find the level to increment (for example in 115999)
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            if (planningValueWalker.hasWalk()) {
                // Increment that level (for example 5 in 115999)
                planningValueWalker.walk();
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningValueWalker.resetWalk();
            }
        }
    }

    public void resetWalk() {
        for (PlanningValueWalker planningValueWalker : planningValueWalkerList) {
            planningValueWalker.resetWalk();
        }
    }

}
