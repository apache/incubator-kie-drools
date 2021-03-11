/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.index;

import org.drools.model.BetaIndexN;
import org.drools.model.functions.Function1;

public abstract class AbstractBetaIndex<A, V> extends AbstractIndex<A, V> implements BetaIndexN<A, V> {

    private final Class<?> rightReturnType;

    public AbstractBetaIndex(Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor);
        this.rightReturnType = rightReturnType;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.BETA;
    }

    @Override
    public Class<?> getRightReturnType() {
        return rightReturnType;
    }

}
