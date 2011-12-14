/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.WindowNode.WindowMemory;
import org.drools.reteoo.WindowTupleList;
import org.drools.spi.PropagationContext;

/**
 * A length window behavior implementation
 */
public class SlidingLengthWindow
    implements
    Externalizable,
    Behavior {

    private int size;

    public SlidingLengthWindow() {
        this( 0 );
    }

    /**
     * @param size
     */
    public SlidingLengthWindow(final int size) {
        super();
        this.size = size;
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(final ObjectInput in) throws IOException,
                                                  ClassNotFoundException {
        this.size = in.readInt();
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt( this.size );

    }

    public BehaviorType getType() {
        return BehaviorType.LENGTH_WINDOW;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(final int size) {
        this.size = size;
    }

    public Object createContext() {
        return new SlidingLengthWindowContext( this.size );
    }

    /**
     * @inheritDoc
     */
    public boolean assertFact(final WindowMemory memory,
                              final Object context,
                              final InternalFactHandle handle,
                              final InternalWorkingMemory workingMemory) {
        SlidingLengthWindowContext window = (SlidingLengthWindowContext) context;
        window.pos = (window.pos + 1) % window.handles.length;
        if ( window.handles[window.pos] != null ) {
            final EventFactHandle previous = window.handles[window.pos];
            // retract previous
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.EXPIRATION,
                                                                                      null,
                                                                                      null,
                                                                                      previous );
            WindowTupleList list = (WindowTupleList) memory.events.get( previous );
            for( RightTuple tuple = list.getFirstWindowTuple(); tuple != null; tuple = list.getFirstWindowTuple() ) {
                tuple.getRightTupleSink().retractRightTuple( tuple,
                                                             propagationContext,
                                                             workingMemory );
                propagationContext.evaluateActionQueue( workingMemory );
                tuple.unlinkFromRightParent();
            }
        }
        window.handles[window.pos] = (EventFactHandle) handle;
        return true;
    }

    /**
     * @inheritDoc
     *
     * @see org.drools.rule.Behavior#retractRightTuple(java.lang.Object, org.drools.reteoo.RightTuple, org.drools.common.InternalWorkingMemory)
     */
    public void retractFact(final WindowMemory memory,
                            final Object context,
                            final InternalFactHandle handle,
                            final InternalWorkingMemory workingMemory) {
        SlidingLengthWindowContext window = (SlidingLengthWindowContext) context;
        final int last = (window.pos == 0) ? window.handles.length - 1 : window.pos - 1;
        // we start the loop on current pos because the most common scenario is to retract the
        // right tuple referenced by the current "pos" position, causing this loop to only execute
        // the first iteration
        for ( int i = window.pos; i != last; i = (i + 1) % window.handles.length ) {
            if ( window.handles[i] == handle ) {
                window.handles[i] = null;
                break;
            }
        }
    }

    public void expireFacts(final WindowMemory memory,
                            final Object context,
                            final InternalWorkingMemory workingMemory) {
        // do nothing?
    }

    /**
     * Length windows don't change expiration offset, so
     * always return -1
     */
    public long getExpirationOffset() {
        return -1;
    }

    public String toString() {
        return "SlidingLengthWindow( size=" + size + " )";
    }

    /**
     * A Context object for length windows
     */
    public static class SlidingLengthWindowContext
        implements
        Externalizable {

        public EventFactHandle[] handles;
        public int               pos = 0;

        public SlidingLengthWindowContext(final int size) {
            this.handles = new EventFactHandle[size];
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.pos = in.readInt();
            this.handles = (EventFactHandle[]) in.readObject();

        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt( this.pos );
            out.writeObject( this.handles );
        }
    }

}
