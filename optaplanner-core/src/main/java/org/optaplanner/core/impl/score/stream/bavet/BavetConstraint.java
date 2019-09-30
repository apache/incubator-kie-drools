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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniNode;

public final class BavetConstraint<Solution_> implements Constraint {

    private final BavetConstraintFactory<Solution_> constraintFactory;
    private final String constraintPackage;
    private final String constraintName;
    private Function<Solution_, Score<?>> constraintWeightExtractor;
    private final boolean positive;
    private final List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList;

    public BavetConstraint(BavetConstraintFactory<Solution_> constraintFactory,
            String constraintPackage, String constraintName,
            Function<Solution_, Score<?>> constraintWeightExtractor, boolean positive,
            List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList) {
        this.constraintFactory = constraintFactory;
        this.constraintPackage = constraintPackage;
        this.constraintName = constraintName;
        this.constraintWeightExtractor = constraintWeightExtractor;
        this.positive = positive;
        this.fromStreamList = fromStreamList;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public Score<?> extractConstraintWeight(Solution_ workingSolution) {
        Score<?> constraintWeight = constraintWeightExtractor.apply(workingSolution);
        constraintFactory.getSolutionDescriptor().validateConstraintWeight(constraintPackage, constraintName, constraintWeight);
        return positive ? constraintWeight : constraintWeight.negate();
    }

    public void createNodes(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Map<Class<?>, BavetFromUniNode<Object>> declaredClassToNodeMap,
            Score<?> constraintWeight) {
        for (BavetFromUniConstraintStream<Solution_, Object> fromStream : fromStreamList) {
            int nodeOrder = 0;
            BavetFromUniNode<Object> node = fromStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder, null);
            BavetFromUniNode<Object> oldNode = declaredClassToNodeMap.putIfAbsent(fromStream.getFromClass(), node);
            if (oldNode != null && oldNode != node) {
                throw new IllegalStateException("The oldNode (" + oldNode
                        + ") differs from the new node (" + node + ").");
            }
        }
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetConstraintFactory<Solution_> getConstraintFactory() {
        return constraintFactory;
    }

    @Override
    public String getConstraintPackage() {
        return constraintPackage;
    }

    @Override
    public String getConstraintName() {
        return constraintName;
    }

}
