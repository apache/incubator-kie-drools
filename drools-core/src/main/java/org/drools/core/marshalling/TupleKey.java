/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.marshalling;

import java.util.Arrays;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.Tuple;

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
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TupleKey other = (TupleKey) obj;
        if ( !Arrays.equals( tuple, other.tuple ) ) return false;
        return true;
    }

    public static TupleKey createTupleKey(final Tuple leftTuple) {
        return new TupleKey( createTupleArray( leftTuple ) );
    }

    public static long[] createTupleArray(final Tuple tuple) {
        if( tuple != null ) {
            LeftTuple leftTuple = (LeftTuple) tuple;
            long[] tupleArray = new long[((LeftTupleNode)leftTuple.getTupleSink()).getLeftTupleSource().getObjectCount()];
            // tuple iterations happens backwards
            int i = tupleArray.length-1;
            for( Tuple entry = leftTuple.skipEmptyHandles(); entry != null; entry = entry.getParent() ) {
                tupleArray[i--] = entry.getFactHandle().getId();
            }
            return tupleArray;
        } else {
            return new long[0];
        }
    }
}
