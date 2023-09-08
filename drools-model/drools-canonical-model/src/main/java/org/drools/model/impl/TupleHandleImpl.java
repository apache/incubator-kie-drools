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
package org.drools.model.impl;

import org.drools.model.Tuple;
import org.drools.model.TupleHandle;
import org.drools.model.Variable;

public class TupleHandleImpl implements TupleHandle {

    private final Tuple parent;
    private final Object object;
    private final int size;
    private Variable variable;

    public TupleHandleImpl(Object object) {
        this(null, object, null);
    }

    public TupleHandleImpl(Object object, Variable variable) {
        this(null, object, variable);
    }

    public TupleHandleImpl(Tuple parent, Object object, Variable variable) {
        this.parent = parent;
        this.object = object;
        this.variable = variable;
        this.size = parent == null ? 1 : parent.size()+1;
    }

    @Override
    public Tuple getParent() {
        return parent;
    }

    @Override
    public <T> T get(Variable<T> variable) {
        if (this.variable != null && this.variable.equals(variable)) {
            return (T) object;
        }
        return parent == null ? null : parent.get(variable);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object getObject() {
        return object;
    }
}
