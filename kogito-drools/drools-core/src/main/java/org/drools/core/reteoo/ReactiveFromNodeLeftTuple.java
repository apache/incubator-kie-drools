/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;

import java.util.Arrays;

import static org.drools.core.phreak.ReactiveObjectUtil.ModificationType.REMOVE;

public class ReactiveFromNodeLeftTuple extends FromNodeLeftTuple {

    private ModificationType modificationType = ModificationType.NONE;

    private final Object[] objects;
    private final int hash;

    public ReactiveFromNodeLeftTuple(final LeftTuple leftTuple,
                                     final RightTuple rightTuple,
                                     final LeftTuple currentLeftChild,
                                     final LeftTuple currentRightChild,
                                     final Sink sink,
                                     final boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);

        objects = new Object[leftTuple.getIndex() + 2];
        System.arraycopy( leftTuple.toObjects(), 0, objects, 0, leftTuple.getIndex() );
        objects[leftTuple.getIndex()+1] = rightTuple.getFactHandle().getObject();
        hash = Arrays.hashCode( objects );
    }

    public ReactiveFromNodeLeftTuple( InternalFactHandle factHandle, LeftTuple leftTuple, Sink sink ) {
        super(factHandle, leftTuple, sink);

        objects = new Object[leftTuple.getIndex() + 2];
        System.arraycopy( leftTuple.toObjects(), 0, objects, 0, leftTuple.getIndex() );
        objects[leftTuple.getIndex()+1] = factHandle.getObject();
        hash = Arrays.hashCode( objects );
    }

    public ReactiveFromNodeLeftTuple( InternalFactHandle factHandle, Sink sink, boolean leftTupleMemoryEnabled ) {
        super( factHandle, sink, leftTupleMemoryEnabled );

        objects = new Object[] { factHandle.getObject() };
        hash = Arrays.hashCode( objects );
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals( Object other ) {
        return other instanceof ReactiveFromNodeLeftTuple && Arrays.equals( objects, ( (ReactiveFromNodeLeftTuple) other ).objects );
    }

    public boolean updateModificationState(ModificationType newState ) {
        switch (modificationType) {
            case NONE:
                modificationType = newState;
                return true;
            case ADD:
                if (newState == REMOVE) {
                    modificationType = ModificationType.NONE;
                }
                break;
            case MODIFY:
                if (newState == REMOVE) {
                    modificationType = REMOVE;
                }
                break;
        }
        return false;
    }

    public void resetModificationState() {
        modificationType = ModificationType.NONE;
    }

    public ModificationType getModificationType() {
        return modificationType;
    }
}
