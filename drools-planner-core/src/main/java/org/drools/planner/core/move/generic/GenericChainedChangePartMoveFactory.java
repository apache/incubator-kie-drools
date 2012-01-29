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
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
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
public class GenericChainedChangePartMoveFactory extends AbstractMoveFactory {

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
                if (variableDescriptor.isTriggerChainCorrection()) {
                    Map<Object,List<Object>> variableToEntitiesMap = solutionDirector.getVariableToEntitiesMap(
                            variableDescriptor);
                    Collection<?> values = variableDescriptor.extractAllPlanningValues(workingSolution);
                    for (Object value : values) {
                        // value can never be null because nullable isn't allowed with triggerChainCorrection
                        if (!entityDescriptor.getPlanningEntityClass().isAssignableFrom(value.getClass())) {
                            List<Object> valueWithEntitiesChain = new ArrayList<Object>(values.size());
                            valueWithEntitiesChain.add(value);
                            List<Object> chainedEntities = variableToEntitiesMap.get(value);
                            while (chainedEntities != null) {
                                if (chainedEntities.size() > 1) {
                                    throw new IllegalStateException("The planningValue (" + value
                                            + ") has multiple chained entities (" + chainedEntities
                                            + ") pointing to it.");
                                }
                                Object chainedEntity = chainedEntities.get(0);
                                valueWithEntitiesChain.add(chainedEntity);
                                chainedEntities = variableToEntitiesMap.get(chainedEntity);
                            }

                            for (int fromIndex = 1; fromIndex < valueWithEntitiesChain.size(); fromIndex++) {
                                Object oldToValue = valueWithEntitiesChain.get(fromIndex - 1);
                                for (int toIndex = fromIndex + 2; toIndex <= valueWithEntitiesChain.size(); toIndex++) {
                                    List<Object> entitiesSubChain = valueWithEntitiesChain.subList(fromIndex, toIndex);
                                    Object oldChainedEntity;
                                    FactHandle oldChainedEntityFactHandle;
                                    if (toIndex < valueWithEntitiesChain.size()) {
                                        oldChainedEntity = valueWithEntitiesChain.get(toIndex);
                                        oldChainedEntityFactHandle = workingMemory.getFactHandle(oldChainedEntity);
                                    } else {
                                        oldChainedEntity = null;
                                        oldChainedEntityFactHandle = null;
                                    }
                                    for (Object toValue : values) {
                                        // Subchains can only be moved into other (sub)chains
                                        if (!entitiesSubChain.contains(toValue)) {
                                            Object newChainedEntity = findChainedEntity(variableToEntitiesMap, toValue);
                                            FactHandle newChainedEntityFactHandle = newChainedEntity == null
                                                    ? null : workingMemory.getFactHandle(newChainedEntity);
                                            // Moving to the same oldToValue has no effect
                                            // TODO also filter out moves done by GenericChainedChangeMoveFactory
                                            // where the entire subchain is only moved 1 position back or forth
                                            if (!oldToValue.equals((toValue))) {
                                                moveList.add(new GenericChainedChangePartMove(entitiesSubChain,
                                                        variableDescriptor, toValue,
                                                        oldChainedEntity, oldChainedEntityFactHandle,
                                                        newChainedEntity, newChainedEntityFactHandle));
                                            }
                                            moveList.add(new GenericReverseChainedChangePartMove(entitiesSubChain,
                                                    variableDescriptor, toValue,
                                                    oldChainedEntity, oldChainedEntityFactHandle,
                                                    newChainedEntity, newChainedEntityFactHandle));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return moveList;
    }

    private Object findChainedEntity(Map<Object,List<Object>> variableToEntitiesMap, Object planningValue) {
        List<Object> chainedEntities = variableToEntitiesMap.get(planningValue);
        if (chainedEntities == null) {
            return null;
        }
        if (chainedEntities.size() > 1) {
            throw new IllegalStateException("The planningValue (" + planningValue
                    + ") has multiple chained entities (" + chainedEntities + ") pointing to it.");
        }
        return chainedEntities.get(0);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        solutionDescriptor = null;
        solutionDirector = null;
    }

}
