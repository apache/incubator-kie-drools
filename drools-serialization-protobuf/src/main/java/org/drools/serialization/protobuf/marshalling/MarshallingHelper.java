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
package org.drools.serialization.protobuf.marshalling;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.TupleImpl ;

import static org.drools.base.reteoo.NodeTypeEnums.AccumulateNode;
import static org.drools.base.reteoo.NodeTypeEnums.FromNode;
import static org.drools.base.reteoo.NodeTypeEnums.ReactiveFromNode;
import static org.drools.core.marshalling.TupleKey.createTupleArray;

public class MarshallingHelper {

    public static ActivationKey createActivationKey( String pkgName, String ruleName, TupleImpl  leftTuple) {
        return createActivationKey( pkgName, ruleName, toArrayOfObject(createTupleArray( leftTuple )) );
    }

    public static ActivationKey createActivationKey( String pkgName, String ruleName, Object[] tuple) {
        return new ActivationKey( pkgName, ruleName, tuple );
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
        return nodeWithMemory( leftTupleSource ) || hasNodeMemory(leftTupleSource.getLeftTupleSource());
    }

    private static boolean nodeWithMemory(NetworkNode node) {
        return node.getType() == FromNode || node.getType() == ReactiveFromNode || node.getType() == AccumulateNode;
    }
}
