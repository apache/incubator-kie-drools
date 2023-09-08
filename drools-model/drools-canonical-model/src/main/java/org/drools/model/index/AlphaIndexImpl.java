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

import org.drools.model.AlphaIndex;
import org.drools.model.functions.Function1;

public class AlphaIndexImpl<A, V> extends AbstractIndex<A, V> implements AlphaIndex<A, V> {

    private final V rightValue;

    public AlphaIndexImpl(Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, V rightValue) {
        super( indexedClass, constraintType, indexId, leftOperandExtractor );
        this.rightValue = rightValue;
    }

    @Override
    public V getRightValue() {
        return rightValue;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.ALPHA;
    }

    @Override
    public String toString() {
        return "AlphaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: " + rightValue + ")";
    }
}
