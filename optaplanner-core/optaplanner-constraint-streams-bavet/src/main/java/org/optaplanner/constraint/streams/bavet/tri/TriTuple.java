/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.tri;

import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class TriTuple<A, B, C> implements Tuple {

    // Only a tuple's origin node may modify a fact.
    public A factA;
    public B factB;
    public C factC;

    public final Object[] store;

    public BavetTupleState state;

    public TriTuple(A factA, B factB, C factC, int storeSize) {
        this.factA = factA;
        this.factB = factB;
        this.factC = factC;
        store = (storeSize <= 0) ? null : new Object[storeSize];
    }

    @Override
    public BavetTupleState getState() {
        return state;
    }

    @Override
    public void setState(BavetTupleState state) {
        this.state = state;
    }

    @Override
    public Object[] getStore() {
        return store;
    }

    @Override
    public String toString() {
        return "{" + factA + ", " + factB + ", " + factC + "}";
    }
}
