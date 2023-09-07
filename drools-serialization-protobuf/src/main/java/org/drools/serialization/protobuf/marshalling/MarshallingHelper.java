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

package org.drools.serialization.protobuf.marshalling;

import org.drools.core.reteoo.LeftTupleSource;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;

import static org.drools.core.marshalling.TupleKey.createTupleArray;

public class MarshallingHelper {

    public static ActivationKey createActivationKey( String pkgName, String ruleName, Tuple leftTuple) {
        return createActivationKey( pkgName, ruleName, toArrayOfObject(createTupleArray( leftTuple )) );
    }

    public static ActivationKey createActivationKey( String pkgName, String ruleName, Object[] tuple) {
        return new ActivationKey( pkgName, ruleName, tuple );
    }

    public static ActivationKey createActivationKey( String pkgName, String ruleName) {
        return new ActivationKey( pkgName, ruleName, new Object[0] );
    }

    protected static Object[] toArrayOfObject(long[] longs) {
        Object[] objects = new Object[longs.length];
        for(int i = 0; i < longs.length; i++) {
            objects[i] = longs[i];
        }
        return objects;
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
