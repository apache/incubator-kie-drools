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

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.util.Quadruple;

final class Group4Mapping0CollectorTriNode<OldA, OldB, OldC, A, B, C, D>
        extends AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, Quadruple<A, B, C, D>, Void, Void> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMappingA;
    private final TriFunction<OldA, OldB, OldC, B> groupKeyMappingB;
    private final TriFunction<OldA, OldB, OldC, C> groupKeyMappingC;
    private final TriFunction<OldA, OldB, OldC, D> groupKeyMappingD;
    private final int outputStoreSize;

    public Group4Mapping0CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMappingA,
            TriFunction<OldA, OldB, OldC, B> groupKeyMappingB, TriFunction<OldA, OldB, OldC, C> groupKeyMappingC,
            TriFunction<OldA, OldB, OldC, D> groupKeyMappingD, int groupStoreIndex,
            Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesInsert, nextNodesRetract);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.groupKeyMappingD = groupKeyMappingD;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Quadruple<A, B, C, D> createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC);
        C c = groupKeyMappingC.apply(oldA, oldB, oldC);
        D d = groupKeyMappingD.apply(oldA, oldB, oldC);
        return Quadruple.of(a, b, c, d);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Group<QuadTuple<A, B, C, D>, Quadruple<A, B, C, D>, Void> group) {
        Quadruple<A, B, C, D> key = group.groupKey;
        return new QuadTuple<>(key.getA(), key.getB(), key.getC(), key.getD(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupTriNode 4+0";
    }

}
