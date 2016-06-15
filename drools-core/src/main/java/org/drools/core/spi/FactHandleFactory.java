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

package org.drools.core.spi;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.ObjectTypeConf;

/**
 * Factory Interface to return new <code>FactHandle</code>s
 */
public interface FactHandleFactory {
   /**
     * Construct a handle with a new id.
     * 
     * @return The handle.
     */
    InternalFactHandle newFactHandle(Object object,
                                     ObjectTypeConf conf,
                                     InternalWorkingMemory workingMemory,
                                     WorkingMemoryEntryPoint wmEntryPoint );
    
    InternalFactHandle newFactHandle(int id,
                                     Object object,
                                     long recency,
                                     ObjectTypeConf conf,
                                     InternalWorkingMemory workingMemory,
                                     WorkingMemoryEntryPoint wmEntryPoint );
    
    /**
     * Increases the recency of the FactHandle
     * 
     * @param factHandle
     *      The fact handle to have its recency increased.
     */
    void increaseFactHandleRecency(InternalFactHandle factHandle);

    void destroyFactHandle(InternalFactHandle factHandle);

    /**
     * @return a fresh instance of the fact handle factory, with any IDs reset etc.
     */
    FactHandleFactory newInstance();
    
    FactHandleFactory newInstance(int id, long counter);

    Class<?> getFactHandleType();

    int getId();

    long getRecency();

    int getNextId();

    long getNextRecency();
    
    void clear(int id, long counter);
}
