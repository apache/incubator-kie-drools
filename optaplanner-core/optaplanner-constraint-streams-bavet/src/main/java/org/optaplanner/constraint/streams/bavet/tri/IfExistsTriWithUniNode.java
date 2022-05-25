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

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.function.TriFunction;

final class IfExistsTriWithUniNode<A, B, C, D> extends AbstractIfExistsNode<TriTuple<A, B, C>, D> {

    private final TriFunction<A, B, C, IndexProperties> mappingABC;
    private final QuadPredicate<A, B, C, D> filtering;

    public IfExistsTriWithUniNode(boolean shouldExist,
            TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
            int inputStoreIndexABC, int inputStoreIndexD,
            Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
            Indexer<TriTuple<A, B, C>, Counter<TriTuple<A, B, C>>> indexerABC,
            Indexer<UniTuple<D>, Set<Counter<TriTuple<A, B, C>>>> indexerD,
            QuadPredicate<A, B, C, D> filtering) {
        super(shouldExist, mappingD, inputStoreIndexABC, inputStoreIndexD, nextNodesInsert, nextNodesRetract, indexerABC,
                indexerD);
        this.mappingABC = mappingABC;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(TriTuple<A, B, C> abcTriTuple) {
        return mappingABC.apply(abcTriTuple.factA, abcTriTuple.factB, abcTriTuple.factC);
    }

    @Override
    protected boolean isFiltering() {
        return filtering != null;
    }

    @Override
    protected boolean isFiltered(TriTuple<A, B, C> abcTriTuple, UniTuple<D> rightTuple) {
        return filtering.test(abcTriTuple.factA, abcTriTuple.factB, abcTriTuple.factC, rightTuple.factA);
    }

    @Override
    public String toString() {
        return "IfExistsTriWithUniNode";
    }

}
