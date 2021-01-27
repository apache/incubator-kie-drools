/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;

public final class BavetFromUniTuple<A> extends BavetAbstractUniTuple<A> {

    private final BavetFromUniNode<A> node;
    private final A factA;
    private final List<BavetAbstractTuple> childTupleList;

    public BavetFromUniTuple(BavetFromUniNode<A> node, A factA, int childTupleListSize) {
        this.childTupleList = new ArrayList<>(childTupleListSize);
        this.node = node;
        this.factA = factA;
    }

    @Override
    public String toString() {
        return "From(" + getFactsString() + ") with " + childTupleList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetFromUniNode<A> getNode() {
        return node;
    }

    @Override
    public List<BavetAbstractTuple> getChildTupleList() {
        return childTupleList;
    }

    @Override
    public A getFactA() {
        return factA;
    }

}
