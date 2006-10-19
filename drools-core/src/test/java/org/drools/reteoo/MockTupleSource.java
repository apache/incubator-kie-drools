package org.drools.reteoo;

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

import java.util.Collections;
import java.util.List;

import org.drools.common.BaseNode;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.PropagationContext;

public class MockTupleSource extends TupleSource {

    /**
     * 
     */
    private static final long serialVersionUID = -2831490656596388807L;

    private int               attached;

    private int               updated;

    public MockTupleSource(final int id) {
        super( id );
    }

    public void attach() {
        this.attached++;
    }

    public int getAttached() {
        return this.attached;
    }

    public int getUdated() {
        return this.updated;
    }

    public void updateSink(final TupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        this.updated++;
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub

    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        // TODO Auto-generated method stub

    }

    public List getPropagatedTuples(final ReteooWorkingMemory workingMemory,
                                    final TupleSink sink) {
        // TODO Auto-generated method stub
        return Collections.EMPTY_LIST;
    }

}