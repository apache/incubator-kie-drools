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
        return new ActivationKey( pkgName, ruleName, null );
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
