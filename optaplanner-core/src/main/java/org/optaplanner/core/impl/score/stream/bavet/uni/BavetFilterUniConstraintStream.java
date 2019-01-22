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
import java.util.function.Predicate;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetFilterUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final Predicate<A> predicate;

    public BavetFilterUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, Predicate<A> predicate) {
        super(bavetConstraint);
        this.predicate = predicate;
        if (predicate == null) {
            throw new IllegalArgumentException("The predicate (null) cannot be null.");
        }
    }

    @Override
    protected BavetAbstractUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList) {
        if (childNodeList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        return new BavetFilterUniNode<>(buildPolicy.getSession(), nodeOrder, predicate, childNodeList);
    }

    @Override
    public String toString() {
        return "Filter() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
