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

package org.optaplanner.core.impl.bruteforce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.optaplanner.core.impl.bruteforce.event.BruteForceSolverPhaseLifecycleListener;
import org.optaplanner.core.impl.bruteforce.scope.BruteForceSolverPhaseScope;
import org.optaplanner.core.impl.bruteforce.scope.BruteForceStepScope;
import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelectionOrder;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelectionPromotion;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueSelector;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningValueWalker;
import org.optaplanner.core.impl.heuristic.selector.variable.PlanningVariableWalker;

public class BruteForceEntityWalker implements BruteForceSolverPhaseLifecycleListener {

    private SolutionDescriptor solutionDescriptor;

    private List<PlanningVariableWalker> planningVariableWalkerList;

    public BruteForceEntityWalker(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    public void phaseStarted(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        List<Object> workingPlanningEntityList = bruteForceSolverPhaseScope.getWorkingPlanningEntityList();
        planningVariableWalkerList = new ArrayList<PlanningVariableWalker>(workingPlanningEntityList.size());
        for (Object planningEntity : workingPlanningEntityList) {
            PlanningEntityDescriptor planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(
                    planningEntity.getClass());
            PlanningVariableWalker planningVariableWalker = new PlanningVariableWalker(planningEntityDescriptor);
            List<PlanningValueWalker> planningValueWalkerList = buildPlanningValueWalkerList(planningEntityDescriptor);
            planningVariableWalker.setPlanningValueWalkerList(planningValueWalkerList);
            planningVariableWalkerList.add(planningVariableWalker);
            planningVariableWalker.phaseStarted(bruteForceSolverPhaseScope);
            planningVariableWalker.initWalk(planningEntity);
        }
    }

    private List<PlanningValueWalker> buildPlanningValueWalkerList(PlanningEntityDescriptor planningEntityDescriptor) {
        Collection<PlanningVariableDescriptor> planningVariableDescriptors
                = planningEntityDescriptor.getPlanningVariableDescriptors();
        List<PlanningValueWalker> planningValueWalkerList = new ArrayList<PlanningValueWalker>(
                planningVariableDescriptors.size());
        for (PlanningVariableDescriptor planningVariableDescriptor : planningVariableDescriptors) {
            PlanningValueSelector planningValueSelector = new PlanningValueSelector(planningVariableDescriptor);
            planningValueSelector.setSelectionOrder(PlanningValueSelectionOrder.ORIGINAL);
            planningValueSelector.setSelectionPromotion(PlanningValueSelectionPromotion.NONE);
            planningValueSelector.setRoundRobinSelection(false);
            PlanningValueWalker planningValueWalker = new PlanningValueWalker(planningVariableDescriptor,
                    planningValueSelector);
            planningValueWalkerList.add(planningValueWalker);
        }
        return planningValueWalkerList;
    }

    public boolean hasWalk() {
        for (PlanningVariableWalker planningVariableWalker : planningVariableWalkerList) {
            if (planningVariableWalker.hasWalk()) {
                return true;
            }
        }
        // All levels are maxed out
        return false;
    }

    public void walk() {
        // Find the level to increment (for example in 115999)
        for (PlanningVariableWalker planningVariableWalker : planningVariableWalkerList) {
            if (planningVariableWalker.hasWalk()) {
                // Increment that level (for example 5 in 115999)
                planningVariableWalker.walk();
                // Do not touch the higher levels (for example each 1 in 115999)
                break;
            } else {
                // Reset the lower levels (for example each 9 in 115999)
                planningVariableWalker.resetWalk();
            }
        }
    }

    public void stepEnded(BruteForceStepScope bruteForceStepScope) {
        for (PlanningVariableWalker planningVariableWalker : planningVariableWalkerList) {
            planningVariableWalker.stepEnded(bruteForceStepScope);
        }
    }

    public void phaseEnded(BruteForceSolverPhaseScope bruteForceSolverPhaseScope) {
        for (PlanningVariableWalker planningVariableWalker : planningVariableWalkerList) {
            planningVariableWalker.phaseEnded(bruteForceSolverPhaseScope);
        }
        planningVariableWalkerList = null;
    }

}
