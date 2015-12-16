/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;

public class MockTupleSource extends LeftTupleSource {

    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    public MockTupleSource(final int id) {
        super( id, null );
    }

    public void attach() {
        this.attached++;
    }

    public int getAttached() {
        return this.sink.getSinks().length;
    }

    public int getUdated() {
        return this.updated;
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        this.updated++;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        return true;
    }

    public void attach( BuildContext context ) {
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
    }

    public short getType() {
        return 0;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return null;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        return true;
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }

}
