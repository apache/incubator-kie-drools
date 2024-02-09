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
package org.drools.base.util;

import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.accessor.TupleValueExtractor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
public class IndexedValueReader implements Externalizable {
    private static final long serialVersionUID = 510l;

    private TupleValueExtractor leftExtractor;
    private TupleValueExtractor rightExtractor;
    private boolean requiresCoercion;

    public IndexedValueReader() {
    }

    public IndexedValueReader(TupleValueExtractor leftExtractor, TupleValueExtractor rightExtractor) {
        this.leftExtractor = leftExtractor;
        this.rightExtractor = rightExtractor;
        this.requiresCoercion = isCoercionRequired(rightExtractor, leftExtractor);
    }

    private boolean isCoercionRequired(TupleValueExtractor extractor, TupleValueExtractor declaration) {
        return extractor.getValueType() != declaration.getValueType();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        rightExtractor = (TupleValueExtractor) in.readObject();
        leftExtractor = (TupleValueExtractor) in.readObject();
        requiresCoercion = isCoercionRequired(rightExtractor, leftExtractor);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rightExtractor);
        out.writeObject(leftExtractor);
    }

    public TupleValueExtractor getLeftExtractor() {
        return this.leftExtractor;
    }

    public TupleValueExtractor getRightExtractor() {
        return this.rightExtractor;
    }

    public boolean requiresCoercion() {
        return requiresCoercion;
    }

    public Object indexedValueOf(BaseTuple tuple, boolean left) {
        return left ?
                (requiresCoercion ?
                        rightExtractor.getValueType().coerce(leftExtractor.getValue(tuple)) :
                        leftExtractor.getValue(tuple)) :
                rightExtractor.getValue(tuple);
    }
}
