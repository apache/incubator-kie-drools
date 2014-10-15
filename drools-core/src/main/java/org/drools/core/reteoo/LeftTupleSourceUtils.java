package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

public class LeftTupleSourceUtils {
    public static void doModifyLeftTuple(InternalFactHandle factHandle,
                                         ModifyPreviousTuples modifyPreviousTuples,
                                         PropagationContext context,
                                         InternalWorkingMemory workingMemory,
                                         LeftTupleSink sink,
                                         ObjectTypeNode.Id leftInputOtnId,
                                         BitMask leftInferredMask) {
        LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
        while ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() != null &&
                leftTuple.getLeftTupleSink().getLeftInputOtnId().before( leftInputOtnId ) ) {
            modifyPreviousTuples.removeLeftTuple();

            // we skipped this node, due to alpha hashing, so retract now
            ((LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource()).retractLeftTuple( leftTuple,
                                                                                                         context,
                                                                                                         workingMemory );

            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }

        if ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() != null &&
             leftTuple.getLeftTupleSink().getLeftInputOtnId().equals( leftInputOtnId ) ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.reAdd();
            if ( context.getModificationMask().intersects( leftInferredMask ) ) {
                // LeftTuple previously existed, so continue as modify, unless it's currently staged
                sink.modifyLeftTuple( leftTuple,
                                      context,
                                      workingMemory );
            }
        } else {
            if ( context.getModificationMask().intersects( leftInferredMask ) ) {
                // LeftTuple does not exist, so create and continue as assert
                LeftTuple newLeftTuple = sink.createLeftTuple( factHandle,
                                                               sink,
                                                               true );

                sink.assertLeftTuple( newLeftTuple,
                                      context,
                                      workingMemory );
            }
        }
    }
}
