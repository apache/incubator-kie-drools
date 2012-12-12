/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.UpdateContext;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;

public class MockTupleSource extends LeftTupleSource {

    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    public MockTupleSource(final int id) {
        super( id, RuleBasePartitionId.MAIN_PARTITION, false );
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

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
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
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }

}
