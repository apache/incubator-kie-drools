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
package org.drools.core.util.index;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.SingleLinkedEntry;

import java.io.Serializable;

public class TupleListWithContext<C> extends TupleList {

    public static final long        serialVersionUID = 510l;

    private C                       context;

    public TupleListWithContext() {
    }

    public TupleListWithContext(C c) {
        this.context = c;
    }

    public TupleListWithContext(TupleImpl first, TupleImpl last, int size) {
        super(first, last, size);
    }

    public C getContext() {
        return context;
    }

    public void setContext(C context) {
        this.context = context;
    }

    @Override
    public void clear() {
        super.clear();
        context = null;
    }

    protected void copyStateInto(TupleListWithContext<C> other) {
        super.copyStateInto(other);
        other.context = this.context;
    }
}
