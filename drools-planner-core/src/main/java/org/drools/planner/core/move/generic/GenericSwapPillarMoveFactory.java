/*
 * Copyright 2011 JBoss Inc
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.drools.FactHandle;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.localsearch.LocalSearchSolverPhaseScope;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.move.factory.AbstractMoveFactory;
import org.drools.planner.core.move.factory.CachedMoveFactory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;

/**
 * Non-cacheable
 */
public class GenericSwapPillarMoveFactory extends AbstractMoveFactory {

    private SolutionDescriptor solutionDescriptor;
    private SolutionDirector solutionDirector;

    @Override
    public void phaseStarted(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        solutionDescriptor = localSearchSolverPhaseScope.getSolutionDescriptor();
        solutionDirector = localSearchSolverPhaseScope.getSolutionDirector();
        super.phaseStarted(localSearchSolverPhaseScope);
    }

    @Override
    public void phaseEnded(LocalSearchSolverPhaseScope localSearchSolverPhaseScope) {
        super.phaseEnded(localSearchSolverPhaseScope);
        solutionDescriptor = null;
        solutionDirector = null;
    }

    public List<Move> createMoveList(Solution solution) {
        List<Move> moveList = new ArrayList<Move>();
        List<Object> globalEntityList = solutionDescriptor.getPlanningEntityList(solution);
        for (Class<?> entityClass : solutionDescriptor.getPlanningEntityImplementationClassSet()) {
            PlanningEntityDescriptor entityDescriptor = solutionDescriptor.getPlanningEntityDescriptor(entityClass);
            Collection<PlanningVariableDescriptor> variableDescriptors = entityDescriptor.getPlanningVariableDescriptors();

            List<List<Object>> swapEntityListList = buildSwapEntityListList(entityClass, variableDescriptors, globalEntityList);

            for (ListIterator<List<Object>> leftIt = swapEntityListList.listIterator(); leftIt.hasNext();) {
                List<Object> leftPlanningEntityList = leftIt.next();
                for (ListIterator<List<Object>> rightIt = swapEntityListList.listIterator(leftIt.nextIndex());
                        rightIt.hasNext();) {
                    List<Object> rightPlanningEntityList = rightIt.next();
                    moveList.add(new GenericSwapPillarMove(variableDescriptors,
                            leftPlanningEntityList, rightPlanningEntityList));
                }
            }
        }
        return moveList;
    }

    private List<List<Object>> buildSwapEntityListList(Class<?> entityClass,
            Collection<PlanningVariableDescriptor> variableDescriptors, List<Object> globalEntityList) {
        Map<List<Object>, List<Object>> valueStateToSwapEntityListMap
                = new LinkedHashMap<List<Object>, List<Object>>(globalEntityList.size());
        for (Object entity : globalEntityList) {
            if (entityClass.isInstance(entity)) {
                List<Object> valueState = new ArrayList<Object>(variableDescriptors.size());
                // TODO add optional filtering to only 1 variable
                for (PlanningVariableDescriptor variableDescriptor : variableDescriptors) {
                    Object value = variableDescriptor.getValue(entity);
                    valueState.add(value);
                }
                List<Object> swapEntityList = valueStateToSwapEntityListMap.get(valueState);
                if (swapEntityList == null) {
                    swapEntityList = new ArrayList<Object>();
                    valueStateToSwapEntityListMap.put(valueState, swapEntityList);
                }
                swapEntityList.add(entity);
            }
        }
        return new ArrayList<List<Object>>(valueStateToSwapEntityListMap.values());
    }

}
