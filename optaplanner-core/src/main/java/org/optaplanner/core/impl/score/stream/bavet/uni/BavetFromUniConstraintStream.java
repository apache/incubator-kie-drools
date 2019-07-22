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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetFromUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> fromClass;

    public BavetFromUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, Class<A> fromClass) {
        super(bavetConstraint);
        this.fromClass = fromClass;
        if (fromClass == null) {
            throw new IllegalArgumentException("The fromClass (null) cannot be null.");
        }
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public BavetFromUniNode<A> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        return (BavetFromUniNode<A>) super.createNodeChain(buildPolicy, constraintWeight, nodeOrder, parentNode);
    }

    @Override
    protected BavetFromUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        if (parentNode != null) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") cannot have a parentNode (" + parentNode + ").");
        }
        return new BavetFromUniNode<>(buildPolicy.getSession(), nodeOrder, fromClass);
    }

    @Override
    public String toString() {
        return "From(" + fromClass.getSimpleName() + ") with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Class<A> getFromClass() {
        return fromClass;
    }

}
