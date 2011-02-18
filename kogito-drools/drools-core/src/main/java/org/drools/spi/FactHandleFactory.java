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

package org.drools.spi;

import org.drools.FactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 * Factory Interface to return new <code>FactHandle</code>s
 * 
 *  @see FactHandle
 */
public interface FactHandleFactory {
   /**
     * Construct a handle with a new id.
     * 
     * @return The handle.
     */
    public InternalFactHandle newFactHandle(Object object, ObjectTypeConf conf, InternalWorkingMemory workingMemory, WorkingMemoryEntryPoint wmEntryPoint );
    
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
    
    public void clear(int id, long counter);
}
