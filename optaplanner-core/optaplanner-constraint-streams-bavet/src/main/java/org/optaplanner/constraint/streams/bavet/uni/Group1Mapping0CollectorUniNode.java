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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Consumer;
import java.util.function.Function;

final class Group1Mapping0CollectorUniNode<OldA, A>
        extends AbstractGroupUniNode<OldA, UniTuple<A>, A, Void, Void> {

    private final Function<OldA, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping0CollectorUniNode(Function<OldA, A> groupKeyMapping, int groupStoreIndex,
            Consumer<UniTuple<A>> nextNodesInsert, Consumer<UniTuple<A>> nextNodesUpdate,
            Consumer<UniTuple<A>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesInsert, nextNodesUpdate, nextNodesRetract);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(UniTuple<OldA> tuple) {
        return groupKeyMapping.apply(tuple.factA);
    }

    @Override
    protected UniTuple<A> createOutTuple(A a) {
        return new UniTuple<>(a, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(UniTuple<A> aUniTuple, Void unused) {
        throw new IllegalStateException("Impossible state: collector is null.");
    }

    @Override
    public String toString() {
        return "GroupUniNode 1+0";
    }

}
