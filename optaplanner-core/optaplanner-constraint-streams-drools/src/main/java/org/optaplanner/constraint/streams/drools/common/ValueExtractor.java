/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.drools.common;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;

final class ValueExtractor<X> implements Function<BaseTuple, X> {

    private static final UnaryOperator<BaseTuple> TUPLE_EXTRACTOR_OFFSET_1 = BaseTuple::getParent;
    private static final UnaryOperator<BaseTuple> TUPLE_EXTRACTOR_OFFSET_2 = tuple -> tuple.getParent()
            .getParent();
    private static final UnaryOperator<BaseTuple> TUPLE_EXTRACTOR_OFFSET_3 = tuple -> tuple.getParent()
            .getParent()
            .getParent();

    static UnaryOperator<BaseTuple> getTupleExtractor(Declaration declaration, BaseTuple leftTuple) {
        int offset = 0;
        while (leftTuple.getIndex() != declaration.getTupleIndex()) {
            leftTuple = leftTuple.getParent();
            offset++;
        }
        switch (offset) {
            case 0: // The tuple will be accessed directly to avoid a method call on the hot path.
                return null;
            case 1:
                return TUPLE_EXTRACTOR_OFFSET_1;
            case 2:
                return TUPLE_EXTRACTOR_OFFSET_2;
            case 3:
                return TUPLE_EXTRACTOR_OFFSET_3;
            default:
                throw new UnsupportedOperationException("Impossible state: tuple delta offset (" + offset + ").");
        }
    }

    private final Declaration declaration;
    private final UnaryOperator<BaseTuple> tupleExtractor;

    public ValueExtractor(Declaration declaration, BaseTuple leftTuple) {
        this.declaration = Objects.requireNonNull(declaration);
        this.tupleExtractor = getTupleExtractor(declaration, leftTuple);
    }

    @Override
    public X apply(BaseTuple tuple) {
        BaseTuple extractedTuple = tupleExtractor == null ? tuple : tupleExtractor.apply(tuple);
        return (X) declaration.getValue(null, extractedTuple.getFactHandle().getObject());
    }
}
