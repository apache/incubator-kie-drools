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
package org.drools.core.reteoo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.phreak.ReactiveObjectUtil.ModificationType;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.core.phreak.ReactiveObjectUtil.ModificationType.REMOVE;

public class ReactiveFromNodeLeftTuple extends LeftTuple {

    private Map<Object, ModificationType> modificationTypeMap = new HashMap<>(); 

    private Object[] objects;
    private int hash;
    private int peerIndex;

    public ReactiveFromNodeLeftTuple() {
        // constructor needed for serialisation
    }

    public ReactiveFromNodeLeftTuple(final TupleImpl leftTuple,
                                     final TupleImpl rightTuple,
                                     final TupleImpl currentLeftChild,
                                     final TupleImpl currentRightChild,
                                     final Sink sink,
                                     final boolean leftTupleMemoryEnabled) {
        super(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
        storeTupleObjects(leftTuple, rightTuple.getFactHandle());
    }


    public ReactiveFromNodeLeftTuple(InternalFactHandle factHandle, TupleImpl leftTuple, Sink sink ) {
        super(factHandle, leftTuple, sink);
        storeTupleObjects(leftTuple, factHandle);
    }

    public ReactiveFromNodeLeftTuple(FactHandle factHandle, Sink sink, boolean leftTupleMemoryEnabled) {
        super( (InternalFactHandle) factHandle, sink, leftTupleMemoryEnabled );
        objects = new Object[] { factHandle.getObject() };
        hash = Arrays.hashCode( objects );
    }

    private void storeTupleObjects(TupleImpl leftTuple, FactHandle factHandle) {
        Object[] leftObjects = leftTuple.toObjects();
        // left tuple size + 1 for the right object
        objects = new Object[leftObjects.length + 1];
        System.arraycopy( leftObjects, 0, objects, 0, leftObjects.length );
        objects[leftObjects.length] = factHandle.getObject();
        hash = Arrays.hashCode( objects );
    }

    @Override
    public void initPeer(TupleImpl original, Sink sink) {
        super.initPeer( original, sink );
        if ( original instanceof ReactiveFromNodeLeftTuple ) {
            ReactiveFromNodeLeftTuple reactiveTuple = ( (ReactiveFromNodeLeftTuple) original );
            objects = reactiveTuple.objects;
            peerIndex = reactiveTuple.peerIndex + 1;
            hash = Arrays.hashCode( objects ) + peerIndex;
        }
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals( Object other ) {
        return other instanceof ReactiveFromNodeLeftTuple &&
               Arrays.equals( objects, ( (ReactiveFromNodeLeftTuple) other ).objects ) &&
               peerIndex == ( (ReactiveFromNodeLeftTuple) other ).peerIndex;
    }

    public boolean updateModificationState(Object object, ModificationType newState ) {
        ModificationType modificationType = modificationTypeMap.computeIfAbsent(object, (k) -> ModificationType.NONE);
        switch ( modificationType ) {
            case NONE:
                modificationType = newState;
                modificationTypeMap.put(object, modificationType);
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
        modificationTypeMap.put(object, modificationType);
        return false;
    }

    public ModificationType resetModificationState(Object object) {
        return modificationTypeMap.remove(object);
    }
}
