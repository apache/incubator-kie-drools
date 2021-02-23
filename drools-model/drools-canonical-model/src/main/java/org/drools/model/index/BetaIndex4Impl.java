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

import org.drools.model.BetaIndex4;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;

public class BetaIndex4Impl<A, B, C, D, E, V> extends AbstractBetaIndex<A, V> implements BetaIndex4<A, B, C, D, E, V> {

    private final Function4<B, C, D, E, ?> rightOperandExtractor;

    public BetaIndex4Impl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor, rightReturnType);
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public Function4<B, C, D, E, ?> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }
}
