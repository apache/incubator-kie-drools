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

package org.drools.core.spi;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.ObjectTypeConf;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * Factory Interface to return new <code>FactHandle</code>s
 */
public interface FactHandleFactory {
   /**
     * Construct a handle with a new id.
     * 
     * @return The handle.
     */
    public InternalFactHandle newFactHandle(Object object, 
                                            ObjectTypeConf conf, 
                                            InternalWorkingMemory workingMemory, 
                                            EntryPoint wmEntryPoint );
    
    public InternalFactHandle newFactHandle(final int id,
                                            final Object object,
                                            final long recency,
                                            final ObjectTypeConf conf,
                                            final InternalWorkingMemory workingMemory,
                                            final EntryPoint wmEntryPoint);
    
    /**
     * Increases the recency of the FactHandle
     * 
     * @param factHandle
     *      The fact handle to have its recency increased.
     */
    public void increaseFactHandleRecency(InternalFactHandle factHandle);

    public void destroyFactHandle(InternalFactHandle factHandle);

    /**
     * @return a fresh instance of the fact handle factory, with any IDs reset etc.
     */
    public FactHandleFactory newInstance();
    
    public FactHandleFactory newInstance(int id, long counter);

    public Class<?> getFactHandleType();

    public int getId();

    public long getRecency();
    
    public AtomicInteger getAtomicId();
    
    public AtomicLong getAtomicRecency();      
    
    public void clear(int id, long counter);
}
