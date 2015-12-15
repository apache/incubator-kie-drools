/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.exhaustivesearch.scope;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchLayer;
import org.optaplanner.core.impl.exhaustivesearch.node.ExhaustiveSearchNode;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;

public class ExhaustiveSearchPhaseScope extends AbstractPhaseScope {

    private List<ExhaustiveSearchLayer> layerList;
    private SortedSet<ExhaustiveSearchNode> expandableNodeQueue;
    private Score bestPessimisticBound;

    private ExhaustiveSearchStepScope lastCompletedStepScope;

    public ExhaustiveSearchPhaseScope(DefaultSolverScope solverScope) {
        super(solverScope);
        lastCompletedStepScope = new ExhaustiveSearchStepScope(this, -1);
    }

    public List<ExhaustiveSearchLayer> getLayerList() {
        return layerList;
    }

    public void setLayerList(List<ExhaustiveSearchLayer> layerList) {
        this.layerList = layerList;
    }

    public SortedSet<ExhaustiveSearchNode> getExpandableNodeQueue() {
        return expandableNodeQueue;
    }

    public void setExpandableNodeQueue(SortedSet<ExhaustiveSearchNode> expandableNodeQueue) {
        this.expandableNodeQueue = expandableNodeQueue;
    }

    public Score getBestPessimisticBound() {
        return bestPessimisticBound;
    }

    public void setBestPessimisticBound(Score bestPessimisticBound) {
        this.bestPessimisticBound = bestPessimisticBound;
    }

    public ExhaustiveSearchStepScope getLastCompletedStepScope() {
        return lastCompletedStepScope;
    }

    public void setLastCompletedStepScope(ExhaustiveSearchStepScope lastCompletedStepScope) {
        this.lastCompletedStepScope = lastCompletedStepScope;
    }


    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public int getDepthSize() {
        return layerList.size();
    }

    public void registerPessimisticBound(Score pessimisticBound) {
        if (pessimisticBound.compareTo(bestPessimisticBound) > 0) {
            bestPessimisticBound = pessimisticBound;
            // TODO optimize this because expandableNodeQueue is too long to iterate
            for (Iterator<ExhaustiveSearchNode> iterator = expandableNodeQueue.iterator(); iterator.hasNext(); ) {
                // Prune it
                ExhaustiveSearchNode node = iterator.next();
                if (node.getOptimisticBound().compareTo(bestPessimisticBound) < 0) {
                    iterator.remove();
                }
            }
        }
    }

    public void addExpandableNode(ExhaustiveSearchNode moveNode) {
        expandableNodeQueue.add(moveNode);
        moveNode.setExpandable(true);
    }

}
