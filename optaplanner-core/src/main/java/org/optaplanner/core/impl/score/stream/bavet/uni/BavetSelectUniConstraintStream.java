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

import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetSelectUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final Class<A> selectClass;

    public BavetSelectUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, Class<A> selectClass) {
        super(bavetConstraint);
        this.selectClass = selectClass;
        if (selectClass == null) {
            throw new IllegalArgumentException("The selectClass (null) cannot be null.");
        }
    }

    @Override
    public BavetSelectUniNode<A> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder) {
        return (BavetSelectUniNode<A>) super.createNodeChain(buildPolicy, constraintWeight, nodeOrder);
    }

    @Override
    protected BavetSelectUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList) {
        if (childNodeList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        BavetSelectUniNode<A> node = new BavetSelectUniNode<>(buildPolicy.getSession(), nodeOrder, selectClass, childNodeList);
        BavetSelectUniNode<A> sharedNode = buildPolicy.retrieveSharedNode(node);
        if (sharedNode != node) {
            sharedNode.getChildNodeList().addAll(childNodeList); // TODO Doesn't allow sharing the filter after select
        }
        return sharedNode;
    }

    @Override
    public String toString() {
        return "Select(" + selectClass.getSimpleName() + ") to " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public Class<A> getSelectClass() {
        return selectClass;
    }

}
