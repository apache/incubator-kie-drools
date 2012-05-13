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

package org.drools.planner.core.heuristic.selector.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.heuristic.selector.common.RandomIterator;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;

/**
 * This is the common {@link EntitySelector} implementation.
 */
public class FromSolutionEntitySelector extends AbstractEntitySelector {

    protected PlanningEntityDescriptor entityDescriptor;
    protected boolean randomSelection = false;
    protected long randomProbabilityWeight = 1L;

    protected Random workingRandom = null;

    protected List<Object> entityList = null;

    public FromSolutionEntitySelector(PlanningEntityDescriptor entityDescriptor) {
        this.entityDescriptor = entityDescriptor;
    }

    public PlanningEntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    public boolean isRandomSelection() {
        return randomSelection;
    }

    public void setRandomSelection(boolean randomSelection) {
        this.randomSelection = randomSelection;
    }

    public long getRandomProbabilityWeight() {
        return randomProbabilityWeight;
    }

    public void setRandomProbabilityWeight(long randomProbabilityWeight) {
        this.randomProbabilityWeight = randomProbabilityWeight;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public void phaseStarted(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseStarted(solverPhaseScope);
        workingRandom = solverPhaseScope.getWorkingRandom();
        // TODO if entities are added and removed by moves, then this caching is broken
        entityList = entityDescriptor.extractEntities(solverPhaseScope.getWorkingSolution());
    }

    @Override
    public void phaseEnded(AbstractSolverPhaseScope solverPhaseScope) {
        super.phaseEnded(solverPhaseScope);
        entityList = null;
    }

    public Iterator<Object> iterator() {
        if (!randomSelection) {
            return entityList.iterator();
        } else {
            return new RandomIterator<Object>(entityList, workingRandom);
        }
    }

    public boolean isContinuous() {
        return false;
    }

    public boolean isNeverEnding() {
        return randomSelection;
    }

    public long getSize() {
        return (long) entityList.size();
    }

}
