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

package org.drools.core.common;

import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.FactHandleFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractFactHandleFactory
    implements
    FactHandleFactory  {

    /** The fact id. */
    private AtomicInteger              id;

    /** The number of facts created - used for recency. */
    private AtomicLong                 counter;
    
    public AbstractFactHandleFactory() {
        // starts at 0. So first assigned is 1.
        // 0 is hard coded to Initialfact
        this.id = new AtomicInteger(0);
        this.counter = new AtomicLong(0);
    }
    
    public AbstractFactHandleFactory(int id, long counter) {
        this.id = new AtomicInteger( id );
        this.counter = new AtomicLong( counter );
    }

    /* (non-Javadoc)
    * @see org.kie.reteoo.FactHandleFactory#newFactHandle()
    */
    public final InternalFactHandle newFactHandle(Object object,
                                                  ObjectTypeConf conf,
                                                  InternalWorkingMemory workingMemory,
                                                  InternalWorkingMemoryEntryPoint wmEntryPoint) {
        return newFactHandle( this.id.incrementAndGet(),
                              object,
                              conf,
                              workingMemory,
                              wmEntryPoint );
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public final InternalFactHandle newFactHandle(int id,
                                                  Object object,
                                                  ObjectTypeConf conf,
                                                  InternalWorkingMemory workingMemory,
                                                  InternalWorkingMemoryEntryPoint wmEntryPoint) {
        return newFactHandle( id,
                              object,
                              this.counter.incrementAndGet(),
                              conf,
                              workingMemory,
                              wmEntryPoint );
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public abstract InternalFactHandle newFactHandle(int id,
                                                     Object object,
                                                     long recency,
                                                     ObjectTypeConf conf,
                                                     InternalWorkingMemory workingMemory,
                                                     InternalWorkingMemoryEntryPoint wmEntryPoint);

    public final void increaseFactHandleRecency(final InternalFactHandle factHandle) {
        factHandle.setRecency( this.counter.incrementAndGet() );
    }

    public void destroyFactHandle(final InternalFactHandle factHandle) {
        factHandle.invalidate();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newInstance()
     */
    public abstract FactHandleFactory newInstance();
    
    public AtomicInteger getAtomicId() {
        return this.id;
    }
    
    public AtomicLong getAtomicRecency() {
        return this.counter;
    }    
    
    public int getId() {
        return this.id.get();
    }

    public long getRecency() {
        return this.counter.get();
    }
    
    public void clear(int id, long counter) {
        this.id = new AtomicInteger( id );
        this.counter = new AtomicLong( counter );
    }

    public void reset() {
        id.set(0);
        counter.set(0);
    }
}
