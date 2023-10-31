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

import org.drools.base.util.FieldIndex;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.Tuple;

public abstract class AbstractTupleIndexTree {
    protected FieldIndex index;

    protected ConstraintTypeOperator constraintType;


    protected int factSize;
    protected boolean left;


    public boolean isIndexed() {
        return true;
    }

    protected Comparable getLeftIndexedValue(Tuple tuple) {
        return getIndexedValue( tuple, left );
    }

    protected Comparable getRightIndexedValue(Tuple tuple) {
        return getIndexedValue( tuple, !left );
    }

    protected Comparable getIndexedValue(Tuple tuple, boolean left) {
        return left ?
               (Comparable) index.getLeftExtractor().getValue( tuple ) :
               (Comparable) index.getRightExtractor().getValue( tuple.getFactHandle().getObject() );
    }

    public static class IndexTupleList extends TupleList<Tuple> {
        private Comparable key;

        public IndexTupleList(Comparable key) {
            this.key = key;
        }

        public Comparable key() {
            return key;
        }
    }
}
