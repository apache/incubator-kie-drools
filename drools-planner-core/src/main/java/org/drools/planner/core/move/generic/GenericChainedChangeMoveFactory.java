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
import java.util.Map;

import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
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
        Solution workingSolution = solutionDirector.getWorkingSolution();
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
        for (PlanningEntityDescriptor entityDescriptor : solutionDescriptor.getPlanningEntityDescriptors()) {
            for (PlanningVariableDescriptor variableDescriptor : entityDescriptor.getPlanningVariableDescriptors()) {
                Map<Object,List<Object>> variableToEntitiesMap = solutionDirector.getVariableToEntitiesMap(
                        variableDescriptor);
                // TODO this fetches the list twice
                List<Object> entityList =  entityDescriptor.extractEntities(workingSolution);
                for (Object entity : entityList) {
                    FactHandle entityFactHandle = workingMemory.getFactHandle(entity);
                    if (!variableDescriptor.isChained()) {
                        for (Object toPlanningValue : variableDescriptor.extractPlanningValues(
                                workingSolution, entity)) {
                            moveList.add(new GenericChangeMove(entity, entityFactHandle,
                                    variableDescriptor, toPlanningValue));
                        }
                    } else {
                        Object oldTrailingEntity = findTrailingEntity(variableToEntitiesMap, variableDescriptor, entity);
                        FactHandle oldTrailingEntityFactHandle = oldTrailingEntity == null
                                ? null : workingMemory.getFactHandle(oldTrailingEntity);
                        for (Object toPlanningValue : variableDescriptor.extractPlanningValues(
                                workingSolution, entity)) {
                            Object newTrailingEntity = findTrailingEntity(variableToEntitiesMap, variableDescriptor,
                                    toPlanningValue);
                            FactHandle newTrailingEntityFactHandle = newTrailingEntity == null
                                    ? null : workingMemory.getFactHandle(newTrailingEntity);
                            moveList.add(new GenericChainedChangeMove(entity, entityFactHandle,
                                    variableDescriptor, toPlanningValue,
                                    oldTrailingEntity, oldTrailingEntityFactHandle,
                                    newTrailingEntity, newTrailingEntityFactHandle));
                        }
                    }
                }
            }
        }
        return moveList;
    }

    private Object findTrailingEntity(Map<Object, List<Object>> variableToEntitiesMap,
            PlanningVariableDescriptor variableDescriptor, Object planningValue) {
        List<Object> trailingEntities = variableToEntitiesMap.get(planningValue);
        if (trailingEntities == null) {
            return null;
        }
        if (trailingEntities.size() > 1) {
            throw new IllegalStateException("The planningValue (" + planningValue
                    + ") has multiple trailing entities (" + trailingEntities
                    + ") pointing to it for chained planningVariable ("
                    + variableDescriptor.getVariablePropertyName() + ").");
        }
        return trailingEntities.get(0);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        solutionDescriptor = null;
        solutionDirector = null;
    }

}
