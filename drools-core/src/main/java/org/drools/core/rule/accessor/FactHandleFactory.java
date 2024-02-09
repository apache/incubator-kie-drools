/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.rule.accessor;

import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
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
                                     ReteEvaluator reteEvaluator,
                                     WorkingMemoryEntryPoint wmEntryPoint );
    
    InternalFactHandle newFactHandle(long id,
                                     Object object,
                                     long recency,
                                     ObjectTypeConf conf,
                                     ReteEvaluator reteEvaluator,
                                     WorkingMemoryEntryPoint wmEntryPoint );

    InternalFactHandle newInitialFactHandle(WorkingMemoryEntryPoint wmEntryPoint);
    
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
    
    FactHandleFactory newInstance(long id, long counter);

    Class<?> getFactHandleType();

    long getId();

    long getRecency();

    long getNextId();

    long getNextRecency();
    
    void clear(long id, long counter);

    void doRecycleIds( Collection<Long> usedIds );
    void stopRecycleIds();

    DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint);

    DefaultEventHandle createEventFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, long timestamp, long duration);
}
