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

public final class BavetIntScoringBiTuple<A, B> extends BavetAbstractBiTuple<A, B> {

    private final BavetIntScoringBiNode<A, B> node;
    private final BavetAbstractBiTuple<A, B> previousTuple;

    private int score = 0;

    public BavetIntScoringBiTuple(BavetIntScoringBiNode<A, B> node, BavetAbstractBiTuple<A, B> previousTuple) {
        this.node = node;
        this.previousTuple = previousTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "IntScore(" + getFactA() + ", " + getFactB() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetIntScoringBiNode<A, B> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return previousTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return previousTuple.getFactB();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
