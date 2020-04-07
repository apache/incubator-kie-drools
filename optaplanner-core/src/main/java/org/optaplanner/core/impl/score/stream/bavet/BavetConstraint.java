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

package org.optaplanner.core.impl.score.stream.bavet;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniNode;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraint;
import org.optaplanner.core.impl.score.stream.common.ScoreImpactType;

public final class BavetConstraint<Solution_> extends AbstractConstraint<Solution_, BavetConstraintFactory<Solution_>> {

    private final List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList;

    public BavetConstraint(BavetConstraintFactory<Solution_> constraintFactory, String constraintPackage,
            String constraintName, Function<Solution_, Score<?>> constraintWeightExtractor,
            ScoreImpactType scoreImpactType, boolean isConstraintWeightConfigurable,
            List<BavetFromUniConstraintStream<Solution_, Object>> fromStreamList) {
        super(constraintFactory, constraintPackage, constraintName, constraintWeightExtractor, scoreImpactType,
                isConstraintWeightConfigurable);
        this.fromStreamList = fromStreamList;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

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

    @Override
    public String toString() {
        return "BavetConstraint(" + getConstraintId() + ") in " + fromStreamList.size() + " from() stream(s)";
    }

}
