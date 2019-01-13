/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public abstract class BavetAbstractUniConstraintStream<Solution_, A> implements UniConstraintStream<A> {

    protected final BavetConstraint<Solution_> bavetConstraint;
    protected final List<BavetAbstractUniConstraintStream<Solution_, A>> nextStreamList = new ArrayList<>(2);

    public BavetAbstractUniConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        this.bavetConstraint = bavetConstraint;
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    @Override
    public BavetAbstractUniConstraintStream<Solution_, A> filter(Predicate<A> predicate) {
        BavetFilterUniConstraintStream<Solution_, A> stream = new BavetFilterUniConstraintStream<>(bavetConstraint, predicate);
        nextStreamList.add(stream);
        return stream;
    }

    @Override
    public void penalize() {
        ToIntFunction<A> matchWeigher = (A a) -> -1;
        addIntScoring(matchWeigher);
    }

    @Override
    public void reward() {
        ToIntFunction<A> matchWeigher = (A a) -> 1;
        addIntScoring(matchWeigher);
    }

    private void addIntScoring(ToIntFunction<A> matchWeigher) {
        BavetIntScoringUniConstraintStream<Solution_, A> stream = new BavetIntScoringUniConstraintStream<>(bavetConstraint, matchWeigher);
        nextStreamList.add(stream);
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    public BavetAbstractUniNode<A> createNodeChain(BavetNodeBuildPolicy buildPolicy, Score<?> constraintWeight, int nodeOrder) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        if (nextStreamList.isEmpty()) {
            return createNode(buildPolicy, constraintWeight, nodeOrder, null);
        } else if (nextStreamList.size() == 1) {
            BavetAbstractUniConstraintStream<Solution_, A> nextStream = nextStreamList.get(0);
            BavetAbstractUniNode<A> nextNode = nextStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1);
            return createNode(buildPolicy, constraintWeight, nodeOrder, nextNode);
        }
        // TODO Implement or fail-faster during setup
        throw new UnsupportedOperationException("Node sharing is currently not supported.");
    }

    protected abstract BavetAbstractUniNode<A> createNode(BavetNodeBuildPolicy buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> nextNode);

}
