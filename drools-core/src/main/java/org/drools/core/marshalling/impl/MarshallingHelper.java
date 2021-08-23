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

package org.drools.core.marshalling.impl;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Tuple;

public class MarshallingHelper {

    public static ActivationKey createActivationKey( String pkgName, String ruleName, Tuple leftTuple) {
        return createActivationKey( pkgName, ruleName, toArrayOfObject(createTupleArray( leftTuple )) );
    }

    public static ActivationKey createActivationKey( String pkgName, String ruleName, Object[] tuple) {
        return new ActivationKey( pkgName, ruleName, tuple );
    }

    public static TupleKey createTupleKey(final Tuple leftTuple) {
        return new TupleKey( createTupleArray( leftTuple ) );
    }

    protected static Object[] toArrayOfObject(long[] longs) {
        Object[] objects = new Object[longs.length];
        for(int i = 0; i < longs.length; i++) {
            objects[i] = longs[i];
        }
        return objects;
    }

    protected static long[] createTupleArray(final Tuple tuple) {
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

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value  & 0xFF) };
    }

    public static final int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
                + ((b[1] & 0xFF) << 16)
                + ((b[2] & 0xFF) << 8)
                + (b[3] & 0xFF);
    }

    // more efficient than instantiating byte buffers and opening streams
    public static final byte[] longToByteArray(long value) {
        return new byte[]{
                (byte) ((value >>> 56) & 0xFF),
                (byte) ((value >>> 48) & 0xFF),
                (byte) ((value >>> 40) & 0xFF),
                (byte) ((value >>> 32) & 0xFF),
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)};
    }

    public static final long byteArrayToLong(byte[] b) {
        return ((((long)b[0]) & 0xFF) << 56)
                + ((((long)b[1]) & 0xFF) << 48)
                + ((((long)b[2]) & 0xFF) << 40)
                + ((((long)b[3]) & 0xFF) << 32)
                + ((((long)b[4]) & 0xFF) << 24)
                + ((((long)b[5]) & 0xFF) << 16)
                + ((((long)b[6]) & 0xFF) << 8)
                + (((long)b[7]) & 0xFF);
    }

    public static boolean hasNodeMemory( TerminalNode terminalNode) {
        return hasNodeMemory( terminalNode.getLeftTupleSource() );
    }

    private static boolean hasNodeMemory( LeftTupleSource leftTupleSource) {
        if (leftTupleSource == null) {
            return false;
        }
        return NodeTypeEnums.hasNodeMemory( leftTupleSource ) ? true : hasNodeMemory(leftTupleSource.getLeftTupleSource());
    }
}
