/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.exhaustivesearch.node;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.impl.exhaustivesearch.node.bounder.ScoreBounder;
import org.optaplanner.core.impl.heuristic.move.Move;

public class ExhaustiveSearchNode {

    private final ExhaustiveSearchLayer layer;
    private final ExhaustiveSearchNode parent;
    private final long breadth;

    // The move to get from the parent to this node
    private Move move;
    private Move undoMove;
    private Score score;
    /**
     * Never worse than the best possible score a leaf node below this node might lead to.
     *
     * @see ScoreBounder#calculateOptimisticBound(ScoreDirector, Score)
     */
    private Score optimisticBound;
    private boolean expandable = false;

    public ExhaustiveSearchNode(ExhaustiveSearchLayer layer, ExhaustiveSearchNode parent) {
        this.layer = layer;
        this.parent = parent;
        this.breadth = layer.assignBreadth();
    }

    public ExhaustiveSearchLayer getLayer() {
        return layer;
    }

    public ExhaustiveSearchNode getParent() {
        return parent;
    }

    public long getBreadth() {
        return breadth;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Move getUndoMove() {
        return undoMove;
    }

    public void setUndoMove(Move undoMove) {
        this.undoMove = undoMove;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Score getOptimisticBound() {
        return optimisticBound;
    }

    public void setOptimisticBound(Score optimisticBound) {
        this.optimisticBound = optimisticBound;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public int getDepth() {
        return layer.getDepth();
    }

    public String getTreeId() {
        return layer.getDepth() + "-" + breadth;
    }

    public Object getEntity() {
        return layer.getEntity();
    }

    public boolean isLastLayer() {
        return layer.isLastLayer();
    }

    public long getParentBreadth() {
        return parent == null ? -1 : parent.getBreadth();
    }

    @Override
    public String toString() {
        return getTreeId() + " (" + layer.getEntity() + ")";
    }

}
