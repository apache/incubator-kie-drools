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
package org.drools.core.marshalling;

import java.util.Arrays;

import org.drools.core.common.BaseNode;
import org.drools.core.common.SuperCacheFixer;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.TupleImpl ;

public class TupleKey {
    private final long[] tuple;

    public TupleKey(long[] tuple) {
        super();
        this.tuple = tuple;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( tuple );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        TupleKey other = (TupleKey) obj;
        if ( !Arrays.equals( tuple, other.tuple ) ) {
            return false;
        }
        return true;
    }

    public static TupleKey createTupleKey(final TupleImpl  leftTuple) {
        return new TupleKey( createTupleArray( leftTuple ) );
    }

    public static long[] createTupleArray(final TupleImpl  tuple) {
        if( tuple != null ) {
            long[] tupleArray = new long[SuperCacheFixer.getLeftTupleSource(tuple).getObjectCount()];
            // tuple iterations happens backwards
            int i = tupleArray.length-1;
            for( TupleImpl  entry = tuple.skipEmptyHandles(); entry != null; entry = entry.getParent() ) {
                tupleArray[i--] = entry.getFactHandle().getId();
            }
            return tupleArray;
        } else {
            return new long[0];
        }
    }
}
