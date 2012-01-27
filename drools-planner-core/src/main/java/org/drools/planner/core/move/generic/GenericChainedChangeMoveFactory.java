/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.move.generic;

import java.util.ArrayList;
import java.util.List;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

// TODO Unify me into the normal GenericChangeMoveFactory
public class GenericChainedChangeMoveFactory extends AbstractMoveFactory {

    private SolutionDescriptor solutionDescriptor;
    private SolutionDirector solutionDirector;

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        solutionDescriptor = localSearchSolverPhaseScope.getSolutionDescriptor();
        solutionDirector = localSearchSolverPhaseScope.getSolutionDirector();
    }

    public List<Move> createMoveList(Solution solution) {
        List<Move> moveList = new ArrayList<Move>();
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
        for (Object planningEntity : solutionDescriptor.getPlanningEntityList(solution)) {
            FactHandle planningEntityFactHandle = workingMemory.getFactHandle(planningEntity);
            PlanningEntityDescriptor planningEntityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(
                    planningEntity.getClass());
            for (PlanningVariableDescriptor planningVariableDescriptor
                    : planningEntityDescriptor.getPlanningVariableDescriptors()) {
                if (!planningVariableDescriptor.isTriggerChainCorrection()) {
                    for (Object toPlanningValue : planningVariableDescriptor.extractPlanningValues(
                            solutionDirector.getWorkingSolution(), planningEntity)) {
                        moveList.add(new GenericChangeMove(planningEntity, planningEntityFactHandle,
                                planningVariableDescriptor, toPlanningValue));
                    }
                } else {
                    Object oldChainedEntity = findChainedEntity(planningVariableDescriptor, solution, planningEntity);
                    FactHandle oldChainedEntityFactHandle = oldChainedEntity == null
                            ? null : workingMemory.getFactHandle(oldChainedEntity);
                    for (Object toPlanningValue : planningVariableDescriptor.extractPlanningValues(
                            solutionDirector.getWorkingSolution(), planningEntity)) {
                        Object newChainedEntity = findChainedEntity(
                                planningVariableDescriptor, solution, toPlanningValue);
                        FactHandle newChainedEntityFactHandle = newChainedEntity == null
                                ? null : workingMemory.getFactHandle(newChainedEntity);
                        moveList.add(new GenericChainedChangeMove(planningEntity, planningEntityFactHandle,
                                planningVariableDescriptor, toPlanningValue,
                                oldChainedEntity, oldChainedEntityFactHandle,
                                newChainedEntity, newChainedEntityFactHandle));
                    }
                }
            }
        }
        return moveList;
    }

    private Object findChainedEntity(PlanningVariableDescriptor planningVariableDescriptor,
            Solution solution, Object planningEntity) {
        Object chainedEntity = null;
        PlanningEntityDescriptor entityDescriptor = planningVariableDescriptor.getPlanningEntityDescriptor();
        SolutionDescriptor solutionDescriptor = entityDescriptor.getSolutionDescriptor();
        for (Object suspectedChainedEntity : solutionDescriptor.getPlanningEntityListByPlanningEntityClass(
                solution, entityDescriptor.getPlanningEntityClass())) {
            if (planningVariableDescriptor.getValue(suspectedChainedEntity) == planningEntity) {
                if (chainedEntity != null) {
                    throw new IllegalStateException("The planningEntity (" + planningEntity
                            + ") has multiple chained entities (" + chainedEntity + ") ("
                            + suspectedChainedEntity + ") pointing to it.");
                }
                chainedEntity = suspectedChainedEntity;
            }
        }
        return chainedEntity;
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        solutionDescriptor = null;
        solutionDirector = null;
    }

}
