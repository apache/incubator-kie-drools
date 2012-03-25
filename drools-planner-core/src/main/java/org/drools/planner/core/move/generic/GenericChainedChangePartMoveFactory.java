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
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

// TODO this is a dirty prototype
public class GenericChainedChangePartMoveFactory extends AbstractMoveFactory {

    private SolutionDescriptor solutionDescriptor;
    private SolutionDirector solutionDirector;

    // TODO implement me + make this configurable
    private Integer maximumSubChainSize = null;

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseStarted(localSearchSolverPhaseScope);
        solutionDescriptor = localSearchSolverPhaseScope.getSolutionDescriptor();
        solutionDirector = localSearchSolverPhaseScope.getSolutionDirector();
    }

    public List<Move> createMoveList(Solution solution) {
        List<Move> moveList = new ArrayList<Move>();
        Solution workingSolution = solutionDirector.getWorkingSolution();
        WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
        for (PlanningEntityDescriptor entityDescriptor : solutionDescriptor.getPlanningEntityDescriptors()) {
            for (PlanningVariableDescriptor variableDescriptor : entityDescriptor.getPlanningVariableDescriptors()) {
                if (variableDescriptor.isChained()) {
                    Map<Object,List<Object>> variableToEntitiesMap = solutionDirector.getVariableToEntitiesMap(
                            variableDescriptor);
                    Collection<?> values = variableDescriptor.extractAllPlanningValues(workingSolution);
                    if (values.size() > 500) {
                        // TODO https://issues.jboss.org/browse/JBRULES-3371
                        throw new IllegalStateException("TODO fix JBRULES-3371 so this works.");
                    }
                    for (Object anchor : values) {
                        // value can never be null because nullable isn't allowed with chained
                        if (!entityDescriptor.getPlanningEntityClass().isAssignableFrom(anchor.getClass())) {
                            List<Object> anchorWithChain = new ArrayList<Object>(values.size());
                            anchorWithChain.add(anchor);
                            List<Object> trailingEntities = variableToEntitiesMap.get(anchor);
                            while (trailingEntities != null) {
                                if (trailingEntities.size() > 1) {
                                    throw new IllegalStateException("The planningValue (" + anchor
                                            + ") has multiple trailing entities (" + trailingEntities
                                            + ") pointing to it for chained planningVariable ("
                                            + variableDescriptor.getVariablePropertyName() + ").");
                                }
                                Object trailingEntity = trailingEntities.get(0);
                                anchorWithChain.add(trailingEntity);
                                trailingEntities = variableToEntitiesMap.get(trailingEntity);
                            }

                            int chainSize = anchorWithChain.size();
                            for (int fromIndex = 1; fromIndex < chainSize; fromIndex++) {
                                Object oldToValue = anchorWithChain.get(fromIndex - 1);
                                for (int toIndex = fromIndex + 2; toIndex <= chainSize; toIndex++) {
                                    List<Object> entitiesSubChain = anchorWithChain.subList(fromIndex, toIndex);
                                    Object oldTrailingEntity;
                                    if (toIndex < chainSize) {
                                        oldTrailingEntity = anchorWithChain.get(toIndex);
                                    } else {
                                        oldTrailingEntity = null;
                                    }
                                    for (Object toValue : values) {
                                        // Subchains can only be moved into other (sub)chains
                                        if (!entitiesSubChain.contains(toValue)) {
                                            Object newTrailingEntity = findTrailingEntity(variableToEntitiesMap,
                                                    variableDescriptor, toValue);
                                            // Moving to the same oldToValue has no effect
                                            // TODO also filter out moves done by GenericChainedChangeMoveFactory
                                            // where the entire subchain is only moved 1 position back or forth
                                            if (!oldToValue.equals((toValue))) {
                                                moveList.add(new GenericChainedChangePartMove(entitiesSubChain,
                                                        variableDescriptor, toValue,
                                                        oldTrailingEntity, newTrailingEntity));
                                            }
                                            // Reversing an entire chain has no effect
                                            // TODO in some case it has an effect (when the trucks don't go back to the depot) make this configurable
                                            if (chainSize != entitiesSubChain.size()) {
                                                moveList.add(new GenericReverseChainedChangePartMove(entitiesSubChain,
                                                        variableDescriptor, toValue,
                                                        oldTrailingEntity, newTrailingEntity));
                                            }
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
