/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EmptyObjectSinkAdapter implements ObjectSinkPropagator {

    private static final long                   serialVersionUID = 510l;

    private static final EmptyObjectSinkAdapter INSTANCE = new EmptyObjectSinkAdapter();

    private static final ObjectSink[]           SINK_LIST        = new ObjectSink[0];

    public static EmptyObjectSinkAdapter getInstance() {
        return INSTANCE;
    }

    public ObjectSinkPropagator addObjectSink(final ObjectSink sink, int alphaNodeHashingThreshold) {
        return new SingleObjectSinkAdapter( sink );
    }

    public ObjectSinkPropagator removeObjectSink(final ObjectSink sink) {
        throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void propagateAssertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {

    }

    public void propagateRetractObject(final InternalFactHandle handle,
                                       final PropagationContext context,
                                       final InternalWorkingMemory workingMemory,
                                       final boolean useHash) {
    }
    
    public void propagateModifyObject(InternalFactHandle factHandle,
                                      ModifyPreviousTuples modifyPreviousTuples,
                                      PropagationContext context,
                                      InternalWorkingMemory workingMemory) {

    }
    
    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        
    }

    public BaseNode getMatchingNode(BaseNode candidate) {
        return null;
    }

    public ObjectSink[] getSinks() {
        return SINK_LIST;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean equals(Object obj) {
        return obj instanceof EmptyObjectSinkAdapter;
    }

    public void doLinkRiaNode(InternalWorkingMemory wm) {
        // TODO Auto-generated method stub
        
    }

    public void doUnlinkRiaNode(InternalWorkingMemory wm) {
        // TODO Auto-generated method stub
        
    }



}
