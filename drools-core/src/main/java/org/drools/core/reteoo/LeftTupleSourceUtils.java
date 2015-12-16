/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
        while ( leftTuple != null && leftTuple.getTupleSink().getLeftInputOtnId() != null &&
                leftTuple.getTupleSink().getLeftInputOtnId().before( leftInputOtnId ) ) {
            modifyPreviousTuples.removeLeftTuple();

            // we skipped this node, due to alpha hashing, so retract now
            ((LeftInputAdapterNode) leftTuple.getTupleSink().getLeftTupleSource()).retractLeftTuple( leftTuple,
                                                                                                         context,
                                                                                                         workingMemory );

            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }

        if ( leftTuple != null && leftTuple.getTupleSink().getLeftInputOtnId() != null &&
             leftTuple.getTupleSink().getLeftInputOtnId().equals( leftInputOtnId ) ) {
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
