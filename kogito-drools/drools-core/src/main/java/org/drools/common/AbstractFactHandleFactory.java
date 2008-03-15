package org.drools.common;

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

import org.drools.WorkingMemory;
import org.drools.spi.FactHandleFactory;
import org.drools.util.PrimitiveLongStack;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public abstract class AbstractFactHandleFactory
    implements
    FactHandleFactory, Externalizable {

    /**
     *
     */
    private static final long          serialVersionUID = 400L;

    protected PrimitiveLongStack factHandlePool   = new PrimitiveLongStack();

    /** The fact id. */
    private long                       id;

    /** The number of facts created - used for recency. */
    private long                       counter;


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id  = in.readLong();
        counter = in.readLong();
        factHandlePool  = (PrimitiveLongStack)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(id);
        out.writeLong(counter);
        out.writeObject(factHandlePool);
    }

    /* (non-Javadoc)
    * @see org.drools.reteoo.FactHandleFactory#newFactHandle()
    */
    public final InternalFactHandle newFactHandle( final Object object, final boolean isEvent, final WorkingMemory workingMemory ) {
        if ( !this.factHandlePool.isEmpty() ) {
            return newFactHandle( this.factHandlePool.pop(),
                                  object,
                                  isEvent,
                                  0,
                                  workingMemory );
        }

        return newFactHandle( this.id++,
                              object,
                              isEvent,
                              0,
                              workingMemory );
    }

    /* (non-Javadoc)
    * @see org.drools.reteoo.FactHandleFactory#newFactHandle()
    */
    public final InternalFactHandle newFactHandle( final Object object, final boolean isEvent, long duration, final WorkingMemory workingMemory ) {
        if ( !this.factHandlePool.isEmpty() ) {
            return newFactHandle( this.factHandlePool.pop(),
                                  object,
                                  isEvent,
                                  duration,
                                  workingMemory );
        }

        return newFactHandle( this.id++,
                              object,
                              isEvent,
                              duration,
                              workingMemory );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    protected final InternalFactHandle newFactHandle(final long id,
                                                     final Object object,
                                                     final boolean isEvent,
                                                     final WorkingMemory workingMemory ) {
        return newFactHandle( id,
                              object,
                              this.counter++,
                              isEvent,
                              0,
                              workingMemory );
    }

    /* (non-Javadoc)
    * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
    */
    protected final InternalFactHandle newFactHandle(final long id,
                                                     final Object object,
                                                     final boolean isEvent,
                                                     final long duration,
                                                     final WorkingMemory workingMemory ) {
        return newFactHandle( id,
                              object,
                              this.counter++,
                              isEvent,
                              duration,
                              workingMemory );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    protected abstract InternalFactHandle newFactHandle(final long id,
                                                        final Object object,
                                                        final long recency,
                                                        final boolean isEvent,
                                                        final WorkingMemory workingMemory );

    /* (non-Javadoc)
    * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
    */
    protected abstract InternalFactHandle newFactHandle(final long id,
                                                        final Object object,
                                                        final long recency,
                                                        final boolean isEvent,
                                                        final long duration,
                                                        final WorkingMemory workingMemory );

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#increaseFactHandleRecency(org.drools.FactHandle)
     */
    public final void increaseFactHandleRecency(final InternalFactHandle factHandle) {
        factHandle.setRecency( this.counter++ );
    }

    public void destroyFactHandle(final InternalFactHandle factHandle) {
        this.factHandlePool.push( factHandle.getId() );
        factHandle.invalidate();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public abstract FactHandleFactory newInstance();
}
