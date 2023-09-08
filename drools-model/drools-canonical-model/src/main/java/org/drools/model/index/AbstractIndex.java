/**
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
package org.drools.model.index;

import org.drools.model.Index;
import org.drools.model.functions.Function1;

public abstract class AbstractIndex<A, V> implements Index<A, V> {

    private final Class<V> indexedClass;
    private ConstraintType constraintType;
    private final int indexId;
    private final Function1<A, V> leftOperandExtractor;

    protected AbstractIndex( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor ) {
        this.indexedClass = indexedClass;
        this.constraintType = constraintType;
        this.indexId = indexId;
        this.leftOperandExtractor = leftOperandExtractor;
    }

    @Override
    public Class<V> getIndexedClass() {
        return indexedClass;
    }

    @Override
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    @Override
    public int getIndexId() {
        return indexId;
    }

    @Override
    public Function1<A, V> getLeftOperandExtractor() {
        return leftOperandExtractor;
    }

    @Override
    public Index<A, V> negate() {
        this.constraintType = this.constraintType.negate();
        return this;
    }
}
