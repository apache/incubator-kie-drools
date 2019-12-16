/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.index;

import org.drools.model.BetaIndex;
import org.drools.model.functions.Function1;

public class BetaIndexImpl<A, B, V> extends AbstractIndex<A, V> implements BetaIndex<A, B, V> {

    private final Function1<B, V> rightOperandExtractor;

    public BetaIndexImpl(Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function1<B, V> rightOperandExtractor) {
        super( indexedClass, constraintType, indexId, leftOperandExtractor );
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.BETA;
    }

    @Override
    public Function1<B, V> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }

}
