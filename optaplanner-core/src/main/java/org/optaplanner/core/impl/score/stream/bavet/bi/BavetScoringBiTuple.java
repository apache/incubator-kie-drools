/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;

public final class BavetScoringBiTuple<A, B> extends BavetAbstractBiTuple<A, B> {

    private final BavetScoringBiNode<A, B> node;
    private final BavetAbstractBiTuple<A, B> parentTuple;

    private UndoScoreImpacter undoScoreImpacter = null;

    public BavetScoringBiTuple(BavetScoringBiNode<A, B> node, BavetAbstractBiTuple<A, B> parentTuple) {
        this.node = node;
        this.parentTuple = parentTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "Scoring(" + getFactA() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetScoringBiNode<A, B> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return parentTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return parentTuple.getFactB();
    }

    public UndoScoreImpacter getUndoScoreImpacter() {
        return undoScoreImpacter;
    }

    public void setUndoScoreImpacter(UndoScoreImpacter undoScoreImpacter) {
        this.undoScoreImpacter = undoScoreImpacter;
    }

}
